/*
 * Copyright 2017 jjlauer.
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

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * A valid JSR-356 endpoint that extracts out the real target endpoint from
 * the handshake stored as the <code>UserPrincipal</code> of the WebSocket session.
 * 
 * @author jjlauer
 */
public class Jsr356DelegatingEndpoint extends Endpoint {

    private Jsr356Handshake handshake;
    
    public void verify(Session session) {
        if (this.handshake == null) {
            this.handshake = Jsr356HandshakePrincipal.unwrapHandshake(session)
                .orElse(null);
        }
        if (this.handshake == null) {
            throw new IllegalStateException("No websocket handshake exists. Something appears "
                + "to be broken in how your HTTP container handles websocket handshakes.");
        }
        if (this.handshake.getEndpoint() == null) {
            throw new IllegalStateException("No websocket endpoint exists to delegate to."
                + " An endpoint must have either not been created in Ninja or " 
                + " your HTTP container failed to pass along the value during the websocket handshake process.");
        }
    } 
    
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.verify(session);
        this.handshake.getEndpoint().onOpen(session, config);
    }
    
    @Override
    public void onError(Session session, Throwable cause) {
        this.verify(session);
        this.handshake.getEndpoint().onError(session, cause);
    }

    @Override
    public void onClose(Session session, CloseReason close) {
        this.verify(session);
        this.handshake.getEndpoint().onClose(session, close);
    }
    
}