/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import com.google.inject.Injector;
import ninja.utils.NinjaTestBrowser;
import ninja.utils.NinjaTestServer;
import org.doctester.testbrowser.Request;

import org.junit.After;
import org.junit.Before;

/**
 * Baseclass for tests that require a running server.
 * 
 * @author rbauer
 * 
 */
public class NinjaTest {

    /** Backend of the test => Starts Ninja */
	public NinjaTestServer ninjaTestServer;
	
	/** A persistent HttpClient that stores cookies to make requests */
	public NinjaTestBrowser ninjaTestBrowser;
	
	public NinjaTest() {
	    //intentionally left emtpy.
	    //startup stuff is done in @Before method.
    }

    @Before
    public void startupServerAndBrowser() {
        ninjaTestServer = new NinjaTestServer();
        ninjaTestBrowser = new NinjaTestBrowser();
    }

    public Injector getInjector(){
        return ninjaTestServer.getInjector();
    }

    /**
     * Something like http://localhost:8080/ 
     * 
     * Will contain trailing slash!
     * @return
     */
    public String getServerAddress() {
		return ninjaTestServer.getServerAddress();
	}

    public URI getServerAddressAsUri() {
        return ninjaTestServer.getServerAddressAsUri();
    }

    @After
    public void shutdownServerAndBrowser() {
        ninjaTestServer.shutdown();
        ninjaTestBrowser.shutdown();
    }

}
