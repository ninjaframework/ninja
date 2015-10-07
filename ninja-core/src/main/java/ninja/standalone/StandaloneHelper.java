/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.standalone;

import java.io.IOException;
import java.net.ServerSocket;

public class StandaloneHelper {
    
    static public int findAvailablePort(int min, int max) {
        for (int port = min; port < max; port++) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                // Must already be taken
            }
        }
        throw new IllegalStateException(
                "Could not find available port in range " + min + " to " + max);
    }
    
    static public Class<? extends Standalone> findDefaultStandaloneClass() {
        // either system property OR the default implementation
        String defaultClassName = System.getProperty(Standalone.KEY_NINJA_STANDALONE, Standalone.DEFAULT_STANDALONE_CLASS);
        try {
            return (Class<Standalone>)Class.forName(defaultClassName);
        } catch (Exception e) {
            throw new RuntimeException("Unable to find standalone class '" + defaultClassName + "' (class does not exist)");
        }
    }
    
}
