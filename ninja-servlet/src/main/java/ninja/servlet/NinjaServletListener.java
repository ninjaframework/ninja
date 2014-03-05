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

import javax.servlet.ServletContextEvent;

import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * define in web.xml:
 * 
 * <listener>
 *   <listener-class>ninja.NinjaServletListener</listener-class>
 * </listener>
 *  
 * @author zoza
 * 
 */
public class NinjaServletListener extends GuiceServletContextListener {
    
    private NinjaBootstap ninjaBootstap;

    NinjaPropertiesImpl ninjaProperties;
    
    String contextPath;

    public void setNinjaProperties(NinjaPropertiesImpl ninjaProperties) {
        this.ninjaProperties = ninjaProperties; 
        
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) { 
        contextPath = servletContextEvent.getServletContext().getContextPath();
        super.contextInitialized(servletContextEvent);
    }
   
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ninjaBootstap.shutdown();
        super.contextDestroyed(servletContextEvent);
    }
   
    @Override
    public Injector getInjector() {
        
        // If Ninja is already booted and ready return the injector
        if (ninjaBootstap != null) {
            return ninjaBootstap.getInjector();
        }
        
        // we set the contextpath.
        ninjaProperties.setContextPath(contextPath);
        
        // Otherwise create a new bootstrap and generate a new injector.
        if (ninjaProperties != null) {
            ninjaBootstap = new NinjaBootstap(ninjaProperties);
            
        } else {
            ninjaBootstap = new NinjaBootstap();
        }
        
        ninjaBootstap.boot();
        return ninjaBootstap.getInjector();
    }

}
