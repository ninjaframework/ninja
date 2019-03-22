/**
 * Copyright (C) 2012-2019 the original author or authors.
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

package ninja.logging;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.google.common.io.Resources;

/**
 *
 * Helps to configure Logback from application.conf via a property named
 * "logback.configurationFile. Allows to load arbitrary configurations that fit
 * your application.
 *
 * @author ra
 */
public class LogbackConfigurator {

    public final static String LOGBACK_CONFIGURATION_FILE_PROPERTY = "logback.configurationFile";

    private final static Logger logger = LoggerFactory.getLogger(LogbackConfigurator.class);

    public static void initConfiguration(NinjaProperties ninjaProperties) {

        // First of all check if someone set 
        // -Dlogback.configurationFile=logback_prod.xml
        // This is a default property of Logback (http://logback.qos.ch/manual/configuration.html).
        // If that is the case we do nothing and leave everything to LogBack
        if (System.getProperty(LOGBACK_CONFIGURATION_FILE_PROPERTY) != null) {
            return;
        }

        // If not we check if we got a logback configurationFile declared
        // in our application.conf and load it...
        String logbackConfigurationFile
                = ninjaProperties.get(LOGBACK_CONFIGURATION_FILE_PROPERTY);
        
        if (logbackConfigurationFile == null) {
            return;
        }
        
        URL logbackConfigurationFileAsURL 
            = getUrlForStringFromClasspathAsFileOrUrl(logbackConfigurationFile);
        
        if (logbackConfigurationFileAsURL == null) {
            logger.error("Cannot configure logger from {} provided in application.conf", logbackConfigurationFile);
            return;
        }
        
        // At that point we got a valid Url for configuring Logback. Let's do it :)

        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {

            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(logbackConfigurationFileAsURL);

        } catch (JoranException je) {
            // StatusPrinter will handle this
        }

        logger.info("Successfully configured application logging from: {}", logbackConfigurationFileAsURL);
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);


    }

    /**
     * Looks up a potential file from
     * 1) The classpahth
     * 2) From the filesystem
     * 3) From an URL
     * 
     * @param logbackConfigurationFile
     * @return null if not found or a valid url created from the logbackConfigurationFile
     */
    protected static URL getUrlForStringFromClasspathAsFileOrUrl(String logbackConfigurationFile) {

        URL url = null;

        try {
            url = Resources.getResource(logbackConfigurationFile);
        } catch (IllegalArgumentException ex) {
            // doing nothing intentionally..
        }

        if (url == null) {
            // configuring from file:
            try {
                File file = new File(logbackConfigurationFile);
                
                if (file.exists()) {
                    url = new File(logbackConfigurationFile).toURI().toURL();
                }
                
            } catch (MalformedURLException ex) {
                // doing nothing intentionally..
            }

        }

        if (url == null) {
            try {
                // we assume we got a real http://... url here...
                url = new URL(logbackConfigurationFile);
            } catch (MalformedURLException ex) {
                // doing nothing intentionally..
            }

        }

        return url;
    }
}
