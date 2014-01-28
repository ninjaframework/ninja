package ninja.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * CookieDataCodec and CookieDataCodecTest are imported from Play Framework.
 * 
 * Enables us to use the same sessions as Play Framework if
 * the secret is the same.
 * 
 * Also really important because we want to make sure that our client
 * side session mechanism is widely used and stable.
 * We don't want to reinvent 
 * the wheel of securely encoding / decoding and signing cookie data.
 * 
 * All praise goes to Play Framework and their awesome work.
 * 
 */
public class NinjaModeHelperTest {
    
    @Test
    public void testNinjaModeHelperWorksWithNoModeSet() {
        
        assertEquals(false, NinjaModeHelper.determineModeFromSystemProperties().isPresent());
        assertEquals(NinjaMode.prod, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        
    }

    @Test
    public void testNinjaModeHelperWorksWithTestSet() {
        
        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_TEST);
        
        assertEquals(NinjaMode.test, NinjaModeHelper.determineModeFromSystemProperties().get());
        assertEquals(NinjaMode.test, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);
        
    }
    
    @Test
    public void testNinjaModeHelperWorksWithDevSet() {
        
        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_DEV);
        
        assertEquals(NinjaMode.dev, NinjaModeHelper.determineModeFromSystemProperties().get());
        assertEquals(NinjaMode.dev, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);
        
    }
    
    @Test
    public void testNinjaModeHelperWorksWithProdSet() {
        
        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_PROD);
        
        assertEquals(NinjaMode.prod, NinjaModeHelper.determineModeFromSystemProperties().get());
        assertEquals(NinjaMode.prod, NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);
        
    }
}
