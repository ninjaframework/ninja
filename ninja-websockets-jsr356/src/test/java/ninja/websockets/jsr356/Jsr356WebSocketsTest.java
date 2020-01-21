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

package ninja.websockets.jsr356;

import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import ninja.Route;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Jsr356WebSocketsTest {
    
    private ServerContainer serverContainer;
    private Route route;
    private Jsr356WebSockets webSockets;
    
    @Before
    public void before() {
        serverContainer = mock(ServerContainer.class);
        route = mock(Route.class);
        when(route.getUri()).thenReturn("/test");
        webSockets = new Jsr356WebSockets(serverContainer);
    }
    
    @Test
    public void compileRoute() throws DeploymentException {
        assertThat(webSockets.isEnabled(), is(true));
        
        doReturn(Endpoint.class).when(route).getControllerClass();
        
        webSockets.compileRoute(route);
        
        verify(serverContainer).addEndpoint((ServerEndpointConfig)any());
    }
    
    @Test
    public void compileRouteIsNotAnEndpoint() throws DeploymentException {
        assertThat(webSockets.isEnabled(), is(true));
        
        doReturn(Object.class).when(route).getControllerClass();
        
        try {
            webSockets.compileRoute(route);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
        
        verify(serverContainer, times(0)).addEndpoint((ServerEndpointConfig)any());
    }
    
}
