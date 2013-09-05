/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        assertTrue(result.contains("Integration Test"));

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
        // Assert that reverse routing works:
        assertTrue(response.contains("By the way... Reverse url of this rawUrl is: /user/12345/john@example.com/userDashboard"));

    }

    @Test
    public void testThatValidationWorks() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        String response =
                ninjaTestBrowser.makeRequest(getServerAddress()
                        + "validation?email=john@example.com");

        // And assert that stuff is visible on page:
        assertEquals(response, "\"john@example.com\"");

        response = ninjaTestBrowser.makeRequest(getServerAddress() + "validation");

        // And assert that stuff is visible on page:
        assertEquals(
                response.trim(),
                "[{\"field\":\"email\",\"constraintViolation\":{\"messageKey\":\"validation.required.violation\",\"fieldKey\":\"email\",\"defaultMessage\":\"email is required\",\"messageParams\":[]}}]");

    }

    @Test
    public void testPostFormParsingWorks() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();
        Map<String, String> formParameters = Maps.newHashMap();

        formParameters.put("description", "test3");
        formParameters.put("email", "test2@email.com");
        formParameters.put("name", "test1");

        String response =
                ninjaTestBrowser.makePostRequestWithFormParameters(
                        getServerAddress() + "/contactForm",
                        headers,
                        formParameters);

        // And assert that stuff is visible on page:
        assertTrue(response.contains("test3"));
        assertTrue(response.contains("test2@email.com"));
        assertTrue(response.contains("test1"));


    }
    
    @Test
    public void testFlashSuccessWorks() {

        String response =
                ninjaTestBrowser.makeRequest(getServerAddress() + "/flash_success");

        // And assert that stuff is visible on page:
        assertTrue(response.contains("This is a flashed success - with placeholder: PLACEHOLDER"));

    }
    
    @Test
    public void testFlashErrorWorks() {

        String response =
                ninjaTestBrowser.makeRequest(getServerAddress() + "/flash_error");

        // And assert that stuff is visible on page:
        assertTrue(response.contains("This is a flashed error - with placeholder: PLACEHOLDER"));

    }
    
    @Test
    public void testFlashAnyWorks() {

        String response =
                ninjaTestBrowser.makeRequest(getServerAddress() + "/flash_any");

        // And assert that stuff is visible on page:
        assertTrue(response.contains("This is an arbitrary message as flash message - with placeholder: PLACEHOLDER"));

    }
    
    
    @Test
    public void testCachingWorks() {

        String response =
                ninjaTestBrowser.makeRequest(getServerAddress() + "/test_caching");

        // First request => no caching
        assertTrue(response.contains("No cache key set."));
        
        
        response =
                ninjaTestBrowser.makeRequest(getServerAddress() + "/test_caching");

        // Second request hits cache:
        assertTrue(response.contains("Cache key is: cacheKeyValue"));

    }

}
