/**
 * Copyright (C) 2012- the original author or authors.
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

package ninja.websockets;

import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * Utilities for implementing WebSockets.
 * 
 * @author jjlauer
 */
public class WebSocketUtils {
    
    // Attribute key for context storage of the endpoint instance to use
    // for continuation of a websocket handshake
    static public final String ATTRIBUTE_ENDPOINT = "ninja.websocket.endpoint";
    static public final String ATTRIBUTE_HANDSHAKE = "ninja.websocket.handshake";
    
    /**
     * Parses header `Sec-WebSocket-Protocol: chat, superchat` into a list
     * such as `chat`, and `superchat`.
     * @param value The header value
     * @return A set of values or null if value was null or empty
     */
    static public Set<String> parseProtocolRequestHeader(String value) {
        Set<String> subprotocols = new LinkedHashSet<>();
        
        if (!StringUtils.isEmpty(value)) {
            String[] values = value.split(",");
            for (String v : values) {
                subprotocols.add(v.trim());
            }
        }
        
        return subprotocols;
    }
    
}
