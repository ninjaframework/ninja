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

package ninja.utils;

import java.net.URI;
import java.net.URISyntaxException;
import com.google.inject.Injector;
import ninja.standalone.Standalone;
import ninja.standalone.StandaloneHelper;

/**
 * Starts a new server using an embedded standalone. Startup is really fast and thus
 * usable in integration tests.
 * 
 * @author rbauer
 */
public class NinjaTestServer {

    private final int port;
    private final Standalone standalone;

    public NinjaTestServer() {
        this(StandaloneHelper.findDefaultStandaloneClass());
    }
    
    public NinjaTestServer(Class<? extends Standalone> standaloneClass) {
        this.port = StandaloneHelper.findAvailablePort(1000, 10000);
        
        try {
            this.standalone = standaloneClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create " + standaloneClass.getCanonicalName() + " (either not on classpath or invalid class name)");
        }
        
        try {
            // configure then start
            this.standalone
                .port(this.port)
                .ninjaMode(NinjaMode.test);
            
            standalone.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public NinjaTestServer ninjaMode(NinjaMode ninjaMode) {
        standalone.ninjaMode(ninjaMode);
        return this;
    }
    
    public NinjaMode getNinjaMode() {
        return standalone.getNinjaMode();
    }

    public Injector getInjector() {
        return standalone.getInjector();
    }

    public String getServerAddress() {
        // standalone already builds this based on the host & port it binds to
        return standalone.getNinjaProperties().get(NinjaConstant.serverName) + "/";
    }

    public URI getServerAddressAsUri() {
        try {
            return new URI(getServerAddress());
        } catch (URISyntaxException e) {
            // should not be able to happen...
            return null;
        }
    }

    public void shutdown() {
        standalone.shutdown();
    }

}
