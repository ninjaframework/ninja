package ninja.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpHeaderUtilsTest {

    @Test
    public void testGetContentTypeFromContentTypeAndCharacterSetting() {

        assertEquals("application/json", HttpHeaderUtils.getContentTypeFromContentTypeAndCharacterSetting("application/json; charset=utf-8"));
        assertEquals("application/json", HttpHeaderUtils.getContentTypeFromContentTypeAndCharacterSetting("application/json"));

    }
    
    @Test
    public void testGetCharacterSetOfContentType() {
        
        assertEquals("utf-8", HttpHeaderUtils.getCharsetOfContentType("application/json; charset=utf-8", "TEST_ENCODING"));
        assertEquals("utf-8", HttpHeaderUtils.getCharsetOfContentType("application/json;charset=utf-8", "TEST_ENCODING"));
        assertEquals("TEST_ENCODING", HttpHeaderUtils.getCharsetOfContentType("application/json", "TEST_ENCODING"));
 
    }

    
    @Test
    public void testGetCharacterSetOfContentTypeOrUtf8() {
        
        assertEquals("TEST_ENCODING", HttpHeaderUtils.getCharsetOfContentType("application/json; charset=TEST_ENCODING", NinjaConstant.UTF_8));
        assertEquals("TEST_ENCODING", HttpHeaderUtils.getCharsetOfContentType("application/json;charset=TEST_ENCODING", "TEST_ENCODING"));
        assertEquals(NinjaConstant.UTF_8, HttpHeaderUtils.getCharsetOfContentType("application/json", NinjaConstant.UTF_8));

    }

}
