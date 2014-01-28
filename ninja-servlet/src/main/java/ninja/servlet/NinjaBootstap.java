/**
 * Copyright (C) 2013 the original author or authors.
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

package ninja.servlet;

import java.util.ArrayList;
import java.util.List;

import ninja.Configuration;
import ninja.Context;
import ninja.Ninja;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.lifecycle.LifecycleSupport;
import ninja.scheduler.SchedulerSupport;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.ServletModule;

public class NinjaBootstap {
    
    private Logger logger = LoggerFactory.getLogger(NinjaBootstap.class);

    private static final String APPLICATION_GUICE_MODULE_CONVENTION_LOCATION = "conf.Module";
    private static final String APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION = "conf.ServletModule";
    private static final String ROUTES_CONVENTION_LOCATION = "conf.Routes";
    private NinjaPropertiesImpl ninjaProperties;

    private Injector injector = null;

    public NinjaBootstap() {

        
        this(new NinjaPropertiesImpl(
                NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet()));
        
    }

    public NinjaBootstap(
                         NinjaPropertiesImpl ninjaProperties) {

        this.ninjaProperties = ninjaProperties;
    }

    public Injector getInjector() {
        return injector;
    }

    public synchronized void boot() {
        if (injector != null) {
            throw new RuntimeException("NinjaBootstap already booted");
        }
        
        long startTime = System.currentTimeMillis();
        
        injector = initInjector();
        
        long injectorStartupTime = System.currentTimeMillis() - startTime;
        logger.info("Ninja injector started in " + injectorStartupTime + " ms.");
        
        Preconditions.checkNotNull(injector, "Ninja injector cannot be generated. Please check log for further errors.");
        
        Ninja ninja = injector.getInstance(Ninja.class);
        ninja.start();
    }

    public synchronized void shutdown() {
        if (injector != null) {            
            Ninja ninja = injector.getInstance(Ninja.class);
            ninja.shutdown();
            injector = null;
            ninja = null;
        } else {           
            logger.info("Shutdown of Ninja not clean => injector already null.");
        }
    }

    private Injector initInjector() {

        try {
            List<Module> modulesToLoad = new ArrayList<Module>();

            // Bind lifecycle support
            modulesToLoad.add(LifecycleSupport.getModule());
            // Scheduling support
            modulesToLoad.add(SchedulerSupport.getModule());

            // Get base configuration of Ninja:
            modulesToLoad.add(new Configuration(ninjaProperties));
            modulesToLoad.add(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Context.class).to(ContextImpl.class);
                }
            });

            // Load main application module:
            if (doesClassExist(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION)) {
                Class<?> applicationConfigurationClass = Class
                        .forName(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION);

              
                AbstractModule applicationConfiguration = (AbstractModule) applicationConfigurationClass
                        .getConstructor().newInstance();
 
                modulesToLoad.add(applicationConfiguration);
            }
            
            // Load servlet module. By convention this is a ServletModule where 
            // the user can register other servlets and servlet filters
            // If the file does not exist we simply load the default servlet
            if (doesClassExist(APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION)) {
                Class<?> servletModuleClass = Class
                        .forName(APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION);

                ServletModule servletModule = (ServletModule) servletModuleClass
                        .getConstructor().newInstance();
 
                modulesToLoad.add(servletModule);
                
            } else {
                // The servlet Module does not exist => we load the default one.                
                ServletModule servletModule = new ServletModule() {
                    
                    @Override
                    protected void configureServlets() {   
                        bind(NinjaServletDispatcher.class).asEagerSingleton();                        
                        serve("/*").with(NinjaServletDispatcher.class);
                    }
                    
                };
                
                modulesToLoad.add(servletModule);
                
            }
            

            // And let the injector generate all instances and stuff:
            injector = Guice.createInjector(Stage.PRODUCTION, modulesToLoad);

            // Init routes
            if (doesClassExist(ROUTES_CONVENTION_LOCATION)) {
                Class<?> clazz = Class.forName(ROUTES_CONVENTION_LOCATION);
                ApplicationRoutes applicationRoutes = (ApplicationRoutes) injector
                        .getInstance(clazz);

                // System.out.println("init routes");
                Router router = injector.getInstance(Router.class);

                applicationRoutes.init(router);
                router.compileRoutes();
            }
            return injector;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean doesClassExist(String nameWithPackage) {

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

}
