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

/**
 * Thread local storage of a WebSocket handshake.
 * 
 * @author jjlauer
 */
public class Jsr356HandshakeThreadLocal {

    static private final ThreadLocal<Jsr356Handshake> INSTANCE
        = new ThreadLocal<Jsr356Handshake>() {
            @Override
            protected Jsr356Handshake initialValue() {
                return null;
            }
        };

    static public Jsr356Handshake get() {
        Jsr356Handshake handshake = INSTANCE.get();
        
        if (handshake == null) {
            throw new IllegalStateException("No JSR-356 handshake is currently bound."
                + " Its possible a thread handed off websocket handshake processing to another thread"
                + " before Ninja had completed its own handshake.");
        }
        
        return handshake;
    }
    
    static public void set(Jsr356Handshake handshake) {
        Jsr356Handshake current = INSTANCE.get();
        
        if (current != null) {
            throw new IllegalStateException("A previous JSR-356 handshake is already bound.");
        }
        
        INSTANCE.set(handshake);
    }
    
    static public void remove() {
        INSTANCE.remove();
    }

}