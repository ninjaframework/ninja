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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaTest;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class LoginLogoutControllerTest extends NinjaTest {
    
    @Before
    public void setup() {
        
        ninjaTestBrowser.makeRequest(getServerAddress() + "setup");
        
    }

    @Test
    public void testLogingLogout() {

        Map<String, String> headers = Maps.newHashMap();

        // /////////////////////////////////////////////////////////////////////
        // Test posting of article does not work without login
        // /////////////////////////////////////////////////////////////////////
        String response = ninjaTestBrowser.makeRequest(getServerAddress()
                + "article/new", headers);
        System.out.println(response);
        assertTrue(response.contains("Error. Forbidden."));

        // /////////////////////////////////////////////////////////////////////
        // Login
        // /////////////////////////////////////////////////////////////////////
        Map<String, String> formParameters = Maps.newHashMap();
        formParameters.put("username", "bob@gmail.com");
        formParameters.put("password", "secret");

        ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress()
                + "login", headers, formParameters);

        // /////////////////////////////////////////////////////////////////////
        // Test posting of article works when are logged in
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeRequest(getServerAddress()
                + "article/new", headers);
        
        assertTrue(response.contains("New article"));

        // /////////////////////////////////////////////////////////////////////
        // Logout
        // /////////////////////////////////////////////////////////////////////
        ninjaTestBrowser.makeRequest(getServerAddress() + "logout", headers);

        // /////////////////////////////////////////////////////////////////////
        // Assert that posting of article does not work any more...
        // /////////////////////////////////////////////////////////////////////
        response = ninjaTestBrowser.makeRequest(getServerAddress()
                + "article/new", headers);
        System.out.println(response);
        assertTrue(response.contains("Error. Forbidden."));

    }

}
