/**
 * Copyright (C) the original author or authors.
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