package ninja.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NinjaPropertiesImplTest {
    
    @Test
    public void testSkippingThroughModesWorks() {
        
        //check that mode tests works:
        System.setProperty("mode", "test");        
        NinjaPropertiesImpl ninjaPropertiesImpl = new NinjaPropertiesImpl();       
        assertEquals("test_testproperty", ninjaPropertiesImpl.get("testproperty"));       
        
        //check that mode dev works:
        System.setProperty("mode", "dev");        
        ninjaPropertiesImpl = new NinjaPropertiesImpl();       
        assertEquals("dev_testproperty", ninjaPropertiesImpl.get("testproperty"));
        assertEquals("secret", ninjaPropertiesImpl.get("application.secret"));
        
        //remove property => we expect that the dev property is used as default value
        System.clearProperty("mode");     
        ninjaPropertiesImpl = new NinjaPropertiesImpl();       
        assertEquals("dev_testproperty", ninjaPropertiesImpl.get("testproperty"));
        assertEquals("secret", ninjaPropertiesImpl.get("application.secret"));
        
        //and in a completely different mode with no "%"-prefixed key the default value use used
        System.setProperty("mode", "prod");     
        ninjaPropertiesImpl = new NinjaPropertiesImpl();       
        assertEquals("testproperty_without_prefix", ninjaPropertiesImpl.get("testproperty"));
        assertEquals("secret", ninjaPropertiesImpl.get("application.secret"));
        
        //tear down
        System.clearProperty("mode");
 
    }

}
