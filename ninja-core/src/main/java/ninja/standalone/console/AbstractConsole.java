/*
 * Copyright 2018 ninjaframework.
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
package ninja.standalone.console;

import com.google.inject.CreationException;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import ninja.utils.OverlayedNinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Console that implements most functionality required to write
 * a concrete Console.  See NinjaConsole for example concrete implementation.
 * @param <T> The concrete console implementation
 */
public abstract class AbstractConsole<T extends AbstractConsole> implements Console<T> {
    // allow logger to take on persona of concrete class
    final protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    // can all be changed prior to configure()
    protected NinjaMode ninjaMode;
    protected String externalConfigurationPath;
    protected String name;
    // internal state
    protected boolean configured;
    protected boolean started;
    protected NinjaPropertiesImpl ninjaProperties; // after configure()
    protected OverlayedNinjaProperties overlayedNinjaProperties; // after configure()

    public AbstractConsole(String name) {
        // set mode as quickly as possible (can still be changed before configure())
        this.ninjaMode = NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet();
        this.name = name;
        this.configured = false;
        this.started = false;
    }

    @Override
    public final T configure() throws Exception {
        checkNotConfigured();
        
        // create ninja properties & overlayed view
        this.ninjaProperties = new NinjaPropertiesImpl(this.ninjaMode, this.externalConfigurationPath);
        this.overlayedNinjaProperties = new OverlayedNinjaProperties(this.ninjaProperties);
        
        this.doPreConfigure();
        this.doConfigure();
        
        this.configured = true;
        
        this.doPostConfigure();
        
        return (T) this;
    }
    
    @Override
    public final T start() throws Exception {
        if (!this.configured) {
            this.configure();
        }
        
        this.doStart();
        
        this.started = true;
        
        this.logStarted();
        
        return (T) this;
    }

    @Override
    public final T shutdown() {
        this.doShutdown();
        return (T) this;
    }

    protected void doPreConfigure() throws Exception {
        
    }
    
    protected void doPostConfigure() throws Exception {
        
    }
    
    protected abstract void doConfigure() throws Exception;

    protected abstract void doStart() throws Exception;

    protected abstract void doShutdown();

    protected void logStarted() {
        logger.info("-------------------------------------------------------");
        logger.info("Ninja console application running...");
        logger.info("-------------------------------------------------------");
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
        return (T) this;
    }

    @Override
    public String getExternalConfigurationPath() {
        return this.externalConfigurationPath;
    }

    @Override
    public T externalConfigurationPath(String externalConfigurationPath) {
        this.externalConfigurationPath = externalConfigurationPath;
        return (T) this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T name(String name) {
        this.name = name;
        return (T) this;
    }

    @Override
    public NinjaPropertiesImpl getNinjaProperties() {
        // only available after configure()
        checkConfigured();
        return ninjaProperties;
    }

    protected Exception tryToUnwrapInjectorException(Exception exception) {
        Throwable cause = exception.getCause();
        if (cause != null && cause instanceof CreationException) {
            return (CreationException) cause;
        } else {
            return exception;
        }
    }
    
}
