package ninja.utils;

import static org.junit.Assert.assertEquals;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

public class NinjaConstantTest {

    /**
     * This testcase makes sure that all constants defined
     * in NinjaConstant are working.
     * 
     * File conf/all_constants.conf simply contains all contstants.
     * We simply read them in and check if the constants are okay.
     * 
     * Aim is to prevent stupid spelling mistakes.
     * 
     */
    @Test
    public void testAllConstants() {

        Configuration configuration =
                SwissKnife.loadConfigurationFromClasspathInUtf8("conf/all_constants.conf", this
                        .getClass());

        assertEquals("LANGUAGES", configuration.getString(NinjaConstant.applicationLanguages));
        
        assertEquals("PREFIX", configuration.getString(NinjaConstant.applicationCookiePrefix));

        assertEquals("NAME", configuration.getString(NinjaConstant.applicationName));
        
        assertEquals("SECRET", configuration.getString(NinjaConstant.applicationSecret));
        
        assertEquals("SERVER_NAME", configuration.getString(NinjaConstant.serverName));

        assertEquals(9999, configuration.getInt(NinjaConstant.sessionExpireTimeInSeconds));

        assertEquals(false, configuration.getBoolean(NinjaConstant.sessionSendOnlyIfChanged));

        assertEquals(false, configuration.getBoolean(NinjaConstant.sessionTransferredOverHttpsOnly));

    }

}
