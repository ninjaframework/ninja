/*
 * Copyright 2017 ninjaframework.
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
package ninja.websockets;

import java.util.Set;

/**
 * Represents a websocket handshake.  Helps negotiate subprotocols, extensions,
 * etc.  See https://tools.ietf.org/html/rfc6455.
 * 
 * @author jjlauer
 */
public interface WebSocketHandshake {
    
    /**
     * The sub protocols requested by the client. Will be ordered to match
     * the order sent by the client.
     * 
     * @return The set of sub protocols requested by the client or null if
     *      the header was empty or never sent by the client.
     */
    Set<String> getRequestedProtocols();
    
    /**
     * Gets the current selected sub protocol out of the requested sub
     * protocols.
     * 
     * @return The selected sub protocol or null if none are selected
     */
    String getSelectedProtocol();

    /**
     * Selects the first matching supported sub protocol out of the requested
     * sub protocols. Most browsers and WebSocket implementations require
     * a match from the list supplied by the client or the handshake will fail.
     * 
     * @param protocol 
     * @return True if selected or false if none matched
     */
    boolean selectProtocol(String protocol);
    
}
