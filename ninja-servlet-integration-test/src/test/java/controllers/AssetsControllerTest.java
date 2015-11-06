/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import java.util.Map;

import ninja.NinjaTest;

import org.apache.http.HttpResponse;
import org.junit.Test;

import com.google.common.collect.Maps;
import org.junit.Assert;

public class AssetsControllerTest extends NinjaTest {

    @Test
    public void testThatSettingOfMimeTypeWorks() {

        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers
        HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(
                getServerAddress() + "assets/files/test_for_mimetypes.dxf",
                headers);

        // this is a mimetype nobody knows of...
        // but it is listetd in the ninja mimetypes... therefore it will be
        // found:
        // default charset is always utf-8 by convention.
        assertEquals(
                "application/dxf; charset=UTF-8", 
                httpResponse.getHeaders("Content-Type")[0].getValue());

    }
    
    @Test
    public void testThatAssetsWork() {

        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers
        HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(
                getServerAddress() + "assets/js/google-code-prettify/prettify.css", headers);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());

    }
    
    @Test
    public void testThatMetaInfIntegrationWorks() {

        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        // /redirect will send a location: redirect in the headers
        HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(
                getServerAddress() + "assets/webjars/bootstrap/3.3.4/css/bootstrap.min.css", headers);

        assertEquals(200, httpResponse.getStatusLine().getStatusCode());
    }
    
    @Test
    public void testThatStaticAssetsDoNotSetNinjaCookies() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Cookie", "NINJA_FLASH=\"success=This+is+a+flashed+success+-+with+placeholder%3A+PLACEHOLDER\";Path=/");

        // /redirect will send a location: redirect in the headers
        HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(
                getServerAddress() + "assets/files/test_for_mimetypes.dxf",
                headers);
            
        // static assets should not set any session information        
        // ... and static assets should not set any flash information
        assertEquals(null, httpResponse.getFirstHeader("Set-Cookie"));
    }

    @Test
    public void testSecurityRelativePathIntoOtherDirectoryDoesNotWork() throws Exception {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();
        // /redirect will send a location: redirect in the headers
        String response = ninjaTestBrowser.makeRequest(
                getServerAddress() + "assets/js/prettify.js/../../../conf/application.conf",
                headers);
        Assert.assertFalse(response.contains("application.secret"));
    }

    @Test
    public void testSecurityRelativePathIntoOtherDirectoryDoesNotWorkWithEncodedSlashes() throws Exception {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();
        // /redirect will send a location: redirect in the headers
        String response = ninjaTestBrowser.makeRequest(
                getServerAddress() + "assets/js/prettify.js%2F..%2F..%2F..%2Fconf%2Fapplication.conf",
                headers);
        Assert.assertFalse(response.contains("application.secret"));
    }

}
