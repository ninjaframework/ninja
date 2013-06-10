package ninja.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SwissKnifeTest {

    @Test
    public void testGetRealClassNameLowerCamelCase() {
        
        MySuperTestObject mySuperTestObject = new MySuperTestObject();
        
        assertEquals("mySuperTestObject",
                SwissKnife.getRealClassNameLowerCamelCase(mySuperTestObject));
         
    }
    
    // just for testing that camel case conversion stuff works
    public class MySuperTestObject {}

}
