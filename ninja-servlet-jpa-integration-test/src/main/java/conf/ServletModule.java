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

package conf;

import com.google.inject.Singleton;

import ninja.servlet.NinjaServletDispatcher;

/**
 * This module is optional.
 * 
 * If Ninja is running inside a servlet container you can
 * specify additional filters and servlets to be loaded.
 * 
 * The cool thing is that you can use all goodies from the servlet
 * world this way.
 * The bad thing is that you might loose a lot of stuff Ninja provides.
 * For instance scalability via a stateless architecture.
 * 
 * In short:
 * If you know what you are doing feel free to use ServletModule.
 * If not - just skip it and enjoy Ninja pure.
 *
 */
public class ServletModule extends com.google.inject.servlet.ServletModule {
    

    
    @Override
    protected void configureServlets() {
        
        bind(NinjaServletDispatcher.class).in(Singleton.class);
        serve("/*").with(NinjaServletDispatcher.class);
        
        
    }
    
}
