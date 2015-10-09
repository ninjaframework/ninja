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
import org.apache.commons.lang3.StringUtils;

/**
 * Helper utility for working with standalone applications.
 * 
 * @author joelauer
 */
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
    
    /**
     * Resolves which standalone class to use. Either defined as a system
     * property or falling back to the default.
     * @return The resolved standalone class to use
     */
    static public Class<? extends Standalone> resolveStandaloneClass() {
        String standaloneClassName = System.getProperty(Standalone.KEY_NINJA_STANDALONE_CLASS, Standalone.DEFAULT_STANDALONE_CLASS);
        try {
            return (Class<Standalone>)Class.forName(standaloneClassName);
        } catch (Exception e) {
            throw new RuntimeException("Unable to find standalone class '" + standaloneClassName + "' (class does not exist)");
        }
    }
    
    static public Standalone create(Class<? extends Standalone> standaloneClass) {
        try {
            return standaloneClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create " + standaloneClass.getCanonicalName() + " (either not on classpath or invalid class name)");
        }
    }
    
    static void checkContextPath(String contextPath) {
        // per servlet docs
        // The path starts with a / character but does not end with a / character.
        // For servlets in the default (root) context, this method returns ""
        if (StringUtils.isEmpty(contextPath)) {
            return;
        }
        
        if (!contextPath.startsWith("/")) {
            throw new IllegalArgumentException("A context path must start with a '/' character. " +
                    " See https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#getContextPath() for more info");
        }
        
        if (contextPath.endsWith("/")) {
            throw new IllegalArgumentException("A context path must not end with a '/' character. " +
                    " See https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#getContextPath() for more info");
        }
    }
    
}
