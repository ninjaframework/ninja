/**
 * Copyright (C) the original author or authors.
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

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import ninja.Bootstrap;
import javax.servlet.ServletContextEvent;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import java.util.Optional;
import javax.servlet.ServletContext;
import javax.websocket.server.ServerContainer;
import ninja.utils.NinjaMode;
import ninja.utils.SwissKnife;
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
    private static final Logger logger = LoggerFactory.getLogger(NinjaServletListener.class);
    
    private volatile Bootstrap ninjaBootstrap;
    private NinjaPropertiesImpl ninjaProperties = null;
    private Optional<com.google.inject.Module> overrideModuleOpt = Optional.empty();
    private String contextPath;
    private Optional<Module> webSocketModule = Optional.empty();

    public synchronized void setNinjaProperties(NinjaPropertiesImpl ninjaPropertiesImpl) {
        if (this.ninjaProperties != null) {
            throw new IllegalStateException("NinjaProperties already set.");
        }
        this.ninjaProperties = ninjaPropertiesImpl;
    }
    
    public synchronized void setOverrideModule(com.google.inject.Module overrideModule) {
        Preconditions.checkNotNull(overrideModule);
        if (this.overrideModuleOpt.isPresent()) {
            throw new IllegalStateException("overrideModule already set.");
        }
        this.overrideModuleOpt = Optional.of(overrideModule);
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) { 
        contextPath = servletContextEvent.getServletContext().getContextPath();
        
        // websocket enabled servlet containers populate this attribute with JSR 356
        // we save it here so we can inject it later into guice
        if (SwissKnife.doesClassExist(
                WebsocketGuiceModuleCreator.WEBSOCKET_SERVER_CONTAINER_CLASSNAME, 
                this)) {
            
            this.webSocketModule = WebsocketGuiceModuleCreator.getWebsocketServerContainerIfPossible(
                    servletContextEvent.getServletContext()); 
        }

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
     * Getting the injector is done via double locking in conjunction
     * with volatile keyword for thread-safety.
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
                        
                        NinjaMode ninjaMode = NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet();      
                        ninjaProperties = NinjaPropertiesImpl.builder()
                            .withMode(ninjaMode)
                            .build();   
                    
                    }
                
                    ninjaBootstrap 
                            = createNinjaBootstrap(ninjaProperties, overrideModuleOpt, contextPath);
                    ninjaBootstrapLocal = ninjaBootstrap;

                }
            
            }
        
        }
        
        return ninjaBootstrapLocal.getInjector();

    }
    

    
    private Bootstrap createNinjaBootstrap(
            NinjaPropertiesImpl ninjaProperties,
            Optional<com.google.inject.Module> overrideModuleOpt,
            String contextPath) {
    
        // we set the contextpath.
        ninjaProperties.setContextPath(contextPath);
        
        ninjaBootstrap = new NinjaServletBootstrap(ninjaProperties, overrideModuleOpt);
        
        // if websocket container present then enable jsr-356 websockets
        webSocketModule.ifPresent(module -> {
            ninjaBootstrap.addModule(module);
        });
        
        ninjaBootstrap.boot();
        
        return ninjaBootstrap;
    }
    
    
    /**
     * Huh. Why is there a separate class like this. Couldn't we not just do
     * everything directly in NinjaServletListener? No.
     *
     * Problem: 
     * When we'd use javax.websocket.server.ServerContainer inside
     * NinjaServletListener, then some ServletContainers will explode with a
     * ClassNotFoundException. For instance the Jetty bundled with the AppEngine
     * explodes like that.
     *
     * Solution: 
     * NinjaServletListener checks if the class exists on the
     * classpath and only then calls this class/method that then causes the
     * classloader to actually load it.
     */
    static class WebsocketGuiceModuleCreator {
        
        public static final String WEBSOCKET_SERVER_CONTAINER_CLASSNAME 
                = "javax.websocket.server.ServerContainer";

        public static Optional<Module> getWebsocketServerContainerIfPossible(
                ServletContext servletContext) {

            ServerContainer websocketServerContainer
                    = (ServerContainer) servletContext.getAttribute(WEBSOCKET_SERVER_CONTAINER_CLASSNAME);
            
            logger.info(
                "Using JSR-356 websocket container {}",
                websocketServerContainer
            );

            if (websocketServerContainer != null) {
                Module websocketsModule = new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(ServerContainer.class).toInstance(websocketServerContainer);
                        bind(WebSockets.class).to(Jsr356WebSockets.class);
                    }
                };
                return Optional.of(websocketsModule);

            } else {
                return Optional.empty();
            }
        }

    }

}
