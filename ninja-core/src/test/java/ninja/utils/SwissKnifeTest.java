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

    @Test
    public void testConvert() {
        
        String aString = "aString";
        String str = "1024";
        String doubleStr = "1024.24";
        String boolStr = "true";
        String byteStr = "89";
        char charStr = 'x';
        String emptyString = "";

        assertEquals(Integer.valueOf(str), SwissKnife.convert(str, int.class));
        assertEquals(Integer.valueOf(str), SwissKnife.convert(str, Integer.class));
        assertEquals(Long.valueOf(str), SwissKnife.convert(str, long.class));
        assertEquals(Float.valueOf(doubleStr), SwissKnife.convert(doubleStr, float.class));
        assertEquals(Float.valueOf(doubleStr), SwissKnife.convert(doubleStr, Float.class));
        assertEquals(Double.valueOf(doubleStr), SwissKnife.convert(doubleStr, double.class));
        assertEquals(Double.valueOf(doubleStr), SwissKnife.convert(doubleStr, Double.class));
        assertEquals(Boolean.valueOf(boolStr), SwissKnife.convert(boolStr, boolean.class));
        assertEquals(Boolean.valueOf(boolStr), SwissKnife.convert(boolStr, Boolean.class));
        assertEquals(Byte.valueOf(byteStr), SwissKnife.convert(byteStr, byte.class));
        assertEquals(Byte.valueOf(byteStr), SwissKnife.convert(byteStr, Byte.class));
        assertEquals(Character.valueOf(charStr), SwissKnife.convert(String.valueOf(charStr), char.class));
        assertEquals(Character.valueOf(charStr), SwissKnife.convert(String.valueOf(charStr), Character.class));
        assertEquals(null, SwissKnife.convert(String.valueOf(emptyString), char.class));
        assertEquals(null, SwissKnife.convert(String.valueOf(emptyString), Character.class));
        
        assertEquals(null, SwissKnife.convert(String.valueOf(aString), StringBuffer.class));
        assertEquals(null, SwissKnife.convert(String.valueOf(aString), Integer.class));
        
        assertEquals(aString, SwissKnife.convert(String.valueOf(aString), String.class));
    }

    // just for testing that camel case conversion stuff works
    public class MySuperTestObject {
    }

}
