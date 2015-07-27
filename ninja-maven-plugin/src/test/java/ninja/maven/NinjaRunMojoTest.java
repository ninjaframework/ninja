package ninja.maven;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NinjaRunMojoTest {

    @Test
    public void testSplitUnescapedSpaces() {
        
        // test are simplified by using _ instead of \\ as it is needed to double them in java
        // and assertion is tested on joined for easier reading and writing of tests
        
        // simple and stupid tests
        testSplitUnescapedSpaceUsingUnderscore("","");
        testSplitUnescapedSpaceUsingUnderscore(""," ");
        testSplitUnescapedSpaceUsingUnderscore("a","a");
        testSplitUnescapedSpaceUsingUnderscore("a"," a");
        testSplitUnescapedSpaceUsingUnderscore("a","a ");
        testSplitUnescapedSpaceUsingUnderscore("a"," a ");
        
        // complex tests with \
        testSplitUnescapedSpaceUsingUnderscore("a,b,c","a b c");
        testSplitUnescapedSpaceUsingUnderscore("a b,c","a_ b c");
        testSplitUnescapedSpaceUsingUnderscore("a_,b,c","a__ b c");
        testSplitUnescapedSpaceUsingUnderscore("a_ b,c","a___ b c");
        testSplitUnescapedSpaceUsingUnderscore("a__,b,c","a____ b c");
        testSplitUnescapedSpaceUsingUnderscore("a__ b,c","a_____ b c");
        
        // take one complex and add \ elsewhere
        testSplitUnescapedSpaceUsingUnderscore("_a__ b,c","_a_____ b c");
        testSplitUnescapedSpaceUsingUnderscore("_a__ b,c","__a_____ b c");
        testSplitUnescapedSpaceUsingUnderscore("__a__ b,c","___a_____ b c");
        testSplitUnescapedSpaceUsingUnderscore("__a__ b,c","____a_____ b c");
        
        // special tests at the beginning
        testSplitUnescapedSpaceUsingUnderscore("b,c"," b c");
        testSplitUnescapedSpaceUsingUnderscore(" b,c","_ b c");
        testSplitUnescapedSpaceUsingUnderscore("_,b,c","__ b c");
        testSplitUnescapedSpaceUsingUnderscore("_ b,c","___ b c");
        testSplitUnescapedSpaceUsingUnderscore("__,b,c","____ b c");
        testSplitUnescapedSpaceUsingUnderscore("__ b,c","_____ b c");
    }
    
    private void testSplitUnescapedSpaceUsingUnderscore(String expected, String value) {
        value = value.replace("_", "\\");
        String[] a = NinjaRunMojo.splitUnescapedSpaces(value);
        String actual = String.join(",", a);
        actual = actual.replace("\\", "_");
        assertEquals(expected, actual);
    }

}
