/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class NinjaModeHelper {
    
    static Logger logger = LoggerFactory.getLogger(NinjaModeHelper.class);
    
    /**
     * returns an empty Optional<NinjaMode> if no mode is set. Or the valid mode
     * set via a System Property called "ninja.mode".
     * 
     * E.g. under mvn you can use mvn ... -Dninja.mode=prod or so. Valid values
     * for ninja.mode are "prod", "dev", "test".
     * 
     * @return The valid mode set via a System Property called "ninja.mode"
     *          or Optional absent if we cannot get one.
     */
    public static Optional<NinjaMode> determineModeFromSystemProperties() {
        
        NinjaMode ninjaMode = null;
        
        // Get mode possibly set via a system property
        String modeFromGetSystemProperty = System
                .getProperty(NinjaConstant.MODE_KEY_NAME);
        
        // If the user specified a mode we set the mode accordingly:
        if (modeFromGetSystemProperty != null) {

            if (modeFromGetSystemProperty.equals(NinjaConstant.MODE_TEST)) {
                
                ninjaMode = NinjaMode.test;
                
            } else if (modeFromGetSystemProperty.equals(NinjaConstant.MODE_DEV)) {
                
                ninjaMode = NinjaMode.dev;
                
            } else if (modeFromGetSystemProperty.equals(NinjaConstant.MODE_PROD)) {
                
                ninjaMode = NinjaMode.prod;
                
            }

        }
        
        return Optional.fromNullable(ninjaMode);
        
    }
    
    /**
     * returns NinjaMode.dev if no mode is set. Or the valid mode
     * set via a System Property called "ninja.mode".
     * 
     * E.g. under mvn you can use mvn ... -Dninja.mode=prod or so. Valid values
     * for ninja.mode are "prod", "dev", "test".
     * 
     * @return The valid mode set via a System Property called "ninja.mode"
     *          or NinjaMode.dev if it is not set.
     */
    public static NinjaMode determineModeFromSystemPropertiesOrProdIfNotSet() {
        
        Optional<NinjaMode> ninjaModeOptional = determineModeFromSystemProperties();
        NinjaMode ninjaMode;
        
        if (!ninjaModeOptional.isPresent()) {
            ninjaMode = NinjaMode.prod;
        } else {
            ninjaMode = ninjaModeOptional.get();
        }
        
        logger.info("Ninja is running in mode {}", ninjaMode.toString());
        
        return ninjaMode;
        
    }

}
