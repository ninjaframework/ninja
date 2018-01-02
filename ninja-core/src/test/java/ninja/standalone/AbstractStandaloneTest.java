/**
 * Copyright (C) 2012- the original author or authors.
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

package ninja.standalone;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class AbstractStandaloneTest {

    @Test
    public void ninjaModeOnConstructor() {
        System.setProperty(NinjaConstant.MODE_KEY_NAME, "dev");
        
        MockStandalone standalone = new MockStandalone();
        
        assertThat(standalone.getNinjaMode(), is(NinjaMode.dev));
    }
    
    @Test
    public void manuallySetExternalConfiguration() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .externalConfigurationPath("conf/standalone.conf");
        
        // port is still null (before configuration)
        assertThat(standalone.getPort(), is(nullValue()));
        
        standalone.configure();
        
        // ninja.port in explicit external config worked to override default
        assertThat(standalone.getPort(), is(9000));
    }
    
    @Test
    public void configurationPropertyPriority() throws Exception {
        
        MockStandalone standalone;
        
        standalone = new MockStandalone()
                .configure();
        
        // defaultValue
        assertThat(standalone.getNinjaMode(), is(NinjaMode.prod));
        assertThat(standalone.getExternalConfigurationPath(), is(nullValue()));
        assertThat(standalone.getContextPath(), is(Standalone.DEFAULT_CONTEXT_PATH));
        assertThat(standalone.getHost(), is(Standalone.DEFAULT_HOST));
        assertThat(standalone.getPort(), is(Standalone.DEFAULT_PORT));
        assertThat(standalone.getIdleTimeout(), is(Standalone.DEFAULT_IDLE_TIMEOUT));

        
        // configProperty > defaultValue
        standalone = new MockStandalone()
                .externalConfigurationPath("conf/standalone.conf")
                .configure();
        
        assertThat(standalone.getNinjaMode(), is(NinjaMode.prod));
        assertThat(standalone.getExternalConfigurationPath(), is("conf/standalone.conf"));
        assertThat(standalone.getContextPath(), is("/mycontext"));
        assertThat(standalone.getHost(), is("1.1.1.1"));
        assertThat(standalone.getPort(), is(9000));
        assertThat(standalone.getIdleTimeout(), is(60000L));
        
        
        // systemProperty > configProperty
        System.setProperty(NinjaConstant.MODE_KEY_NAME, "dev");
        System.setProperty(Standalone.KEY_NINJA_HOST, "2.2.2.2");
        System.setProperty(Standalone.KEY_NINJA_PORT, "9001");
        System.setProperty(Standalone.KEY_NINJA_CONTEXT_PATH, "/yourcontext");
        System.setProperty(Standalone.KEY_NINJA_IDLE_TIMEOUT, "80000");
        
        try {
            standalone = new MockStandalone()
                    .externalConfigurationPath("conf/standalone.conf")
                    .configure();
            
            assertThat(standalone.getNinjaMode(), is(NinjaMode.dev));
            assertThat(standalone.getExternalConfigurationPath(), is("conf/standalone.conf"));
            assertThat(standalone.getContextPath(), is("/yourcontext"));
            assertThat(standalone.getHost(), is("2.2.2.2"));
            assertThat(standalone.getPort(), is(9001));
            assertThat(standalone.getIdleTimeout(), is(80000L));
            
            
            
            // currentValue > systemProperty
            standalone = new MockStandalone()
                .externalConfigurationPath("conf/standalone.conf")
                .host("3.3.3.3")
                .port(9002)
                .contextPath("/othercontext")
                .idleTimeout(70000L)
                .ninjaMode(NinjaMode.test)
                .configure();
        
            
            assertThat(standalone.getNinjaMode(), is(NinjaMode.test));
            assertThat(standalone.getExternalConfigurationPath(), is("conf/standalone.conf"));
            assertThat(standalone.getContextPath(), is("/othercontext"));
            assertThat(standalone.getHost(), is("3.3.3.3"));
            assertThat(standalone.getPort(), is(9002));
            assertThat(standalone.getIdleTimeout(), is(70000L));
            
        } finally {
            System.clearProperty(Standalone.KEY_NINJA_HOST);
            System.clearProperty(Standalone.KEY_NINJA_PORT);
            System.clearProperty(Standalone.KEY_NINJA_CONTEXT_PATH);
            System.clearProperty(Standalone.KEY_NINJA_IDLE_TIMEOUT);
        }
    }
    
    
    @Test
    public void ninjaPropertiesThrowsExceptionUntilConfigured() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .externalConfigurationPath("conf/standalone.conf");
        
        try {
            standalone.getNinjaProperties();
            fail("exception expected");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("configure() not called"));
        }
        
        standalone.configure();
        
        assertThat(standalone.getNinjaProperties(), is(not(nullValue())));
    }
    
    
    @Test
    public void urlUsesLocalhostInLieuOfNull() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .configure();
        
        assertThat(standalone.getServerUrls().get(0), is("http://localhost:8080"));
        assertThat(standalone.getBaseUrls().get(0), is("http://localhost:8080"));
    }
    
    @Test
    public void urlIncludesContext() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .host("1.1.1.1")
                .contextPath("/mycontext")
                .configure();
        
        assertThat(standalone.getServerUrls().get(0), is("http://1.1.1.1:8080"));
        assertThat(standalone.getBaseUrls().get(0), is("http://1.1.1.1:8080/mycontext"));
    }
    
    @Test
    public void urlExcludesWellKnownPorts() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .host("1.1.1.1")
                .port(80)
                .contextPath("/mycontext")
                .configure();
        
        assertThat(standalone.getServerUrls().get(0), is("http://1.1.1.1"));
        assertThat(standalone.getBaseUrls().get(0), is("http://1.1.1.1/mycontext"));
    }
    
    @Test
    public void ninjaPropertiesServerNameSetAfterConfigure() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .host("1.1.1.1")
                .configure();
        
        assertThat(standalone.getNinjaProperties().get(NinjaConstant.serverName), is("http://1.1.1.1:8080"));
        
    }
    
    @Test
    public void ninjaPropertiesServerNameSetButOnlyIfNotInConfigFile() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
                .externalConfigurationPath("conf/standalone.with.servername.conf")
                .host("1.1.1.1")
                .configure();
        
        assertThat(standalone.getNinjaProperties().get(NinjaConstant.serverName), is("http://www.example.com:8080"));
        
    }
    
    @Test
    public void injectorOnlyAvailableAfterStart() throws Exception {
        
        MockStandalone standalone = new MockStandalone();
        
        try {
            standalone.getInjector();
            fail("exception expected");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("start() not called"));
        }
        
        standalone.start();
        
        assertThat(standalone.getInjector(), is(not(nullValue())));
    }
    
    @Test
    public void validateContextPath() throws Exception {
        
        MockStandalone standalone = new MockStandalone()
            .externalConfigurationPath("conf/standalone.with.badcontext.conf");
        
        try {
            standalone.configure();
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("context"));
        }
        
        standalone.contextPath("/goodcontext").configure();
    }
    
    @Test
    public void noPortsEnabled() throws Exception {
        try {
            MockStandalone standalone = new MockStandalone()
                    .port(-1)
                    .configure();
            fail("exception expected");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("ports were disabled"));
        }
    }
    
    @Test
    public void randomPortAssigned() throws Exception {
        MockStandalone standalone = new MockStandalone()
                .port(0)
                .configure();
        
        assertThat(standalone.getPort(), is(not(0)));
        assertThat(standalone.isPortEnabled(), is(true));
        assertThat(standalone.isSslPortEnabled(), is(false));
    }
    
    @Test
    public void sslRandomPortAssigned() throws Exception {
        MockStandalone standalone = new MockStandalone()
                .port(-1)
                .sslPort(0)
                .configure();
        
        assertThat(standalone.getSslPort(), is(not(0)));
    }
    
    @Test
    public void sslConfiguration() throws Exception {
        MockStandalone standalone = new MockStandalone()
                .port(-1)
                .sslPort(8443)
                .start();
        
        assertThat(standalone.getSslPort(), is(not(0)));
        assertThat(standalone.isPortEnabled(), is(false));
        assertThat(standalone.isSslPortEnabled(), is(true));
        assertThat(standalone.getServerUrls().size(), is(1));
        assertThat(standalone.getServerUrls().get(0), endsWith(":8443"));
        assertThat(standalone.getServerUrls().get(0), startsWith("https://"));
    }

}
