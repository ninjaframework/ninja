/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.google.inject.multibindings.Multibinder;

import ninja.application.ApplicationRoutes;
import ninja.lifecycle.LifecycleSupport;
import ninja.logging.LogbackConfigurator;
import ninja.params.ParamParser;
import ninja.scheduler.SchedulerSupport;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

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
    private final List<Module> modulesToLoad;
    private final Optional<String> applicationModulesBasePackage;
    private Injector injector = null;

    public Bootstrap(NinjaPropertiesImpl ninjaProperties) {
        Preconditions.checkNotNull(ninjaProperties);
        this.ninjaProperties = ninjaProperties;
        this.modulesToLoad =  new ArrayList<>();
        
        // custom base package for application modules (e.g. com.example.conf.Routes)
        this.applicationModulesBasePackage
            = Optional.fromNullable(ninjaProperties.get(
                    NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE));
        
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
            ninja = null;
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

        // Bind lifecycle support
        addModule(LifecycleSupport.getModule());
        
        // Scheduling support
        addModule(SchedulerSupport.getModule());

        // Base configuration of Ninja
        addModule(new Configuration(ninjaProperties));
        
        // Main application module (conf.Module or com.example.conf.Module)
        String applicationModuleClassName
                = resolveApplicationClassName(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION);

        if (doesClassExist(applicationModuleClassName)) {

            Class<?> applicationModuleClass = Class
                    .forName(applicationModuleClassName);

            AbstractModule applicationConfiguration = null;
            
            // Tries to instantiate module by giving the NinjaProperties as constructor arg
            try {
                applicationConfiguration = (AbstractModule) applicationModuleClass
                        .getConstructor(NinjaProperties.class).newInstance(ninjaProperties);
            } catch (NoSuchMethodException e) {
                applicationConfiguration = (AbstractModule) applicationModuleClass
                        .getConstructor().newInstance();
            }

            addModule(applicationConfiguration);
        }
        
        // Ninja module
        String applicationNinjaClassName
                = resolveApplicationClassName(NINJA_CONVENTION_LOCATION);

        final Class<? extends Ninja> ninjaClass;

        if (doesClassExist(applicationNinjaClassName)) {

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
                Multibinder.newSetBinder(binder(), ParamParser.class);
            }
        });
    }
    
    private void initInjector() throws Exception {
        // Let the injector generate all instances and stuff
        this.injector = Guice.createInjector(Stage.PRODUCTION, modulesToLoad);
    }
    
    public void initRoutes() throws Exception {
        String applicationRoutesClassName
                = resolveApplicationClassName(ROUTES_CONVENTION_LOCATION);

        if (doesClassExist(applicationRoutesClassName)) {

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
            logger.info(
                    "Logback is not on classpath (you are probably using slf4j-jdk14). I did not configure anything. It's up to you now...", exception);
        }
    }
    
    protected boolean doesClassExist(String nameWithPackage) {

        boolean exists = false;

        try {
            Class.forName(nameWithPackage, false, this.getClass()
                    .getClassLoader());
            exists = true;
        } catch (ClassNotFoundException e) {
            exists = false;
        }

        return exists;

    }

    protected String resolveApplicationClassName(String classLocationAsDefinedByNinja) {
        if (applicationModulesBasePackage.isPresent()) {
            return new StringBuilder()
                .append(applicationModulesBasePackage.get())
                .append('.')
                .append(classLocationAsDefinedByNinja)
                .toString();
        } else {
            return classLocationAsDefinedByNinja;
        }
    }

}
