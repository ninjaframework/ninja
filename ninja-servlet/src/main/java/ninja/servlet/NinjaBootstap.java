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
import java.util.Collection;
import java.util.List;

import com.google.inject.AbstractModule;
import ninja.Context;
import ninja.utils.NinjaPropertiesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;

public class NinjaBootstap extends ninja.NinjaBootstrap {

    private Logger logger = LoggerFactory.getLogger(NinjaBootstap.class);

    private static final String APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION = "conf.ServletModule";

    public NinjaBootstap() {
        super();
    }

    public NinjaBootstap(NinjaPropertiesImpl ninjaProperties) {
        super(ninjaProperties);
    }

    @Override
    protected Collection<Module> getExtraGuiceModules(NinjaPropertiesImpl ninjaProperties) throws Exception {
        List<Module> extraModules = new ArrayList<Module>(2);

        extraModules.add(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Context.class).to(ContextImpl.class);
            }
        });

        // Load servlet module. By convention this is a ServletModule where
        // the user can register other servlets and servlet filters
        // If the file does not exist we simply load the default servlet
        if (doesClassExist(APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION)) {
            Class<?> servletModuleClass = Class
                    .forName(APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION);

            ServletModule servletModule = (ServletModule) servletModuleClass
                    .getConstructor().newInstance();

            extraModules.add(servletModule);

        } else {
            // The servlet Module does not exist => we load the default one.
            ServletModule servletModule = new ServletModule() {
                @Override
                protected void configureServlets() {
                    bind(NinjaServletDispatcher.class).asEagerSingleton();
                    serve("/*").with(NinjaServletDispatcher.class);
                }
            };

            extraModules.add(servletModule);
        }
        return extraModules;
    }
}
