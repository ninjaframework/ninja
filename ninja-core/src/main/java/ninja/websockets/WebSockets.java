/*
 * Copyright 2016 jjlauer.
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

import com.google.inject.ImplementedBy;
import ninja.Route;

/**
 * Interface for implementations of web sockets in Ninja.  Any interaction
 * Ninja requires for enabling web sockets will be done thru this interface.
 * 
 * @author jjlauer
 */
@ImplementedBy(DefaultWebSockets.class)
public interface WebSockets {
    
    /**
     * If WebSocket support is detected and enabled by the underlying container.
     * If true then any websocket routes should successfully configure.
     * 
     * @return True or false if websockets are available.
     */
    boolean isEnabled();
    
    /**
     * Configure the container for the websocket route.
     * 
     * @param route The websocket route
     */
    void compileRoute(Route route);
    
}