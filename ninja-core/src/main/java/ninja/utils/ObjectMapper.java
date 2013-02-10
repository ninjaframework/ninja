package ninja.utils;

import java.lang.reflect.Field;
import java.util.Map;

import ninja.Context;

import com.google.common.collect.Maps;

public class ObjectMapper {

    /**
     * Converts an object to a Map. Does this by taking the fields and
     * puts the as key names into the map.
     * 
     * @param object
     * @return A map with field names as key and corresponding field content as values..
     */
    public static Map<String, Object> convertObjectToMap(Object object) {
        
        Map<String, Object> map = Maps.newHashMap();
        
        for (Field field : object.getClass().getFields()) {
            
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
             
            try {
                map.put(field.getName(), field.get(object));
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        
        return map;
        
        
        
    }

}
