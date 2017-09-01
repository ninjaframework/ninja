/*
 * Copyright 2017 NinjaFramework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.websockets.jsr356;

import java.util.List;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Please note that this configurator will likely be shared across threads and
 * socket handshakes.  Each method may be called by multiple threads at the same
 * time.
 * 
 * @author jjlauer
 */
public class Jsr356ServerEndpointConfigurator extends Configurator {
    static private final Logger log = LoggerFactory.getLogger(Jsr356ServerEndpointConfigurator.class);
    
    // order they seem to be called in by WS containers
    
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        log.trace("checkOrigin: {}", originHeaderValue);
        
        return super.checkOrigin(originHeaderValue);
    }

    @Override
    public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
        log.trace("negotiatedSubprotocol: supported={}, requested={}", supported, requested);
        
        Jsr356Handshake handshake = Jsr356HandshakeThreadLocal.get();
        
        log.trace("using thread local handshake {}", handshake);
        
        String selectedSubprotocol = handshake.getSelectedProtocol();
        
        log.trace("returning selected protocol {}", selectedSubprotocol);
        
        return selectedSubprotocol;
    }

    @Override
    public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
        log.trace("negotiatedExtensions: installed={}, requested={}", installed, requested);
        
        // TODO: its not common to need to negotiate these outside of the container
        // but we could eventually expose them in the WebSocketHandshake as in getNegotiatedSubprotocol
        
        return super.getNegotiatedExtensions(installed, requested);
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        log.trace("modifyHandshake");
        super.modifyHandshake(sec, request, response);
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        log.trace("endpointInstance");
        
        // jetty & tomcat both do this in the same thread as the other methods
        // wildfly already has handed this method off to another thread pool
        // we always return back the same endpoint class so the logic of
        // unwrapping the real endpoint is done in there
        
        return (T)new Jsr356DelegatingEndpoint();
    }
    
}
