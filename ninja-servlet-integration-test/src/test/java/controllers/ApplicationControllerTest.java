/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import java.io.IOException;
import java.util.Map;

import models.FormObject;

import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import ninja.RecycledNinjaServerTester;
import ninja.utils.NinjaTestBrowser;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import org.junit.Before;

public class ApplicationControllerTest extends RecycledNinjaServerTester {

    private NinjaTestBrowser ninjaTestBrowser;
    
    @Before
    public void freshNinjaTestBrowser() {
        this.ninjaTestBrowser = new NinjaTestBrowser();
    }
    
    @Test
    public void testThatRedirectWorks() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers
        String result = ninjaTestBrowser.makeRequest(withBaseUrl("/redirect"), headers);

        // If the redirect has worked we must see the following text
        // from the index screen:
        assertTrue(result.contains("Integration Test"));

    }
    
    @Test
    public void lambdaAnonymous() {
        // if lambda worked then we'd be redirected to index
        String result = ninjaTestBrowser.makeRequest(withBaseUrl("/lambda_anonymous"));
        
        assertThat(result, containsString("Hi!"));
    }
    
    @Test
    public void lambdaAnonymousArgs() {
        // if lambda worked then we'd be redirected to index
        String result = ninjaTestBrowser.makeRequest(withBaseUrl("/lambda_anonymous_args?a=joe"));
        
        assertThat(result, containsString("Query: joe"));
    }

    @Test
    public void testHtmlEscapingInTeamplateWorks() {

        // IF the escaping works I expect the following string inside the page:
        String expectedContent = "&lt;script&gt;alert(&#39;Hello! &lt;&gt;&amp;&quot;&#39;&#39;);&lt;/script&gt;";       

        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers

        String result = ninjaTestBrowser.makeRequest(withBaseUrl("/htmlEscaping"), headers);

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
                ninjaTestBrowser.makeRequestAndGetResponse(withBaseUrl("/session"), headers);

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
                ninjaTestBrowser.makeRequest(withBaseUrl("/user/12345/john@example.com/userDashboard"), headers);

        // And assert that stuff is visible on page:
        assertThat(response, containsString("john@example.com"));
        assertThat(response, containsString("12345"));
        // Assert that reverse routing works:
        assertThat(response, containsString("By the way... Reverse url of this rawUrl is: /user/12345/john@example.com/userDashboard"));

    }

    @Test
    public void testThatValidationWorks() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/validation?email=john@example.com"));

        // And assert that stuff is visible on page:
        assertEquals(response, "\"john@example.com\"");

        response = ninjaTestBrowser.makeRequest(withBaseUrl("/validation"));

        // And assert that stuff is visible on page:
        assertEquals(
                response.trim(),
                "[{\"field\":\"email\",\"constraintViolation\":{\"messageKey\":\"validation.required.violation\",\"fieldKey\":\"email\",\"defaultMessage\":\"email is required\",\"messageParams\":[]}}]");

    }

    @Test
    public void testPostFormParsingWorks() throws IOException {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();
        Map<String, String> formParameters = Maps.newHashMap();

        formParameters.put("name", "tester");
        formParameters.put("email", "test@email.com");

        formParameters.put("primInt", "593765");
        formParameters.put("objInt", "593766");
        
        formParameters.put("primLong", "-3957393");
        formParameters.put("objLong", "-3957394");
        
        formParameters.put("primFloat", "78.12");
        formParameters.put("objFloat", "79.22");
        
        formParameters.put("primDouble", "694.56");
        formParameters.put("objDouble", "696.76");
        
        formParameters.put("primBoolean", "false");
        formParameters.put("objBoolean", "true");
        
        
        formParameters.put("primByte", "111");
        formParameters.put("objByte", "112");
        
        formParameters.put("primShort", "32456");
        formParameters.put("objShort", "32455");
        
        formParameters.put("primChar", "Z");
        formParameters.put("objChar", "X");

        String response =
                ninjaTestBrowser.makePostRequestWithFormParameters(
                        withBaseUrl("/form"),
                        headers,
                        formParameters);
        
        ObjectMapper objectMapper = new ObjectMapper();
        FormObject returnedObject = objectMapper.readValue(response, FormObject.class);

        // And assert that returned object has same values
        assertEquals("tester", returnedObject.name);
        assertEquals("test@email.com", returnedObject.getEmail());
        
        assertEquals(593765, returnedObject.primInt);
        assertEquals(593766, returnedObject.objInt.intValue());
        
        assertEquals(-3957393, returnedObject.primLong);
        assertEquals(-3957394, returnedObject.objLong.longValue());
        
        assertEquals(78.12, returnedObject.primFloat, 0.001);
        assertEquals(79.22, returnedObject.objFloat.floatValue(), 0.001);
        
        assertEquals(694.56, returnedObject.primDouble, 0.001);
        assertEquals(696.76, returnedObject.objDouble.doubleValue(), 0.001);
        
        assertEquals(false, returnedObject.isPrimBoolean());
        assertEquals(true, returnedObject.getObjBoolean().booleanValue());
        
        assertEquals(111, returnedObject.getPrimByte());
        assertEquals(112, returnedObject.getObjByte().byteValue());
        
        assertEquals(32456, returnedObject.getPrimShort());
        assertEquals(32455, returnedObject.getObjShort().shortValue());
        
        assertEquals('Z', returnedObject.getPrimChar());
        assertEquals('X', returnedObject.getObjChar().charValue());
    }

    @Test
    public void testDirectObjectRenderingWorks() {
        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/direct_rendering"));
        
        // And assert that object values are visible on page:
        assertTrue(response.contains("test_name"));
        assertTrue(response.contains("13579"));
        assertTrue(response.contains("-2954"));
    }
    
    @Test
    public void testFlashSuccessWorks() {

        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/flash_success"));

        // And assert that stuff is visible on page:
        assertTrue(response.contains("This is a flashed success - with placeholder: PLACEHOLDER"));

    }
    
    @Test
    public void testFlashErrorWorks() {

        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/flash_error"));

        // And assert that stuff is visible on page:
        assertTrue(response.contains("This is a flashed error - with placeholder: PLACEHOLDER"));

    }
    
    @Test
    public void testFlashAnyWorks() {

        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/flash_any"));

        // And assert that stuff is visible on page:
        assertTrue(response.contains("This is an arbitrary message as flash message - with placeholder: PLACEHOLDER"));

    }
    
    
    @Test
    public void testCachingWorks() {

        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/test_caching"));

        // First request => no caching
        assertTrue(response.contains("No cache key set."));
        
        
        response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/test_caching"));

        // Second request hits cache:
        assertTrue(response.contains("Cache key is: cacheKeyValue"));

    }

    @Test
    public void testJsonPWorks() {
        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/jsonp?callback=App.callback"));
        assertEquals("App.callback({\"object\":\"value\"})", response);
    }
    
    @Test
    public void testThatBadRequestWorks() {
        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/bad_request"));
        assertTrue(response.contains("bad request"));
    }
    
    @Test
    public void testThatReverseRoutingWorks() {
        
        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/test_reverse_routing"));
    
        assertTrue(response.contains("<li>/user/100000/me@me.com/userDashboard</li>"));
        assertTrue(response.contains("<li>/assets/webjars/bootstrap/3.3.4/css/bootstrap.min.css</li>"));
        assertTrue(response.contains("<li>/assets/css/custom.css</li>"));
    }
    
    @Test
    public void testGetContextPathWorks() {
        
        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/test_get_context_path_works"));
    
        // both should be blank. We make sure we don't get any strange "/" delimiters or so...
        assertTrue(response.contains("<li>ninjaProperties.getContextPath(): </li>"));
        assertTrue(response.contains("<li>context.getContextPath(): </li>"));
    }
    
    
    public void test_that_freemarker_emits_400_when_template_not_found() {
        
        String response =
                ninjaTestBrowser.makeRequest(withBaseUrl("/test_that_freemarker_emits_400_when_template_not_found"));
    
        // both should be blank. We make sure we don't get any strange "/" delimiters or so...
        assertTrue(response.contains("<title>400 - Bad Request.</title>"));
        
    }
    
    
}
