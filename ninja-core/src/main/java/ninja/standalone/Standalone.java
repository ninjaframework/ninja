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

package ninja.standalone;

import com.google.inject.Injector;
import java.net.URI;
import java.util.List;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

/**
 * Interface for wrapping an underlying server (e.g. Jetty) to bootstrap Ninja
 * as a standalone application.
 * @param <T> The concrete standalone implementation (to make the builder
 *      pattern work correctly during compilation)
 */
public interface Standalone<T extends Standalone> {
    
    String KEY_NINJA_STANDALONE_CLASS                   = "ninja.standalone.class";
    String KEY_NINJA_CONTEXT_PATH                       = "ninja.context";
    String KEY_NINJA_HOST                               = "ninja.host";
    String KEY_NINJA_IDLE_TIMEOUT                       = "ninja.idle.timeout";
    String KEY_NINJA_PORT                               = "ninja.port";
    String KEY_NINJA_SSL_PORT                           = "ninja.ssl.port";
    String KEY_NINJA_SSL_KEYSTORE_URI                   = "ninja.ssl.keystore.uri";
    String KEY_NINJA_SSL_KEYSTORE_PASSWORD              = "ninja.ssl.keystore.password";
    String KEY_NINJA_SSL_TRUSTSTORE_URI                 = "ninja.ssl.truststore.uri";
    String KEY_NINJA_SSL_TRUSTSTORE_PASSWORD            = "ninja.ssl.truststore.password";
    
    String DEFAULT_STANDALONE_CLASS                     = "ninja.standalone.NinjaJetty";
    String DEFAULT_HOST                                 = null;                 // bind to any address
    Integer DEFAULT_PORT                                = 8080;
    Long DEFAULT_IDLE_TIMEOUT                           = 30000L;               // set to Jetty 9 default
    String DEFAULT_CONTEXT_PATH                         = "";                   // empty (root) context
    Integer DEFAULT_SSL_PORT                            = -1;                   // disabled by default
    
    // only defaults in dev & test mode
    String DEFAULT_DEV_NINJA_SSL_KEYSTORE_URI           = "classpath:/ninja/standalone/ninja-development.keystore";
    String DEFAULT_DEV_NINJA_SSL_KEYSTORE_PASSWORD      = "password";
    String DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_URI         = "classpath:/ninja/standalone/ninja-development.truststore";
    String DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_PASSWORD    = "password";

    /**
     * Configures the standalone to prepare for being started.
     * @return This standalone
     * @throws Exception Thrown if an exception occurs during configuration
     */
    T configure() throws Exception;
    
    /**
     * Configure, start, add shutdown hook, and join.  Since this
     * method joins the underlying server (e.g. Jetty), it does not exit until
     * interrupted.
     */
    void run();

    /**
     * Configures (if not yet done), boots Ninja application and starts the
     * underlying server.
     * @return This standalone
     * @throws Exception Thrown if an exception occurs during Ninja boot or
     *      server start
     */
    T start() throws Exception;
    
    /**
     * Joins the underlying server to wait until its finished.
     * @return This standalone
     * @throws Exception Thrown if an exception occurs while waiting
     */
    T join() throws Exception;
    
    /**
     * Shutdown Ninja and underlying server as safely as possible (tries not
     * to cause exceptions to be thrown).
     * @return This standalone
     */
    T shutdown();

    NinjaMode getNinjaMode();
    
    T ninjaMode(NinjaMode ninjaMode);
    
    String getExternalConfigurationPath();
    
    T externalConfigurationPath(String externalConfigurationPath);
    
    String getName();
    
    T name(String name);
    
    String getHost();
    
    T host(String host);
    
    Integer getPort();

    T port(int port);
    
    String getContextPath();

    /**
     * Sets the "context path" of the Ninja application.  Must follow servlet
     * spects where it starts with a "/" and does not end with a "/".
     * https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#getContextPath()
     * @param contextPath The context path such as "/mycontext"
     * @return This standalone
     * @throws IllegalArgumentException Thrown if the context value is not valid
     */
    T contextPath(String contextPath);

    T idleTimeout(long idleTimeout);
    
    Long getIdleTimeout();

    Integer getSslPort();
    
    T sslPort(int sslPort);
    
    URI getSslKeystoreUri();

    T sslKeystoreUri(URI keystoreUri);
    
    String getSslKeystorePassword();

    T sslKeystorePassword(String keystorePassword);
    
    URI getSslTruststoreUri();

    T sslTruststoreUri(URI truststoreUri);
    
    String getSslTruststorePassword();

    T sslTruststorePassword(String truststorePassword);
    
    /**
     * Gets the NinjaProperties that were used to configure Ninja. This value
     * is only accessible after configure() is successfully called.
     * @return The NinjaProperties implementation
     * @throws IllegalStateException Thrown if attempting to access this variable
     *      before configure() is successfully called.
     */
    NinjaPropertiesImpl getNinjaProperties();
    
    /**
     * Gets the Guice injector that booted the Ninja application. This value
     * is only accessible after start() is successfully called.
     * @return The guice injector
     * @throws IllegalStateException Thrown if attempting to access this variable
     *      before start() is successfully called.
     */
    Injector getInjector();
    
    /**
     * Get the urls for the servers that are configured to start. This value
     * does not include the configured contextPath.  Returns a value in the
     * form 'scheme://host:port'. Well-known scheme and port combinations will
     * not include the port.
     * @return The urls of the server such as http://localhost:8080
     * @see #getServerUrls() 
     */
    List<String> getServerUrls();
    
    /**
     * Get the urls for the application that is configured to start. This value
     * includes the configured contextPath. Returns a value in form
     * 'scheme://host:port/context'. Well-known scheme and port combinations will
     * not include the port.
     * @return The uri(s) of the application such as http://localhost:8080/context
     * @see #getBaseUrls() 
     */
    List<String> getBaseUrls();

    /**
     * Tests if the clear text HTTP port is enabled. Usually this indicates
     * the "ninja.port" property is > -1.
     * @return True if enabled otherwise false.
     */
    boolean isPortEnabled();
    
    /**
     * Tests if the SSL HTTP port is enabled. Usually this indicates
     * the "ninja.ssl.port" property is > -1.
     * @return True if enabled otherwise false.
     */
    boolean isSslPortEnabled();
    
}
