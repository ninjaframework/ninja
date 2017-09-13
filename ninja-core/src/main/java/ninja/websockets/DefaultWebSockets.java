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

package ninja.websockets;

import ninja.Route;

/**
 * Default implementation of WebSockets (which is of course disabled).
 * 
 * @author jjlauer
 */
public class DefaultWebSockets implements WebSockets {

    @Override
    public boolean isEnabled() {
        return false;
    }
    
    @Override
    public void compileRoute(Route route) {
        throw new IllegalStateException("WebSockets are not enabled." + 
            " Unable to add websocket route to " + route.getUri() + "." + 
            " Are you running in a websocket-enabled HTTP server?");
    }
    
}
