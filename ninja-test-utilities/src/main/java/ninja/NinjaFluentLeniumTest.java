/**
 * Copyright (C) 2012-2020 the original author or authors.
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

import org.fluentlenium.adapter.junit.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;

import com.google.inject.Injector;

import ninja.utils.NinjaTestServer;

public abstract class NinjaFluentLeniumTest extends FluentTest {

    /**
     * @deprecated The driver should be access via {@link #getDriver()}. Null
     *             variable left for deprecation notice only.
     * @see #getDriver()
     */
    @Deprecated
    public WebDriver webDriver;

    public NinjaTestServer ninjaTestServer;

    @Before
    public void startupServer() {
        ninjaTestServer = new NinjaTestServer();
    }

    /**
     * @deprecated Updating to FluentLenium 3.2.0, the driver to use is now
     *             either a string, returned by overriding the
     *             {@link #getWebDriver()} method, or the driver implementation,
     *             by overriding the {@link #newWebDriver()} method.
     * @see #getWebDriver()
     * @see #newWebDriver()
     */
    @Deprecated
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

    @Override
    public String getWebDriver() {
        return "htmlunit";
    }

    /**
     * @see #getBaseUrl()
     */
    @Deprecated
    public String getServerAddress() {
        return ninjaTestServer.getServerAddress();
    }

    /**
     * Will be automatically used within the goTo() method.
     * 
     * @see org.fluentlenium.adapter.FluentAdapter#getBaseUrl()
     */
    @Override
    public String getBaseUrl() {
        return ninjaTestServer.getBaseUrl();
    }

    public String getServerUrl() {
        return ninjaTestServer.getServerUrl();
    }

    @After
    public void shutdownServer() {
        ninjaTestServer.shutdown();
    }

    public Injector getInjector() {
        return ninjaTestServer.getInjector();
    }

}
