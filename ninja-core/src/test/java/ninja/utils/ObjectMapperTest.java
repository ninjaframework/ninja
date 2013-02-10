package ninja.utils;

import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class ObjectMapperTest {

    
    public class SimpleClass {
        
        public String name;
        public String test;
        
    }
    
    @Test
    public void testSimple() {
        
        SimpleClass simpleClass = new SimpleClass();
        simpleClass.name = "simpleName";
        simpleClass.test = "simpleTest";
        
        Map<String, Object> map = ObjectMapper.convertObjectToMap(simpleClass);
        
        assertEquals(2, map.entrySet().size());
        assertEquals("simpleName", map.get("name"));
        assertEquals("simpleTest", map.get("test"));
        
        
        
    }
    
    
    
}
