/**
 * Copyright (C) 2012-2020 the original author or authors.
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

import ninja.Bootstrap;
import ninja.Context;
import ninja.utils.NinjaPropertiesImpl;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.ServletModule;
import ninja.utils.SwissKnife;

/**
 * Ninja bootstrap for servlet environments.  Binds the context implementation
 * as well as attempting to load conf.ServletModule.
 */
public class NinjaServletBootstrap extends Bootstrap {
    
    public NinjaServletBootstrap(NinjaPropertiesImpl ninjaProperties) {
        super(ninjaProperties);
    }

    @Override
    protected void configure() throws Exception {
        super.configure();

        // Context for servlet requests
        addModule(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Context.class).to(NinjaServletContext.class);
            }
        });
        
        // Load servlet module. By convention this is a ServletModule where
        // the user can register other servlets and servlet filters
        // If the file does not exist we simply load the default servlet
        String servletModuleClassName
            = ninjaBaseDirectoryResolver.resolveApplicationClassName(APPLICATION_GUICE_SERVLET_MODULE_CONVENTION_LOCATION);

        if (SwissKnife.doesClassExist(servletModuleClassName, this)) {

            Class<?> servletModuleClass = Class
                    .forName(servletModuleClassName);

            ServletModule servletModule = (ServletModule) servletModuleClass
                    .getConstructor().newInstance();

            addModule(servletModule);

        } else {
            // The servlet Module does not exist => we load the default one.
            ServletModule servletModule = new ServletModule() {

                @Override
                protected void configureServlets() {
                    bind(NinjaServletDispatcher.class).asEagerSingleton();
                    serve("/*").with(NinjaServletDispatcher.class);
                }

            };

            addModule(servletModule);
        }
    }
}
