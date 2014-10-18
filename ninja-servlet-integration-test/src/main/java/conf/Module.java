/**
 * Copyright (C) 2012-2014 the original author or authors.
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

package conf;

import ninja.metrics.MetricsModule;

import com.google.inject.AbstractModule;

import etc.GreetingService;
import etc.GreetingServiceImpl;

public class Module extends AbstractModule {

    public Module() {
        super();     
    }

  
    @Override
    protected void configure() {       
        // /////////////////////////////////////////////////////////////////////
        // Some guice bindings
        // /////////////////////////////////////////////////////////////////////
        // some additional bindings for the application:
        bind(GreetingService.class).to(GreetingServiceImpl.class).asEagerSingleton();
        // Bind the UDP ping controller so it starts up on server start
        // bind(UdpPingController.class);

        install(new MetricsModule());

    }

}
