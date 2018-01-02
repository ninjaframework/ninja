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

package ninja.utils;

import java.io.File;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.apache.commons.configuration.SystemConfiguration;

@Singleton
public class NinjaPropertiesImpl implements NinjaProperties {

    private static final Logger logger = LoggerFactory
            .getLogger(NinjaPropertiesImpl.class);

    private final NinjaMode ninjaMode;
    
    private String contextPath = "";

    private final String ERROR_KEY_NOT_FOUND = "Key %s does not exist. Please include it in your application.conf. Otherwise this app will not work";

    /**
     * This is the final configuration holding all information from 
     * 1. application.conf. 
     * 2. Special properties for the mode you are running on extracted 
     *    from application.conf 
     * 3. An external configuration file defined
     *    by a system property and on the classpath.
     */
    private CompositeConfiguration compositeConfiguration;

    public NinjaPropertiesImpl(
            NinjaMode ninjaMode) {
        this(ninjaMode, null);
    }
    
    public NinjaPropertiesImpl(
            NinjaMode ninjaMode, String externalConfigurationPath) {
        
        this.ninjaMode = ninjaMode;

        // This is our main configuration.
        // In the following we'll read the individual configurations and merge
        // them into the composite configuration at the end.
        compositeConfiguration = new CompositeConfiguration();

        // That is the default config.
        PropertiesConfiguration defaultConfiguration = null;

        // Config of prefixed mode corresponding to current mode (eg.
        // %test.myproperty=...)
        Configuration prefixedDefaultConfiguration = null;

        // (Optional) Config set via a system property
        PropertiesConfiguration externalConfiguration = null;

        // (Optional) Config of prefixed mode corresponding to current mode (eg.
        // %test.myproperty=...)
        Configuration prefixedExternalConfiguration = null;
        
        // (Optional) Config of keys via system property
        Configuration systemPropertiesConfiguration = null;
        
        // (Optional) Config of prefixed keys via system property
        Configuration prefixedSystemPropertiesConfiguration = null;

        // First step => load application.conf and also merge properties that
        // correspond to a mode into the configuration.

        defaultConfiguration = SwissKnife
                .loadConfigurationInUtf8(NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);

        if (defaultConfiguration != null) {
            // Second step:
            // Copy special prefix of mode to parent configuration
            // By convention it will be something like %test.myproperty
            prefixedDefaultConfiguration = defaultConfiguration.subset("%"
                    + ninjaMode.name());

            // allow application.conf to be reloaded on changes in dev mode
            if (NinjaMode.dev == ninjaMode) {
                defaultConfiguration
                        .setReloadingStrategy(new FileChangedReloadingStrategy());
            }

        } else {

            // If the property was set, but the file not found we emit
            // a RuntimeException
            String errorMessage = String
                    .format("Error reading configuration file. Make sure you got a default config file %s",
                            NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);

            logger.error(errorMessage);

            throw new RuntimeException(errorMessage);
        }

        // third step => load external configuration when a system property is defined.
        String ninjaExternalConf = externalConfigurationPath;
        
        if (ninjaExternalConf == null) {
            // if not set fallback to system property
            ninjaExternalConf = System.getProperty(NINJA_EXTERNAL_CONF);
        }

        if (ninjaExternalConf != null) {

            // only load it when the property is defined.

            externalConfiguration = SwissKnife
                    .loadConfigurationInUtf8(ninjaExternalConf);

            // this should not happen:
            if (externalConfiguration == null) {

                String errorMessage = String
                        .format("Ninja was told to use an external configuration%n"
                                + " %s = %s %n."
                                + "But the corresponding file cannot be found.%n"
                                + " Make sure it is visible to this application and on the classpath.",
                                NINJA_EXTERNAL_CONF, ninjaExternalConf);

                logger.error(errorMessage);

                throw new RuntimeException(errorMessage);

            } else {

                // allow the external configuration to be reloaded at
                // runtime based on detected file changes
                final boolean shouldReload = Boolean.getBoolean(NINJA_EXTERNAL_RELOAD);
                if (shouldReload) {
                    externalConfiguration
                            .setReloadingStrategy(new FileChangedReloadingStrategy());
                }

                // Copy special prefix of mode to parent configuration
                // By convention it will be something like %test.myproperty
                prefixedExternalConfiguration = externalConfiguration
                        .subset("%" + ninjaMode.name());
            }

        }
        
        // fourth step: system properties ultimate override of any key
        systemPropertiesConfiguration = new SystemConfiguration();
        
        prefixedSystemPropertiesConfiguration = systemPropertiesConfiguration
            .subset("%" + ninjaMode.name());

        // /////////////////////////////////////////////////////////////////////
        // Finally add the stuff to the composite configuration
        // Note: Configurations added earlier will overwrite configurations
        // added later.
        // /////////////////////////////////////////////////////////////////////
        
        if (prefixedSystemPropertiesConfiguration != null) {
            compositeConfiguration
                    .addConfiguration(prefixedSystemPropertiesConfiguration);
        }
        
        if (systemPropertiesConfiguration != null) {
            compositeConfiguration
                    .addConfiguration(systemPropertiesConfiguration);
        }
        
        if (prefixedExternalConfiguration != null) {
            compositeConfiguration
                    .addConfiguration(prefixedExternalConfiguration);
        }

        if (externalConfiguration != null) {
            compositeConfiguration.addConfiguration(externalConfiguration);
        }

        if (prefixedDefaultConfiguration != null) {
            compositeConfiguration
                    .addConfiguration(prefixedDefaultConfiguration);
        }

        if (defaultConfiguration != null) {
            compositeConfiguration.addConfiguration(defaultConfiguration);
        }
        
        // /////////////////////////////////////////////////////////////////////
        // Check that the secret is set or generate a new one if the property
        // does not exist
        // /////////////////////////////////////////////////////////////////////
        NinjaPropertiesImplTool.checkThatApplicationSecretIsSet(
                isProd(), 
                new File("").getAbsolutePath(), 
                defaultConfiguration, 
                compositeConfiguration);

    }

    @Override
    public String get(String key) {

        String value;

        try {
            value = compositeConfiguration.getString(key);
        } catch (Exception e) {
            // Fail silently because we handle errors differently. Simply set
            // them null.
            value = null;
        }

        return value;

    }

    @Override
    public String getOrDie(String key) {

        String value = get(key);

        if (value == null) {
            logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
            throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
        } else {
            return value;
        }

    }

    @Override
    public Integer getInteger(String key) {

        Integer value;

        try {
            value = compositeConfiguration.getInt(key);
        } catch (Exception e) {
            // Fail silently because we handle errors differently. Simply set
            // them null.
            value = null;
        }

        return value;

    }

    @Override
    public Integer getIntegerOrDie(String key) {

        Integer value = getInteger(key);

        if (value == null) {
            logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
            throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
        } else {
            return value;
        }

    }

    @Override
    public Boolean getBooleanOrDie(String key) {

        Boolean value = getBoolean(key);

        if (value == null) {
            logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
            throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
        } else {
            return value;
        }

    }

    @Override
    public Boolean getBoolean(String key) {

        Boolean value;

        try {
            value = compositeConfiguration.getBoolean(key);
        } catch (Exception e) {
            // Fail silently because we handle errors differently. Simply set
            // them null.
            value = null;
        }

        return value;

    }

    public void setProperty(String key, String value) {
        compositeConfiguration.setProperty(key, value);
    }

    public void bindProperties(Binder binder) {
        Names.bindProperties(binder,
                ConfigurationConverter.getProperties(compositeConfiguration));
    }

    @Override
    public boolean isProd() {
        return (ninjaMode.equals(NinjaMode.prod));
    }

    @Override
    public boolean isDev() {
        return (ninjaMode.equals(NinjaMode.dev));
    }

    @Override
    public boolean isTest() {
        return (ninjaMode.equals(NinjaMode.test));
    }
    
    /**
     * Get the context path on which the application is running
     * 
     * That means:
     * - when running on root the context path is empty
     * - when running on context there is NEVER a trailing slash
     * 
     * We conform to the following rules:
     * Returns the portion of the request URI that indicates the context of the 
     * request. The context path always comes first in a request URI. 
     * The path starts with a "/" character but does not end with a "/" character. 
     * For servlets in the default (root) context, this method returns "". 
     * The container does not decode this string.
     * 
     * As outlined by: http://docs.oracle.com/javaee/6/api/javax/servlet/http/HttpServletRequest.html#getContextPath()
     * 
     * @return the context-path with a leading "/" or "" if running on root
     */
    @Override
    public String getContextPath() {
        
        return contextPath;
        
    }
    
    @Override
    public void setContextPath(String contextPath) {
        
        this.contextPath = contextPath;
        
    }

    @Override
    public Properties getAllCurrentNinjaProperties() {

        return ConfigurationConverter.getProperties(compositeConfiguration);

    }

    @Override
    public String[] getStringArray(String key) {
        String value = compositeConfiguration.getString(key);
        if (value != null) {
            return Iterables.toArray(Splitter.on(",").trimResults()
                    .omitEmptyStrings().split(value), String.class);
        } else {
            return null;
        }
    }

    @Override
    public String getWithDefault(String key, String defaultValue) {
        String value = get(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public Integer getIntegerWithDefault(String key, Integer defaultValue) {
        Integer value = getInteger(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public Boolean getBooleanWithDefault(String key, Boolean defaultValue) {
        Boolean value = getBoolean(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }
    
}
