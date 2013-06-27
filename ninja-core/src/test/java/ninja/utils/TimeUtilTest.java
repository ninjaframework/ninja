package ninja.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeUtilTest {

    @Test
    public void test() {
        assertEquals(86400, TimeUtil.parseDuration("1d"));
        assertEquals(10, TimeUtil.parseDuration("10s"));
        assertEquals(2592000, TimeUtil.parseDuration("30d"));
        assertEquals(2592000, TimeUtil.parseDuration(null));
        
        boolean catchedException = false;
        try {
            TimeUtil.parseDuration("NOT_A_VALID_INPUT");
            
        } catch (IllegalArgumentException e) {
                catchedException = true;
        }

        assertTrue(catchedException); 
        
    }

}
