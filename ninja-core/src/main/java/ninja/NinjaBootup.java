/**
 * Copyright (C) 2012 the original author or authors.
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

import ninja.application.ApplicationRoutes;
import ninja.lifecycle.LifecycleService;
import ninja.lifecycle.LifecycleSupport;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import ninja.scheduler.SchedulerSupport;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

public class NinjaBootup {

    private static final String APPLICATION_GUICE_MODULE_CONVENTION_LOCATION = "conf.Module";
    private static final String ROUTES_CONVENTION_LOCATION = "conf.Routes";

    /**
     * Main injector for the class.
     */
    private Injector injector;

    public NinjaBootup(NinjaPropertiesImpl ninjaProperties) {
        try {
            List<Module> modulesToLoad = new ArrayList<Module>();

            // Bind lifecycle support
            modulesToLoad.add(LifecycleSupport.getModule());
            // Scheduling support
            modulesToLoad.add(SchedulerSupport.getModule());

            // Get base configuration of Ninja:
            modulesToLoad.add(new Configuration(ninjaProperties));

            // Load main application module:
            if (doesClassExist(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION)) {
                Class applicationConfigurationClass = Class
                        .forName(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION);
                Module applicationConfiguration = (Module) applicationConfigurationClass
                        .newInstance();
                modulesToLoad.add(applicationConfiguration);
            }

            // And let the injector generate all instances and stuff:
            injector = Guice.createInjector(modulesToLoad);

            // Init routes
            if (doesClassExist(ROUTES_CONVENTION_LOCATION)) {
                Class clazz = Class.forName(ROUTES_CONVENTION_LOCATION);
                ApplicationRoutes applicationRoutes = (ApplicationRoutes) injector
                        .getInstance(clazz);

                // System.out.println("init routes");
                Router router = injector.getInstance(Router.class);

                applicationRoutes.init(router);
                router.compileRoutes();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Injector getInjector() {

        return injector;

    }

    public void shutdown() {
        injector.getInstance(LifecycleService.class).stop();
    }

    /**
     * TODO => I want to live somewhere else...
     * 
     * 
     */
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
