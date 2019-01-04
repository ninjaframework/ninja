/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import java.security.Principal;
import java.util.Optional;
import javax.websocket.Session;

/**
 * JSR-356 has a design flaw where you have no method of getting any value (
 * attribute, original headers, etc.) across the endpoint config -> configurator
 * -> new instance -> onOpen flow.
 * 
 * An excellent discussion of the nuances/design flaw of JSR-356 with regards to
 * fetching the original HTTP request/response for the handshake.
 * 
 * https://stackoverflow.com/questions/17936440/accessing-httpsession-from-httpservletrequest-in-a-web-socket-serverendpoint
 * 
 * Since Ninja does not actually use the "Principal" for request handling and that
 * value is guaranteed to be passed correctly across the entire handshake process,
 * we'll hijack the use of it to store whatever data we need to pass along as
 * a websocket connection is established.  We'll then unwrap the data in 
 * <code>Jsr356DelegatingEndpoint</code> before calling the user's endpoint.
 * 
 */
public class Jsr356HandshakePrincipal implements Principal {
    
    private final Principal principal;                      // original principal
    private final Jsr356Handshake handshake;
    
    public Jsr356HandshakePrincipal(Principal principal, Jsr356Handshake handshake) {
        this.principal = principal;
        this.handshake = handshake;
    }

    public Principal getPrincipal() {
        return this.principal;
    }

    public Jsr356Handshake getHandshake() {
        return handshake;
    }

    @Override
    public String getName() {
        return "jsr356-handshake";
    }
    
    static private Optional<Jsr356HandshakePrincipal> unwrap(Session session) {
        Principal userPrincipal = session.getUserPrincipal();
        if (userPrincipal != null && userPrincipal instanceof Jsr356HandshakePrincipal) {
            return Optional.of((Jsr356HandshakePrincipal)userPrincipal);
        }
        return Optional.empty();
    }
    
    static public Optional<Jsr356Handshake> unwrapHandshake(Session session) {
        return unwrap(session)
            .map(s -> s.getHandshake())
            .filter(o -> o instanceof Jsr356Handshake)
            .map(o -> (Jsr356Handshake)o);
    }
    
}