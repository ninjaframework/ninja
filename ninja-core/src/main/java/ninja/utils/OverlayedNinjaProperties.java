/**
 * Copyright (C) 2012-2018 the original author or authors.
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

import com.google.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import org.apache.commons.lang.StringUtils;

/**
 * Helper utility for handling Ninja properties using a simple "overlayed" view
 * of them.  Does not modify NinjaProperties in any way.  Simply adds an overlay
 * of other possible sources of the same property and helps get the value according
 * to an order of precedence:
 * 
 * <ul>
 *  <li>currentValue</li>
 *  <li>systemProperty</li>
 *  <li>configProperty</li>
 *  <li>defaultValue</li>
 * </ul>
 */
@Singleton
public class OverlayedNinjaProperties {
    
    final private NinjaProperties ninjaProperties;

    @Inject
    public OverlayedNinjaProperties(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    public String get(String key, String currentValue, String defaultValue) {
        // conf/application.conf OR fallback to default value
        String value = ninjaProperties.getWithDefault(key, defaultValue);
        
        // override with system property
        value = System.getProperty(key, value);
        
        // override with current value
        if (StringUtils.isNotEmpty(currentValue)) {
            value = currentValue;
        }
        
        return value;
    }
    
    public Boolean getBoolean(String key, Boolean currentValue, Boolean defaultValue) {
        String value = get(key, safeToString(currentValue), safeToString(defaultValue));
            
        if (StringUtils.isEmpty(value)) {
            return null;
        }

        if ("true".equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if ("false".equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            throw new IllegalArgumentException("Unable to convert property '" + key + "' with value '" + value + "' to a Boolean");
        }
    }
    
    public Integer getInteger(String key, Integer currentValue, Integer defaultValue) {
        String value = null;
        try {
            value = get(key, safeToString(currentValue), safeToString(defaultValue));
            
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            
            return Integer.valueOf(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert property '" + key + "' with value '" + value + "' to an Integer", e);
        }
    }
    
    public Long getLong(String key, Long currentValue, Long defaultValue) {
        String value = null;
        try {
            value = get(key, safeToString(currentValue), safeToString(defaultValue));
            
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            
            return Long.valueOf(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert property '" + key + "' with value '" + value + "' to a Long", e);
        }
    }
    
    public URI getURI(String key, URI currentValue, URI defaultValue) {
        String value = null;
        try {
            value = get(key, safeToString(currentValue), safeToString(defaultValue));
            
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            
            return new URI(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to convert property '" + key + "' with value '" + value + "' to a URI", e);
        }
    }
    
    private String safeToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
}
