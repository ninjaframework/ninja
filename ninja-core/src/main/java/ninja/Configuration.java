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

import ninja.postoffice.Postoffice;
import ninja.postoffice.guice.PostofficeProvider;
import ninja.utils.LoggerProvider;

import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * The basic configuration of the main ninja framework.
 * 
 * @author ra
 * 
 */
public class Configuration extends AbstractModule {

    private final NinjaPropertiesImpl ninjaProperties;

    public Configuration(NinjaPropertiesImpl ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }

    public void configure() {

        System.setProperty("file.encoding", "utf-8");

        // general classes for servlet container:
        bind(RouteBuilder.class).to(RouteBuilderImpl.class);

        bind(Router.class).to(RouterImpl.class).in(Singleton.class);

        bind(Ninja.class).to(NinjaImpl.class).in(Singleton.class);

        bind(Context.class).to(ContextImpl.class);

        // provide logging
        bind(Logger.class).toProvider(LoggerProvider.class);

        // Bind the configuration into Guice
        ninjaProperties.bindProperties(binder());
        bind(NinjaProperties.class).toInstance(ninjaProperties);

        // Postoffice
        bind(Postoffice.class).toProvider(PostofficeProvider.class);
    }

}
