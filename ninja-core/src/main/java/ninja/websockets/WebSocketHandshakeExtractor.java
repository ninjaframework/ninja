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

package ninja.websockets;

import ninja.Context;
import ninja.params.ArgumentExtractor;

/**
 * Extracts a WebSocketHandshake stored in the Context as an attribute.  Allows
 * you to simply request it as a parameter in a controller method :-).
 * 
 * @author jjlauer
 */
public class WebSocketHandshakeExtractor implements ArgumentExtractor<WebSocketHandshake> {
    
    @Override
    public WebSocketHandshake extract(Context context) {
        return context.getAttribute(WebSocketUtils.ATTRIBUTE_HANDSHAKE, WebSocketHandshake.class);
    }

    @Override
    public Class<WebSocketHandshake> getExtractedType() {
        return WebSocketHandshake.class;
    }

    @Override
    public String getFieldName() {
        return null;
    }
    
}