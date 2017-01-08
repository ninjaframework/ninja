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

package ninja;

import ninja.utils.NinjaTestServer;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.google.inject.Injector;

public abstract class NinjaFluentLeniumTest extends FluentTest {

	public WebDriver webDriver = new HtmlUnitDriver();
	
	public NinjaTestServer ninjaTestServer;
	
    @Before
    public void startupServer() {
        ninjaTestServer = new NinjaTestServer();
    }
	
	
    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }
    
    public String getServerAddress() {
    	return ninjaTestServer.getServerAddress();
    }

    @After
    public void shutdownServer() {
        ninjaTestServer.shutdown();
    }
    
    public Injector getInjector() {
        return ninjaTestServer.getInjector();
    }

}
