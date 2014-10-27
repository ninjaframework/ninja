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

package ninja.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

import ninja.standalone.NinjaJetty;

import org.apache.http.client.utils.URIBuilder;

import com.google.inject.Injector;

/**
 * Starts a new server using an embedded jetty. Startup is really fast and thus
 * usable in integration tests.
 * 
 * @author rbauer
 */
public class NinjaTestServer {

    private final int port;
    private final URI serverUri;
    
    private final NinjaJetty ninjaJetty;

    public NinjaTestServer() {

        this.port = findAvailablePort(1000, 10000);
        serverUri = createServerUri();
        
        
        ninjaJetty = new NinjaJetty();
        ninjaJetty.setPort(this.port);
        ninjaJetty.setServerUri(serverUri);
        ninjaJetty.setNinjaMode(NinjaMode.test);
        
        ninjaJetty.start();
        
    }

    public Injector getInjector() {
        return ninjaJetty.getInjector();
    }

    public String getServerAddress() {
        return serverUri.toString() + "/";
    }

    public URI getServerAddressAsUri() {
        return serverUri;
    }

    private URI createServerUri() {
        try {
            return new URIBuilder().setScheme("http").setHost("localhost")
                    .setPort(port).build();
        } catch (URISyntaxException e) {
            // should not be able to happen...
            return null;
        }
    }

    public void shutdown() {
        ninjaJetty.shutdown();
    }

    private static int findAvailablePort(int min, int max) {
        for (int port = min; port < max; port++) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                // Must already be taken
            }
        }
        throw new IllegalStateException(
                "Could not find available port in range " + min + " to " + max);
    }

}
