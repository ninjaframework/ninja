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

package ninja;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaTestServer;

import org.apache.http.client.utils.URIBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.doctester.DocTester;
import org.doctester.testbrowser.Url;

/**
 * Superclass for doctests that require a running server. Uses {@link NinjaTest} for the server
 * stuff.
 * 
 * @author hschuetz
 * 
 */
public abstract class NinjaDocTester extends DocTester {
    private static NinjaTestServer ninjaTestServer;

    public NinjaDocTester() {
    }

    @BeforeClass
    public static void startServerInTestMode() {
        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_TEST);
        ninjaTestServer = new NinjaTestServer();
    }

    @AfterClass
    public static void shutdownServer() {
    	System.clearProperty(NinjaConstant.MODE_KEY_NAME);
        ninjaTestServer.shutdown();
    }

    @Override
    public Url testServerUrl() {
    
        return Url.host(ninjaTestServer.getServerAddress());
    
    }

}
