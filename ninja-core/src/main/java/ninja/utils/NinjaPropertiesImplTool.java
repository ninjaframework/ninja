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

package ninja.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

public class NinjaPropertiesImplTool {

    private static final Logger logger = LoggerFactory
            .getLogger(NinjaPropertiesImplTool.class);

    /**
     * This method checks that your configurations have set a 
     * application.secret=23r213r12r123
     * 
     * If application.secret is missing or is empty it will do the following:
     * - In dev and test mode it'll generate a new application secret and write the secret
     *   to both src/main/java/conf/application.conf and the classes dir were the compiled stuff
     *   goes.
     * - In prod it will throw a runtime exception and stop the server.
     */
    public static void checkThatApplicationSecretIsSet(
                                                boolean isProd,
                                                String baseDirWithoutTrailingSlash,                                                
                                                PropertiesConfiguration defaultConfiguration,
                                                Configuration compositeConfiguration) {
        
        String applicationSecret = compositeConfiguration.getString(NinjaConstant.applicationSecret);
        
        if (applicationSecret == null
                || applicationSecret.isEmpty()) {
            
            // If in production we stop the startup process. It simply does not make
            // sense to run in production if the secret is not set.
            if (isProd) {
                String errorMessage = "Fatal error. Key application.secret not set. Please fix that.";
                logger.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            
            
            logger.info("Key application.secret not set. Generating new one and setting in conf/application.conf.");
            
            // generate new secret
            String secret = SecretGenerator.generateSecret();
            
            // set in overall composite configuration => this enables this instance to 
            // start immediately. Otherwise we would have another build cycle.
            compositeConfiguration.setProperty(NinjaConstant.applicationSecret, secret);
            
            // defaultConfiguration is: conf/application.conf (not prefixed)
            defaultConfiguration.setProperty(NinjaConstant.applicationSecret, secret);
            
            try {
                
                // STEP 1: Save in source directories:
                // save to compiled version => in src/main/target/
                String pathToApplicationConfInSrcDir = baseDirWithoutTrailingSlash + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator + NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION;
                Files.createParentDirs(new File(pathToApplicationConfInSrcDir));
                // save to source
                defaultConfiguration.save(pathToApplicationConfInSrcDir);

                // STEP 2: Save in classes dir (target/classes or similar).
                // save in target directory (compiled version aka war aka classes dir)
                defaultConfiguration.save();
                
            } catch (ConfigurationException e) {
                logger.error("Error while saving new secret to application.conf.", e);
            } catch (IOException e) {
                logger.error("Error while saving new secret to application.conf.", e);
            } 
            
        }
        
    }
    

}
