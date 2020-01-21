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

package ninja.standalone;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.security.KeyStore;
import java.util.Iterator;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import ninja.utils.ForwardingServiceLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper utilities for working with standalone applications.
 */
public class StandaloneHelper {
    static private final Logger log = LoggerFactory.getLogger(StandaloneHelper.class);
    
    static private final String URI_SCHEME_CLASSPATH = "classpath";
    
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
        return resolveStandaloneClass(
            System.getProperty(Standalone.KEY_NINJA_STANDALONE_CLASS),
            ForwardingServiceLoader.loadWithSystemServiceLoader(Standalone.class),
            Standalone.DEFAULT_STANDALONE_CLASS
        );
    }
    
    // used for testing (so we can inject mocks of system static methods)
    static Class<? extends Standalone> resolveStandaloneClass(String standaloneClassNameSystemProperty,
                                                              ForwardingServiceLoader<Standalone> standaloneServiceLoader,
                                                              String standaloneClassNameDefaultValue) {
        Class<? extends Standalone> resolvedStandaloneClass = null;
        
        // 1. System property for 'ninja.standalone.class'
        if (standaloneClassNameSystemProperty != null) {
            try {
                resolvedStandaloneClass
                    = (Class<Standalone>)Class.forName(standaloneClassNameSystemProperty);
            } catch (Exception e) {
                throw new RuntimeException("Unable to find standalone class '" + standaloneClassNameSystemProperty + "' (class does not exist)");
            }
        }
        
        // 2. Implementation on classpath that's registered as service?
        if (resolvedStandaloneClass == null) {
            try {
                Iterator<Standalone> standaloneIterator = standaloneServiceLoader.iterator();
                // first one wins
                if (standaloneIterator.hasNext()) {
                    resolvedStandaloneClass = standaloneIterator.next().getClass();
                }
                // more than one triggers warning
                if (standaloneIterator.hasNext()) {
                    log.warn("More than one implementation of {} on classpath! Using {} which was the first", Standalone.class, resolvedStandaloneClass);
                }
            } finally {
                // always kill cache (since ServiceLoader actually instantiates an instance)
                standaloneServiceLoader.reload();
            }
        }
        
        // 3. Fallback to ninja default
        if (resolvedStandaloneClass == null) {
            try {
                resolvedStandaloneClass
                    = (Class<Standalone>)Class.forName(standaloneClassNameDefaultValue);
            } catch (Exception e) {
                throw new RuntimeException("Unable to find standalone class '" + standaloneClassNameDefaultValue + "' (class does not exist)");
            }
        }
        
        return resolvedStandaloneClass;
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
    
    static public InputStream openKeyStoreInput(URI uri) throws IOException {
        if (uri.getScheme().equals(URI_SCHEME_CLASSPATH)) {
            String resourceName = uri.getPath();
            
            log.debug("Opening keystore on classpath with resource {}", resourceName);
            
            InputStream stream = StandaloneHelper.class.getResourceAsStream(resourceName);
        
            if (stream == null) {
                throw new IOException("Resource '" + resourceName + "' not found on classpath");
            }

            return stream;
        } else {
            URL url = uri.toURL();
            
            log.debug("Opening keystore with url {}", url);
            
            return url.openStream();
        }
    }
    
    static public KeyStore loadKeyStore(URI uri, char[] password) throws Exception {
        try (InputStream stream = openKeyStoreInput(uri)) {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(stream, password);
            return ks;
        }
    }
    
    static public SSLContext createSSLContext(
            URI keystoreUri, char[] keystorePassword,
            URI truststoreUri, char[] truststorePassword) throws Exception {
        
        // load keystore
        KeyStore keystore = loadKeyStore(keystoreUri, keystorePassword);
        KeyManager[] keyManagers;
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keystore, keystorePassword);
        keyManagers = keyManagerFactory.getKeyManagers();

        // load truststore
        KeyStore truststore = loadKeyStore(truststoreUri, truststorePassword);
        TrustManager[] trustManagers;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(truststore);
        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        return sslContext;
    }
    
}
