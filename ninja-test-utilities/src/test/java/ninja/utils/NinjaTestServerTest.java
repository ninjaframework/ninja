/*
 * Copyright (C) the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.utils;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Module;
import ninja.standalone.Console;
import ninja.standalone.Standalone;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class NinjaTestServerTest {
        
    @Test
    public void overrideProperties_works() {
        Map<String, String> overrideProperties = ImmutableMap.of("key", "value");
        NinjaTestServer.builder()
                .standaloneClass(MockStandalone.class)
                .overrideProperties(overrideProperties)
                .build();
        
        Assertions.assertThat(MockStandalone.overrideProperties).isEqualTo(overrideProperties);
        Assertions.assertThat(MockStandalone.startCalled).isTrue();

    }
    
    @Test
    public void port_works() {
        int port = 12345;
        NinjaTestServer.builder()
                .standaloneClass(MockStandalone.class)
                .port(port)
                .build();
        
        Assertions.assertThat(MockStandalone.port).isEqualTo(port);
        Assertions.assertThat(MockStandalone.startCalled).isTrue();
    }
    
    @Test
    public void overrideModule_works() {
        Module mockModule = Mockito.mock(Module.class);
        
        Map<String, String> overrideProperties = ImmutableMap.of("key", "value");
        NinjaTestServer.builder()
                .standaloneClass(MockStandalone.class)
                .overrideModule(mockModule)
                .build();
        
        Assertions.assertThat(MockStandalone.overrideModule).isEqualTo(mockModule);
        Assertions.assertThat(MockStandalone.startCalled).isTrue();
    }
    
    @Test
    public void ninjaMode_works() {
        NinjaMode ninjaMode = NinjaMode.prod;
        
        NinjaTestServer.builder()
                .standaloneClass(MockStandalone.class)
                .ninjaMode(ninjaMode)
                .build();
        
        Assertions.assertThat(MockStandalone.ninjaMode).isEqualTo(ninjaMode);
        Assertions.assertThat(MockStandalone.startCalled).isTrue();
    }
    

    /**
     * The standalone is created via newInstance(). So we cannot
     * mock it right now.
     * 
     * Therefore we use this workaround. A class with static
     * fields that we can later verify in our testcases.
     * 
     * Not nice but works.
     */
    public static class MockStandalone implements Standalone {
        
        public static NinjaMode ninjaMode = null;
        public static Integer port = null;
        public static Map<String, String> overrideProperties = null;
        public static com.google.inject.Module overrideModule = null;
        public static Boolean startCalled = false;

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone join() throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getHost() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone host(String host) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer getPort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone port(int port) {
            this.port = port;
            return this;
        }

        @Override
        public String getContextPath() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone contextPath(String contextPath) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone idleTimeout(long idleTimeout) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Long getIdleTimeout() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Integer getSslPort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone sslPort(int sslPort) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public URI getSslKeystoreUri() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone sslKeystoreUri(URI keystoreUri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSslKeystorePassword() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone sslKeystorePassword(String keystorePassword) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public URI getSslTruststoreUri() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone sslTruststoreUri(URI truststoreUri) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getSslTruststorePassword() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Standalone sslTruststorePassword(String truststorePassword) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List getServerUrls() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List getBaseUrls() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isPortEnabled() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSslPortEnabled() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Console configure() throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Console externalConfigurationPath(String externalConfigurationPath) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Optional getExternalConfigurationPath() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Injector getInjector() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public NinjaMode getNinjaMode() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public NinjaPropertiesImpl getNinjaProperties() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Console name(String name) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Console ninjaMode(NinjaMode ninjaMode) {
            this.ninjaMode = ninjaMode;
            return null;
        }

        @Override
        public Console overrideModule(Module module) {
            this.overrideModule = module;
            return null;
        }

        @Override
        public Console overrideProperties(Map properties) {
            this.overrideProperties = properties;
            return this;
        }

        @Override
        public Console shutdown() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Console start() throws Exception {
            this.startCalled = true;
            return this;
        }

    }
   
    
}
