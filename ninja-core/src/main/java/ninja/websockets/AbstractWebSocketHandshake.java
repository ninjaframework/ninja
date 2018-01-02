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

import java.util.Set;

abstract public class AbstractWebSocketHandshake implements WebSocketHandshake {
    
    private Set<String> requestedProtocols;
    private String selectedProtocol;

    @Override
    public Set<String> getRequestedProtocols() {
        return requestedProtocols;
    }

    public void setRequestedProtocols(Set<String> requestedProtocols) {
        this.requestedProtocols = requestedProtocols;
    }

    @Override
    public String getSelectedProtocol() {
        return selectedProtocol;
    }

    @Override
    public boolean selectProtocol(String subprotocol) {
        if (this.requestedProtocols == null
                || this.requestedProtocols.isEmpty()
                || !this.requestedProtocols.contains(subprotocol)) {
            return false;
        }
        
        this.selectedProtocol = subprotocol;
        return true;
    }
    
}