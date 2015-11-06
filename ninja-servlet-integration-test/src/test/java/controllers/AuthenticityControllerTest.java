package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.UUID;

import ninja.NinjaTest;
import ninja.Result;

import org.apache.http.HttpResponse;
import org.junit.Test;

public class AuthenticityControllerTest extends NinjaTest {
    
    @Test
    public void testToken() {
        String response = ninjaTestBrowser.makeRequest(getServerAddress() + "/token");
        assertNotNull(response);
        
        try {
            UUID.fromString(response);
        } catch (Exception e) {
            fail("Response does not contain authenticity token");
        }
    }
    
    @Test
    public void testForm() {
        String response = ninjaTestBrowser.makeRequest(getServerAddress() + "/form");
        
        assertNotNull(response);
        assertTrue(response.startsWith("<input type=\"hidden\" value=\""));
        assertTrue(response.endsWith("\" name=\"authenticityToken\" />"));
    }
    
    @Test
    public void testUnauthorized() {
        HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(getServerAddress() + "/unauthorized", new HashMap<String, String>());
        
        assertNotNull(httpResponse);
        assertEquals(httpResponse.getStatusLine().getStatusCode(), Result.SC_403_FORBIDDEN);
    }
}