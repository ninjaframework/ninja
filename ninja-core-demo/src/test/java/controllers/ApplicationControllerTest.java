package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaTest;

import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.junit.Test;

import com.google.common.collect.Maps;

public class ApplicationControllerTest extends NinjaTest {

    @Test
    public void testThatRedirectWorks() {

        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers
        String result = ninjaTestBrowser.makeRequest(getServerAddress() + "/redirect", headers);

        // If the redirect has worked we must see the following text
        // from the index screen:
        assertTrue(result.contains("And developing large web applications becomes fun again."));

    }

    @Test
    public void testHtmlEscapingInTeamplateWorks() {

        // IF the escaping works I expect the following string inside the page:
        String expectedContent = "&lt;script&gt;alert('Hello');&lt;/script&gt;";
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers

        String result = ninjaTestBrowser.makeRequest(getServerAddress() + "htmlEscaping", headers);

        // If the redirect has worked we must see the following text
        // from the index screen:
        assertTrue(result.contains(expectedContent));

    }

    @Test
    public void makeSureSessionsGetSentToClient() {

        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // redirect will send a location: redirect in the headers
        HttpResponse httpResponse =
                ninjaTestBrowser.makeRequestAndGetResponse(getServerAddress() + "session", headers);

        // Test that cookies get transported to consumer:
        assertEquals(1, ninjaTestBrowser.getCookies().size());
        Cookie cookie = ninjaTestBrowser.getCookieWithName("NINJA_SESSION");

        assertTrue(cookie != null);

        assertTrue(cookie.getValue().contains("___TS"));
        assertTrue(cookie.getValue().contains("username"));
        assertTrue(cookie.getValue().contains("kevin"));

    }

    @Test
    public void testThatPathParamParsingWorks() {

        // Simply connect to the userDashboard place
        // and make sure that parsing of paramters works as expected.

        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // do the request
        String response =
                ninjaTestBrowser.makeRequest(getServerAddress()
                        + "user/12345/john@example.com/userDashboard", headers);

        // And assert that stuff is visible on page:
        assertTrue(response.contains("john@example.com"));
        assertTrue(response.contains("12345"));

    }

    @Test
    public void testThatValidationWorks() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        String response =
                ninjaTestBrowser.makeRequest(getServerAddress()
                        + "validation?email=john@example.com");

        // And assert that stuff is visible on page:
        assertEquals(response, "john@example.com");

        response = ninjaTestBrowser.makeRequest(getServerAddress() + "validation");

        // And assert that stuff is visible on page:
        assertEquals(response, "email is required");

    }

}
