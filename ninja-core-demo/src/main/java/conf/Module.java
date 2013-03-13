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

package conf;

import javax.servlet.ServletContext;

import ninja.NinjaAppAbstractModule;
import ninja.NinjaServletDispatcher;

import com.google.inject.servlet.ServletModule;

import etc.GreetingService;
import etc.GreetingServiceImpl;
import filters.DemoServletFilter;

public class Module extends NinjaAppAbstractModule{

    public Module(ServletContext servletContext) {
        super(servletContext);     
    }

  
    @Override
    protected void setup() {       
        // /////////////////////////////////////////////////////////////////////
        // Some guice bindings
        // /////////////////////////////////////////////////////////////////////
        // some additional bindings for the application:
        bind(GreetingService.class).to(GreetingServiceImpl.class);
        // Bind the UDP ping controller so it starts up on server start
        // bind(UdpPingController.class);
        
    }

    @Override
    protected ServletModule setupServlets() {
        // add new Servlet filter ONLY if you have some stuff that MUST use Servlet filter.
        // otherwise use filters that provide ninja
        
        // every filter must be defined as singleton       
        bind(NinjaServletDispatcher.class).asEagerSingleton();

        //this one only as reference
        //remove it in your app
        bind(DemoServletFilter.class).asEagerSingleton();
        return new ServletModule() {
            @Override
            protected void configureServlets() {                
                filter("/*").through(DemoServletFilter.class);
                serve("/*").with(NinjaServletDispatcher.class);
            }
        };
    }

}
