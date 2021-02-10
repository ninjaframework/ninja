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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import ninja.utils.NinjaTestServer;

import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.Before;

import com.devbliss.doctest.DocTest;

/**
 * DEPRECATED. Please consider using NinjaDocTester and the org.doctester library instead.
 * 
 * This class and its packages will soon be moved to a Ninja module.
 * 
 * Superclass for doctests that require a running server. Uses {@link NinjaTest} for the server
 * stuff.
 * 
 * @author hschuetz
 * 
 */
@Deprecated
public abstract class NinjaApiDocTest extends DocTest {
    private static NinjaTestServer ninjaTestServer;

    public NinjaApiDocTest() {
    }

    @Before
    public void startServerInTestMode() {
        ninjaTestServer = new NinjaTestServer.Builder().build();
    }

    @After
    public void shutdownServer() {
        ninjaTestServer.shutdown();
    }

    public URI buildUri(String relativePath, Map<String, String> parameters) throws URISyntaxException {
        return build(relativePath, parameters).build();
    }

    public URI buildUri(String relativePath) throws URISyntaxException {
        return build(relativePath, null).build();
    }

    private URIBuilder build(String relativePath, Map<String, String> parameters) {
        URIBuilder uriBuilder =
                new URIBuilder(ninjaTestServer.getServerAddressAsUri()).setPath(relativePath);
        addParametersToURI(parameters, uriBuilder);
        return uriBuilder;
    }

    private void addParametersToURI(Map<String, String> parameters, URIBuilder uriBuilder) {
        if (parameters != null) {
            for (Entry<String, String> param : parameters.entrySet()) {
                uriBuilder.setParameter(param.getKey(), param.getValue());
            }
        }
    }


}
