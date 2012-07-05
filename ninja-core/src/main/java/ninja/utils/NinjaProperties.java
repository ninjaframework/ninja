package ninja.utils;

import com.google.inject.ImplementedBy;

@ImplementedBy(NinjaPropertiesImpl.class)
public interface NinjaProperties {
    
    /**
     * Get a property file from the initially loaded configuration.
     * 
     * @param key
     * 
     */
    public String get(String key);

}
