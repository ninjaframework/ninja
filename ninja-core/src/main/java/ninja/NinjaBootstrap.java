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

package ninja;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import ninja.application.ApplicationRoutes;
import ninja.lifecycle.LifecycleSupport;
import ninja.scheduler.SchedulerSupport;
import ninja.utils.NinjaPropertiesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NinjaBootstrap {

    private static final Logger logger = LoggerFactory.getLogger(NinjaBootstrap.class);

    private static final String APPLICATION_GUICE_MODULE_CONVENTION_LOCATION = "conf.Module";
    private static final String ROUTES_CONVENTION_LOCATION = "conf.Routes";

    private NinjaPropertiesImpl ninjaProperties;

    private Injector injector;

    public NinjaBootstrap() {
        this(new NinjaPropertiesImpl());
    }

    public NinjaBootstrap(NinjaPropertiesImpl ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    public Injector getInjector() {
        return injector;
    }

    public synchronized void boot() {
        if (injector != null) {
            throw new RuntimeException("NinjaBootstap already booted");
        }
        injector = initInjector();
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

            // Load main application module:
            if (doesClassExist(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION)) {
                Class<?> applicationConfigurationClass = Class
                        .forName(APPLICATION_GUICE_MODULE_CONVENTION_LOCATION);


                AbstractModule applicationConfiguration = (AbstractModule) applicationConfigurationClass
                        .getConstructor().newInstance();

                modulesToLoad.add(applicationConfiguration);
            }

            modulesToLoad.addAll(getExtraGuiceModules(ninjaProperties));

            // And let the injector generate all instances and stuff:
            injector = Guice.createInjector(modulesToLoad);

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

    /**
     * Provides a hook for subclasses to add extra Guice modules.
     * <p>
     * The default implementation returns {@link Collections#emptyList()}.
     */
    protected Collection<Module> getExtraGuiceModules(NinjaPropertiesImpl ninjaProperties) throws Exception {
        return Collections.emptyList();
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
}
