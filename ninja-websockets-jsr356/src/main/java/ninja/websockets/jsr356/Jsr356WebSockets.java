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

package ninja.websockets.jsr356;

import com.google.inject.Injector;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerContainer;
import ninja.websockets.WebSockets;
import ninja.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSR-356 implementation of Ninja WebSockets. Will use "programmatic deployment"
 * of WebSocket endpoints when Ninja routes are compiled at boot.

 * @author jjlauer
 */
@Singleton
public class Jsr356WebSockets implements WebSockets {
    static private final Logger log = LoggerFactory.getLogger(Jsr356WebSockets.class);

    private final ServerContainer serverContainer;
    
    @Inject
    public Jsr356WebSockets(ServerContainer serverContainer) {
        Objects.requireNonNull(serverContainer, "serverContainer was null");
        this.serverContainer = serverContainer;
    }

    public ServerContainer getServerContainer() {
        return serverContainer;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    @Override
    public void compileRoute(Route route) {
        log.debug("Adding websocket to {} with endpoint {} in server container {}",
            route.getUri(), route.getControllerClass(), serverContainer.getClass().getCanonicalName());
        
        Class<?> endpointClass = route.getControllerClass();
        
        // this must subclass an Endpoint to be valid
        if (!Endpoint.class.isAssignableFrom(endpointClass)) {
            throw new IllegalArgumentException("WebSocket controller class must be of type " + Endpoint.class.getCanonicalName());
        }
        
        try {
            // programatically add endpoint to websocket container
            serverContainer.addEndpoint(new Jsr356ServerEndpointConfig(route));
        } catch (DeploymentException e) {
            throw new RuntimeException(e);
        }
    }
    
}
