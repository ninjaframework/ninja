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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaTest;

import org.junit.Test;

import com.google.common.collect.Maps;

public class I18nControllerTest extends NinjaTest {
    
    String TEXT_EN = "Hello - this is an i18n message in the templating engine with placeholder: Yea!";
    String TEXT_DE = "Hallo - das ist eine internationalisierte Nachricht in der Templating Engine mit placeholder: Yea!";

    @Test
    public void testThatI18nWorksEn() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "en-US");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n", headers);

        assertTrue(result
                .contains(TEXT_EN));

    }

    @Test
    public void testThatI18nWorksDe() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "de-DE");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n", headers);

        assertTrue(result
                .contains(TEXT_DE));

    }
    
    @Test
    public void testThatImplicitParameterWorks() {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "de-DE");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n", headers);

        assertTrue(result
                .contains("Implicit language is: de-DE"));

    }
    
    
    @Test
    public void testThatExplicitLangSettingWorks() {

        // 1) test that overriding of accept-language header works
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Accept-Language", "de-DE");

        String result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n/en", headers);

        assertTrue(result
                .contains(TEXT_EN));
        
        
        // 2) test that fallback is accept-language header
        headers = Maps.newHashMap();
        headers.put("Accept-Language", "de-DE");

        result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n/tk", headers);

        assertTrue(result
                .contains(TEXT_EN));
        
        
        // 3) test when no accept-lanugage is present => fallback should be
        //    language on the root.
        headers = Maps.newHashMap();

        result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n/tk", headers);

        assertTrue(result
                .contains(TEXT_EN));
        
        
        // 4) normal operation
        headers = Maps.newHashMap();
        headers.put("Accept-Language", "de-DE");

        result = ninjaTestBrowser.makeRequest(getServerAddress()
                + "/i18n/de", headers);

        assertTrue(result
                .contains(TEXT_DE));

    }

}
