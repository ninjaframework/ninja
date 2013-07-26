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

import org.junit.Test;

import com.google.common.collect.Maps;

public class ApplicationControllerTest extends NinjaTest {

    @Test
    public void testThatPostingWorks() {

        ///////////////////////////////////////////////////////////////////////
        // STEP 1: Check that db is empty
        ///////////////////////////////////////////////////////////////////////
        Map<String, String> headers = Maps.newHashMap();

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/", headers);


        assertTrue(result
                .contains("No guestbook entries yet... please enter one above..."));

        ///////////////////////////////////////////////////////////////////////
        // STEP 2: Post a new guestbookentry.
        ///////////////////////////////////////////////////////////////////////
        Map<String, String> formParameters = Maps.newHashMap();

        formParameters.put("text", "a text");
        formParameters.put("email", "test2@email.com");

        ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress()
                + "/", headers, formParameters);

        ///////////////////////////////////////////////////////////////////////
        // STEP 2: Check that posting works (and database, too)
        ///////////////////////////////////////////////////////////////////////
        result = ninjaTestBrowser.makeRequest(getServerAddress() + "/",
                headers);

        assertTrue(result.contains("a text"));
        assertTrue(result.contains("test2@email.com"));
    }
    
    
    @Test
    public void testThatPostingWorksTwoTimesAndDatabseIsResetInBetween() {

        ///////////////////////////////////////////////////////////////////////
        // STEP 1: Check that db is empty
        ///////////////////////////////////////////////////////////////////////
        Map<String, String> headers = Maps.newHashMap();

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/", headers);


        assertTrue(result
                .contains("No guestbook entries yet... please enter one above..."));

        ///////////////////////////////////////////////////////////////////////
        // STEP 2: Post a new guestbookentry.
        ///////////////////////////////////////////////////////////////////////
        Map<String, String> formParameters = Maps.newHashMap();

        formParameters.put("text", "a text");
        formParameters.put("email", "test2@email.com");

        ninjaTestBrowser.makePostRequestWithFormParameters(getServerAddress()
                + "/", headers, formParameters);

        ///////////////////////////////////////////////////////////////////////
        // STEP 2: Check that posting works (and database, too)
        ///////////////////////////////////////////////////////////////////////
        result = ninjaTestBrowser.makeRequest(getServerAddress() + "/",
                headers);

        assertTrue(result.contains("a text"));
        assertTrue(result.contains("test2@email.com"));
    }

}
