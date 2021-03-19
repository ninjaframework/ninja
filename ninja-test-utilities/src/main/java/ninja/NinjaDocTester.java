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

package ninja;

import ninja.utils.NinjaTestServer;

import org.doctester.DocTester;
import org.doctester.testbrowser.Url;
import org.junit.After;
import org.junit.Before;

import com.google.inject.Injector;

/**
 * Superclass for doctests that require a running server. Uses {@link NinjaTest} for the server
 * stuff.
 * 
 * @author hschuetz
 * 
 */
public abstract class NinjaDocTester extends DocTester {
    private NinjaTestServer ninjaTestServer;

    public NinjaDocTester() {
    }

    @Before
    public final void startServerInTestMode() {
        ninjaTestServer = NinjaTestServer.builder().build();
    }

    @After
    public final void shutdownServer() {
        if (ninjaTestServer != null) {
            ninjaTestServer.shutdown();
        }
    }

    @Override
    public Url testServerUrl() {
        return Url.host(ninjaTestServer.getServerUrl());
    }
    
    public Injector getInjector() {
        return ninjaTestServer.getInjector();
    }

}
