/**
 * Copyright (C) the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ninja.application.ApplicationRoutes;
import ninja.logging.LogbackConfigurator;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.util.Modules;

import ninja.conf.FrameworkModule;
import ninja.conf.NinjaBaseModule;
import ninja.conf.NinjaClassicModule;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.SwissKnife;

/**
 * Bootstrap for a Ninja application.  Assists with initializing logging,
 * configuring Guice injector, applying user-defined Guice modules/bindings,
 * creates the injector, and compiles the routes.
 * 
 * Subclasses will likely want to provide an inherited configure() method that
 * adds modules specific to the subclassed Bootstrap.  See ninja-servlet
 * and NinjaServletContext for an example of a subclass.
 */
public class Bootstrap {
    static private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    static public final String APPLICATION_GUICE_MODULE_CONVENTION_LOCATION = "conf.Module";
    static public final String APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION = "conf.ServletModule";
    static public final String ROUTES_CONVENTION_LOCATION = "conf.Routes";
    static public final String NINJA_CONVENTION_LOCATION = "conf.Ninja";

    private final NinjaPropertiesImpl ninjaProperties;
    private final Optional<com.google.inject.Module> overrideModuleOpt;
    protected final NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;
    private final List<Module> modulesToLoad;
    private final Optional<String> applicationModulesBasePackage;
    private Injector injector = null;
    
    public Bootstrap(NinjaPropertiesImpl ninjaProperties) {
        this(ninjaProperties, Optional.empty());
    }

    public Bootstrap(NinjaPropertiesImpl ninjaProperties, Optional<com.google.inject.Module> overrideModuleOpt) {
        Preconditions.checkNotNull(ninjaProperties);
        Preconditions.checkNotNull(overrideModuleOpt);
        this.ninjaProperties = ninjaProperties;
        this.overrideModuleOpt = overrideModuleOpt;
        
        this.modulesToLoad =  new ArrayList<>();
        
        // custom base package for application modules (e.g. com.example.conf.Routes)
        this.applicationModulesBasePackage
                = Optional.ofNullable(ninjaProperties.get(
                        NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE));
        
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties); 
    }

    public synchronized void boot() {
        // 1. initialize logging
        initLogback();

        if (this.injector != null) {
            throw new RuntimeException("Bootstrap already booted");
        }

        // 2. configure all modules for injector
        try {
            configure();
        } catch (Exception e) {
            throw new RuntimeException("Unable to configure Ninja", e);
        }
        
        // 3. create injector
        long startTime = System.currentTimeMillis();

        try {
            initInjector();
        } catch (Exception e) {
            throw new RuntimeException("Ninja injector cannot be generated. Please check log for further errors.", e);
        }
        
        long injectorStartupTime = System.currentTimeMillis() - startTime;
        logger.info("Ninja injector started in " + injectorStartupTime + " ms.");
        
        
        // 4. initialize routes
        try {
            initRoutes();
        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize Ninja routes", e);
        }
        
        // 5. framework started!
        Ninja ninja = injector.getInstance(Ninja.class);
        ninja.onFrameworkStart();
    }

    public synchronized void shutdown() {
        if (this.injector != null) {
            Ninja ninja = injector.getInstance(Ninja.class);
            ninja.onFrameworkShutdown();
            injector = null;
        } else {
            logger.error("Shutdown of Ninja not clean => injector already null.");
        }
    }
    
    public Injector getInjector() {
        return this.injector;
    }
    
    public void addModule(Module module) {
        this.modulesToLoad.add(module);
    }
    
    protected void configure() throws Exception {

        // Base configuration of Ninja
        addModule(new NinjaBaseModule(ninjaProperties));
        
        // Main application module (conf.Module or com.example.conf.Module)
        String applicationModuleClassName
                = ninjaBaseDirectoryResolver.resolveApplicationClassName(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION);

        AbstractModule applicationModule = null;
        
        if (SwissKnife.doesClassExist(applicationModuleClassName, this)) {
            Class<?> applicationModuleClass = Class
                    .forName(applicationModuleClassName);

            // Tries to instantiate module by giving the NinjaProperties as constructor arg
            try {
                applicationModule = (AbstractModule) applicationModuleClass
                        .getConstructor(NinjaProperties.class).newInstance(ninjaProperties);
            } catch (NoSuchMethodException e) {
                applicationModule = (AbstractModule) applicationModuleClass
                        .getConstructor().newInstance();
            }
        }
        
        // Slipstream in the "classic" ninja configuration?
        if (applicationModule == null || !(applicationModule instanceof FrameworkModule)) {
            // Classic configuration of Ninja
            logger.info("Enabling Ninja classic configuration");
            addModule(new NinjaClassicModule(ninjaProperties));
        }
        
        if (applicationModule != null) {
            addModule(applicationModule);
        }
        
        // Ninja module
        String applicationNinjaClassName
                = ninjaBaseDirectoryResolver.resolveApplicationClassName(NINJA_CONVENTION_LOCATION);

        final Class<? extends Ninja> ninjaClass;

        if (SwissKnife.doesClassExist(applicationNinjaClassName, this)) {

            final Class<?> clazzPotentially = Class.forName(applicationNinjaClassName);

            if (Ninja.class.isAssignableFrom(clazzPotentially)) {

                ninjaClass = (Class<? extends Ninja>) clazzPotentially;

            } else {

                final String ERROR_MESSAGE = String.format(
                        "Found a class %s in your application's conf directory."
                        + " This class does not implement Ninja interface %s. "
                        + " Please implement the interface or remove the class.",
                        applicationNinjaClassName,
                        Ninja.class.getName());

                logger.error(ERROR_MESSAGE);

                throw new IllegalStateException(ERROR_MESSAGE);

            }

        } else {

           ninjaClass = NinjaDefault.class;

        }

        addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Ninja.class).to(ninjaClass).in(Singleton.class);
            }
        });
    }
    
    private void initInjector() throws Exception {
        Module combinedModules;
        if (overrideModuleOpt.isPresent()) {
            // OverrideModule is for instance useful in tests. You can mock certain
            // classes and verify against them.
            // In real applications you do not want to use them.
            combinedModules = Modules.override(modulesToLoad).with(overrideModuleOpt.get());
        } else {
            combinedModules = Modules.combine(modulesToLoad);
        }
        this.injector = Guice.createInjector(Stage.PRODUCTION, combinedModules);
    }
    
    public void initRoutes() throws Exception {
        String applicationRoutesClassName
                = ninjaBaseDirectoryResolver.resolveApplicationClassName(ROUTES_CONVENTION_LOCATION);

        if (SwissKnife.doesClassExist(applicationRoutesClassName, this)) {

            Class<?> clazz = Class.forName(applicationRoutesClassName);
            
            ApplicationRoutes applicationRoutes = (ApplicationRoutes) injector
                    .getInstance(clazz);

            Router router = this.injector.getInstance(Router.class);

            applicationRoutes.init(router);
            
            router.compileRoutes();
        }
    }
    
    private void initLogback() {
        // init logging at the very very top
        try {
            Class.forName("ch.qos.logback.classic.joran.JoranConfigurator");
            LogbackConfigurator.initConfiguration(ninjaProperties);
            logger.info("Successfully configured Logback.");
             // It is available
        } catch (ClassNotFoundException exception) {
            logger.info("Ninja did not configure any logging because Logback is not on the classpath (you are probably using slf4j-jdk14).");
        }
    }

}
