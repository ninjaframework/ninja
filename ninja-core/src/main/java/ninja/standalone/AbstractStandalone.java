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

import com.google.inject.Injector;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractStandalone<T extends AbstractStandalone> implements Standalone<T> {
    // allow logger to take on persona of concrete class
    final protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // configuration (can be changed before configure())
    protected NinjaMode ninjaMode;
    protected String name;
    protected String host;
    protected Integer port;
    protected String context;
    protected Long idleTimeout;
    
    protected boolean configured;
    protected boolean started;
    // only available after configure()
    protected NinjaPropertiesImpl ninjaProperties;
    protected ConfigurationHelper configurationHelper;
    
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
            logger.error("Unable to start ", name, e);
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
        
        // create ninja properties & configuration helper
        this.ninjaProperties = new NinjaPropertiesImpl(this.ninjaMode);
        this.configurationHelper = new ConfigurationHelper(this.ninjaProperties);
        
        // current value or system property or conf/application.conf or default value
        host(configurationHelper.get(
                KEY_NINJA_HOST, this.host, DEFAULT_HOST));
        
        port(configurationHelper.get(
                KEY_NINJA_PORT, this.port, DEFAULT_PORT));
        
        context(configurationHelper.get(
                KEY_NINJA_CONTEXT, this.context, DEFAULT_CONTEXT));
        
        idleTimeout(configurationHelper.get(
                KEY_NINJA_IDLE_TIMEOUT, this.idleTimeout, DEFAULT_IDLE_TIMEOUT));
        
        doConfigure();
        
        this.configured = true;
        
        // save generated server name as ninja property
        this.ninjaProperties.setProperty(NinjaConstant.serverName, buildServerName());
        
        return (T)this;
    }
    
    @Override
    final public void start() throws Exception {
        if (!this.configured) {
            configure();
        }
        doStart();
        this.started = true;
    }
    
    @Override
    final public void join() throws Exception{
        checkStarted();
        doJoin();
    }
    
    @Override
    final public void shutdown() {
        doShutdown();
    }
    
    abstract protected void doConfigure() throws Exception;
    
    abstract protected void doStart() throws Exception;
    
    abstract protected void doJoin() throws Exception;
    
    abstract protected void doShutdown();
    
    private String buildServerName() {
        checkConfigured();
        // can eventually be smart to account for https down the road
        // should be only place in standalone process for building server name
        // its value will be saved in NinjaProperties after successful start()
        return new StringBuilder()
            .append("http://")
            .append((this.host == null ? "localhost" : this.host))
            .append(":")
            .append(this.port)
            .toString();
    }
    
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
    public String getContext() {
        return context;
    }
    
    @Override
    public T context(String context) {
        this.context = context;
        return (T)this;
    }
    
    @Override
    public NinjaPropertiesImpl getNinjaProperties() {
        // only available after configure()
        checkConfigured();
        return ninjaProperties;
    }
}
