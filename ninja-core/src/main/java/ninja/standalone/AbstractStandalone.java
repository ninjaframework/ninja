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

package ninja.standalone;

import static ninja.standalone.StandaloneHelper.checkContextPath;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import ninja.utils.OverlayedNinjaProperties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.CreationException;

import javax.net.ssl.SSLContext;

/**
 * Abstract Standalone that implements most functionality required to write
 * a concrete Standalone.  Introduces new doStart(), doStop(), and doJoin()
 * methods which are actually where you'll place most of your logic.  You'll
 * also want to subclass the configure() method to add any configuration
 * specific to your Standalone.  See NinjaJetty for example concrete implementation.
 * @param <T> The concrete standalone implementation
 */
abstract public class AbstractStandalone<T extends AbstractStandalone> implements Standalone<T>, Runnable {
    // allow logger to take on persona of concrete class
    final protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // can all be changed prior to configure()
    protected NinjaMode ninjaMode;
    protected String externalConfigurationPath;
    protected String name;
    protected String host;
    protected Integer port;
    protected String contextPath;
    protected Long idleTimeout;
    protected Integer sslPort;
    protected URI sslKeystoreUri;
    protected String sslKeystorePassword;
    protected URI sslTruststoreUri;
    protected String sslTruststorePassword;
    
    // internal state
    protected boolean configured;
    protected boolean started;
    protected NinjaPropertiesImpl ninjaProperties;                      // after configure()
    protected OverlayedNinjaProperties overlayedNinjaProperties;        // after configure()
    protected List<String> serverUrls;
    protected List<String> baseUrls;
    
    public AbstractStandalone(String name) {
        // set mode as quickly as possible (can still be changed before configure())
        this.ninjaMode = NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet();
        this.name = name;
        this.configured = false;
        this.started = false;
    }
    
    /**
     * Configure, start, add shutdown hook, and join.  Does not exit.
     */
    @Override
    final public void run() {
        // name current thread for improved logging/debugging
        Thread.currentThread().setName(this.name);
        
        try {
            this.configure();
        } catch (Exception e) {
            logger.error("Unable to configure {}", name, e);
            System.exit(1);
        }
 
        try {
            this.start();
        } catch (Exception e) {
            logger.error("Unable to start {}", name, e);
            System.exit(1);
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
        
        try {
            // do not simply exit main() -- join something (likely server)
            join();
        } catch (Exception e) {
            logger.warn("Interrupted (most likely JVM is shutting down and this is safe to ignore)");
        }
    }
    
    @Override
    final public T configure() throws Exception {
        checkNotConfigured();
        
        // create ninja properties & overlayed view
        this.ninjaProperties = new NinjaPropertiesImpl(this.ninjaMode, this.externalConfigurationPath);
        
        this.overlayedNinjaProperties = new OverlayedNinjaProperties(this.ninjaProperties);
        
        // current value or system property or conf/application.conf or default value
        host(overlayedNinjaProperties.get(
                KEY_NINJA_HOST, this.host, DEFAULT_HOST));
        
        port(overlayedNinjaProperties.getInteger(
                KEY_NINJA_PORT, this.port, DEFAULT_PORT));
        
        contextPath(overlayedNinjaProperties.get(
                KEY_NINJA_CONTEXT_PATH, this.contextPath, DEFAULT_CONTEXT_PATH));
        
        idleTimeout(overlayedNinjaProperties.getLong(
                KEY_NINJA_IDLE_TIMEOUT, this.idleTimeout, DEFAULT_IDLE_TIMEOUT));
        
        sslPort(overlayedNinjaProperties.getInteger(
                KEY_NINJA_SSL_PORT, this.sslPort, DEFAULT_SSL_PORT));
        
        // defaults below (with self-signed cert) only valid in dev & test modes
        
        sslKeystoreUri(overlayedNinjaProperties.getURI(KEY_NINJA_SSL_KEYSTORE_URI, this.sslKeystoreUri, 
                (this.ninjaMode == NinjaMode.prod ? null : new URI(DEFAULT_DEV_NINJA_SSL_KEYSTORE_URI))));
        
        sslKeystorePassword(overlayedNinjaProperties.get(
                KEY_NINJA_SSL_KEYSTORE_PASSWORD, this.sslKeystorePassword, 
                (this.ninjaMode == NinjaMode.prod ? null : DEFAULT_DEV_NINJA_SSL_KEYSTORE_PASSWORD)));
        
        sslTruststoreUri(overlayedNinjaProperties.getURI(KEY_NINJA_SSL_TRUSTSTORE_URI, this.sslTruststoreUri, 
                (this.ninjaMode == NinjaMode.prod ? null : new URI(DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_URI))));
        
        sslTruststorePassword(overlayedNinjaProperties.get(
                KEY_NINJA_SSL_TRUSTSTORE_PASSWORD, this.sslTruststorePassword, 
                (this.ninjaMode == NinjaMode.prod ? null : DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_PASSWORD)));
        
        
        // assign random ports if needed
        if (getPort() == null || getPort() == 0) {
            port(StandaloneHelper.findAvailablePort(8000, 9000));
        }
        
        if (getSslPort() == null || getSslPort() == 0) {
            sslPort(StandaloneHelper.findAvailablePort(9001, 9999));
        }
        
        doConfigure();
        
        this.configured = true;
        
        // build configured urls
        this.serverUrls = createServerUrls();
        this.baseUrls = createBaseUrls();
        
        // is there at least one url?
        if (this.serverUrls == null || this.serverUrls.isEmpty()) {
            throw new IllegalStateException("All server ports were disabled." + 
                    " Check the 'ninja.port' property and possibly others depending your standalone.");
        }
        
        // save generated server name as ninja property if its not yet set
        String serverName = this.ninjaProperties.get(NinjaConstant.serverName);
        
        if (StringUtils.isEmpty(serverName)) {
            // grab the first one
            this.ninjaProperties.setProperty(NinjaConstant.serverName, getServerUrls().get(0));
        }
        
        return (T)this;
    }
    
    @Override
    final public T start() throws Exception {
        if (!this.configured) {
            configure();
        }
        
        doStart();
        
        this.started = true;
        
        logBaseUrls();
        
        return (T)this;
    }
    
    @Override
    final public T join() throws Exception{
        checkStarted();
        
        doJoin();
        
        return (T)this;
    }
    
    @Override
    final public T shutdown() {
        doShutdown();
        return (T)this;
    }
    
    abstract protected void doConfigure() throws Exception;
    
    abstract protected void doStart() throws Exception;
    
    abstract protected void doJoin() throws Exception;
    
    abstract protected void doShutdown();
    
    protected void checkNotConfigured() {
        if (this.configured) {
            throw new IllegalStateException(this.getClass().getCanonicalName() + ".configure() already called");
        }
    }
    
    protected void checkConfigured() {
        if (!this.configured) {
            throw new IllegalStateException(this.getClass().getCanonicalName() + ".configure() not called yet");
        }
    }
    
    protected void checkStarted() {
        if (!this.started) {
            throw new IllegalStateException(this.getClass().getCanonicalName() + ".start() not called yet");
        }
    }
    
    // semi-builder pattern
    
    @Override
    public NinjaMode getNinjaMode() {
        return ninjaMode;
    }
    
    @Override
    public T ninjaMode(NinjaMode ninjaMode) {
        this.ninjaMode = ninjaMode;
        return (T)this;
    }
    
    @Override
    public String getExternalConfigurationPath() {
        return this.externalConfigurationPath;
    }
    
    @Override
    public T externalConfigurationPath(String externalConfigurationPath) {
        this.externalConfigurationPath = externalConfigurationPath;
        return (T)this;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public T name(String name) {
        this.name = name;
        return (T)this;
    }
    
    @Override
    public Integer getPort() {
        return this.port;
    }
    
    @Override
    public T port(int port) {
        this.port = port;
        return (T)this;
    }

    @Override
    public String getHost() {
        return this.host;
    }
    
    @Override
    public T host(String host) {
        this.host = host;
        return (T)this;
    }
    
    @Override
    public Long getIdleTimeout() {
        return idleTimeout;
    }
    
    @Override
    public T idleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return (T)this;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }
    
    @Override
    public T contextPath(String contextPath) {
        checkContextPath(contextPath);
        this.contextPath = contextPath;
        return (T)this;
    }
    
    @Override
    public Integer getSslPort() {
        return this.sslPort;
    }
    
    @Override
    public T sslPort(int sslPort) {
        this.sslPort = sslPort;
        return (T)this;
    }

    @Override
    public URI getSslKeystoreUri() {
        return this.sslKeystoreUri;
    }

    @Override
    public T sslKeystoreUri(URI keystoreUri) {
        this.sslKeystoreUri = keystoreUri;
        return (T)this;
    }

    @Override
    public String getSslKeystorePassword() {
        return this.sslKeystorePassword;
    }

    @Override
    public T sslKeystorePassword(String keystorePassword) {
        this.sslKeystorePassword = keystorePassword;
        return (T)this;
    }

    @Override
    public URI getSslTruststoreUri() {
        return this.sslTruststoreUri;
    }

    @Override
    public T sslTruststoreUri(URI truststoreUri) {
        this.sslTruststoreUri = truststoreUri;
        return (T)this;
    }

    @Override
    public String getSslTruststorePassword() {
        return this.sslTruststorePassword;
    }

    @Override
    public T sslTruststorePassword(String truststorePassword) {
        this.sslTruststorePassword = truststorePassword;
        return (T)this;
    }
    
    @Override
    public NinjaPropertiesImpl getNinjaProperties() {
        // only available after configure()
        checkConfigured();
        return ninjaProperties;
    }
    
    @Override
    public List<String> getServerUrls() {
        // only available after configure()
        checkConfigured();
        return serverUrls;
    }
    
    @Override
    public List<String> getBaseUrls() {
        // only available after configure()
        checkConfigured();
        return baseUrls;
    }
    
    @Override
    public boolean isPortEnabled() {
        return this.port != null && this.port > -1;
    }
    
    @Override
    public boolean isSslPortEnabled() {
        return this.sslPort != null && this.sslPort > -1;
    }
    
    protected List<String> createServerUrls() {
        // only available after configure()
        checkConfigured();
        
        List<String> urls = new ArrayList<>();
        
        if (isPortEnabled()) {
            urls.add(createServerUrl("http", getHost(), getPort()));
        }
        
        if (isSslPortEnabled()) {
            urls.add(createServerUrl("https", getHost(), getSslPort()));
        }
        
        return urls;
    }
    
    protected List<String> createBaseUrls() {
        // only available after configure()
        checkConfigured();
        
        List<String> urls = new ArrayList<>();
        
        if (isPortEnabled()) {
            urls.add(createBaseUrl("http", getHost(), getPort(), getContextPath()));
        }
        
        if (isSslPortEnabled()) {
            urls.add(createBaseUrl("https", getHost(), getSslPort(), getContextPath()));
        }
        
        return urls;
    }
    
    // helpful utilities for subclasses
    
    protected String createServerUrl(String scheme, String host, Integer port) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(scheme);
        sb.append("://");
        sb.append((host != null ? host : "localhost"));
        
        if (("http".equals(scheme) && port != 80) || ("https".equals(scheme) && port != 443)) {
            sb.append(":");
            sb.append(port);
        }
        
        return sb.toString();
    }
    
    protected String createBaseUrl(String scheme, String host, Integer port, String context) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(createServerUrl(scheme, host, port));
        
        if (StringUtils.isNotEmpty(context)) {
            sb.append(context);
        }
        
        return sb.toString();
    }
    
    protected Exception tryToUnwrapInjectorException(Exception exception) {
        Throwable cause = exception.getCause();
        if (cause != null && cause instanceof CreationException) {
            return (CreationException)cause;
        } else {
            return exception;
        }
    }
    
    protected String getLoggableIdentifier() {
        // build list of ports
        StringBuilder ports = new StringBuilder();
        
        if (isPortEnabled()) {
            ports.append(getPort());
        }
        
        if (isSslPortEnabled()) {
            if (ports.length() > 0) {
                ports.append(", ");
            }
            ports.append(getSslPort());
            ports.append("/ssl");
        }
        
        StringBuilder s = new StringBuilder();
        
        s.append("on ");
        
        s.append(Optional.ofNullable(getHost()).orElse("<all>"));
        s.append(":");
        s.append(ports);

        return s.toString();
    }
    
    protected void logBaseUrls() {
        logger.info("-------------------------------------------------------");
        logger.info("Ninja application running at");
        
        List<String> uris = getBaseUrls();
        for (String uri : uris) {
            logger.info(" => {}", uri);
        }
        
        logger.info("-------------------------------------------------------");
    }
    
    protected SSLContext createSSLContext() throws Exception {
        if (this.sslKeystoreUri == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key " + KEY_NINJA_SSL_KEYSTORE_URI
                    + " has empty value.  Please check your configuration file.");
        }
        
        if (this.sslKeystorePassword == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key " + KEY_NINJA_SSL_KEYSTORE_PASSWORD
                    + " has empty value.  Please check your configuration file.");
        }
        
        if (this.sslTruststoreUri == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key " + KEY_NINJA_SSL_TRUSTSTORE_URI
                    + " has empty value.  Please check your configuration file.");
        }
        
        if (this.sslTruststorePassword == null) {
            throw new IllegalStateException("Unable to create SSL context. Configuration key " + KEY_NINJA_SSL_TRUSTSTORE_PASSWORD
                    + " has empty value.  Please check your configuration file.");
        }
        
        return StandaloneHelper.createSSLContext(this.sslKeystoreUri, this.sslKeystorePassword.toCharArray(),
            this.sslTruststoreUri, this.sslTruststorePassword.toCharArray());
    }
}
