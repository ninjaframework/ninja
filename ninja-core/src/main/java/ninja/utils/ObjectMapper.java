package ninja.utils;

import java.lang.reflect.Field;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;


public class ObjectMapper {
    
    private static final Logger logger = LoggerFactory
            .getLogger(ObjectMapper.class);

    /**
     * Converts an object to a Map. Does this by taking the fields and
     * puts the as key names into the map.
     * 
     * @param object
     * @return A map with field names as key and corresponding field content as values..
     */
    public static Map<String, Object> convertObjectToMap(Object object) {
        
        Map<String, Object> map = Maps.newHashMap();
        
        for (Field field : object.getClass().getDeclaredFields()) {
            
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
             
            try {
                Object value = field.get(object);
                if (value == null) {
                    map.put(field.getName(), "");
                } else {
                    map.put(field.getName(), value);
                }
            } catch (IllegalArgumentException e) {
                logger.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
            
        }
        
        return map;
        
        
        
    }

}
