/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import com.google.inject.AbstractModule;
import ninja.Bootstrap;
import javax.servlet.ServletContextEvent;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import javax.websocket.server.ServerContainer;
import ninja.websockets.WebSockets;
import ninja.websockets.jsr356.Jsr356WebSockets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    static private final Logger log = LoggerFactory.getLogger(NinjaServletListener.class);
    
    private volatile Bootstrap ninjaBootstrap;
    NinjaPropertiesImpl ninjaProperties = null;
    String contextPath;
    private ServerContainer webSocketServerContainer;

    public synchronized void setNinjaProperties(NinjaPropertiesImpl ninjaPropertiesImpl) {
        if (this.ninjaProperties != null) {
            throw new IllegalStateException("NinjaProperties already set.");
        }
        this.ninjaProperties = ninjaPropertiesImpl;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) { 
        contextPath = servletContextEvent.getServletContext().getContextPath();
        
        // websocket enabled servlet containers populate this attribute with JSR 356
        // we save it here so we can inject it later into guice
        this.webSocketServerContainer = (ServerContainer)servletContextEvent.getServletContext()
            .getAttribute("javax.websocket.server.ServerContainer");
        
        super.contextInitialized(servletContextEvent);
    }
   
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ninjaBootstrap.shutdown();
        super.contextDestroyed(servletContextEvent);
    }
    
    /**
     * Only available after context initialization attempted.
     */
    public Bootstrap getNinjaBootstrap() {
        return this.ninjaBootstrap;
    }
   
    /**
     * Getting the injector is done via double locking in conjuction
     * with volatile keyword for thread safety.
     * See also: http://en.wikipedia.org/wiki/Double-checked_locking
     * 
     * @return The injector for this application.
     */
    @Override
    public Injector getInjector() {
        
        // fetch instance variable into method, so that we access the volatile
        // global variable only once - that's better performance wise.
        Bootstrap ninjaBootstrapLocal = ninjaBootstrap;
        
        if (ninjaBootstrapLocal == null) {

            synchronized(this) {
                
                ninjaBootstrapLocal = ninjaBootstrap;
                
                if (ninjaBootstrapLocal == null) {
                    
                    // if properties 
                    if (ninjaProperties == null) {
                        
                        ninjaProperties 
                                = new NinjaPropertiesImpl(
                                        NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
                    
                    }
                
                    ninjaBootstrap 
                            = createNinjaBootstrap(ninjaProperties, contextPath);
                    ninjaBootstrapLocal = ninjaBootstrap;

                }
            
            }
        
        }
        
        return ninjaBootstrapLocal.getInjector();

    }
    
    private Bootstrap createNinjaBootstrap(
        NinjaPropertiesImpl ninjaProperties,
        String contextPath) {
    
        // we set the contextpath.
        ninjaProperties.setContextPath(contextPath);
        
        ninjaBootstrap = new NinjaServletBootstrap(ninjaProperties);
        
        // if websocket container present then enable jsr-356 websockets
        if (webSocketServerContainer != null) {
            log.info("Using JSR-356 websocket container {}",
                webSocketServerContainer.getClass().getCanonicalName());
            
            ninjaBootstrap.addModule(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(ServerContainer.class).toInstance(webSocketServerContainer);
                    bind(WebSockets.class).to(Jsr356WebSockets.class);
                }
            });
        }
        
        ninjaBootstrap.boot();
        
        return ninjaBootstrap;

    }

}
