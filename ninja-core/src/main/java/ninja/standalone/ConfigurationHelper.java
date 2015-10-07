/*
 * Copyright 2015 joelauer.
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
package ninja.standalone;

import ninja.utils.NinjaProperties;
import org.apache.commons.lang.StringUtils;

/**
 * Helper utility for configuring standalone applications using 4 different
 * values that take precedence over each other.
 * 
 * current value OR system property OR ninja property OR default value
 * 
 * @author joelauer
 */
public class ConfigurationHelper {
    
    final private NinjaProperties ninjaProperties;

    public ConfigurationHelper(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    public String get(String propertyName, String currentValue, String defaultValue) {
        // conf/application.conf OR fallback to default value
        String value = ninjaProperties.getWithDefault(propertyName, defaultValue);
        
        // override with system property
        value = System.getProperty(propertyName, value);
        
        // override with current value
        if (StringUtils.isNotEmpty(currentValue)) {
            value = currentValue;
        }
        
        return value;
    }
    
    public Integer get(String propertyName, Integer currentValue, Integer defaultValue) {
        try {
            String value = get(propertyName, safeToString(currentValue), safeToString(defaultValue));
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert property '" + propertyName + "' to int", e);
        }
    }
    
    public Long get(String propertyName, Long currentValue, Long defaultValue) {
        try {
            String value = get(propertyName, safeToString(currentValue), safeToString(defaultValue));
            return Long.valueOf(value);
        } catch (Exception e) {
            throw new RuntimeException("Unable to convert property '" + propertyName + "' to long", e);
        }
    }
    
    public String safeToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
}
