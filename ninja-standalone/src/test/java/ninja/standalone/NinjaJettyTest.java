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

package ninja.standalone;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.inject.CreationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import ninja.utils.NinjaMode;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.handler.ErrorHandler;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import org.junit.Test;
import static org.junit.Assert.*;

public class NinjaJettyTest {

    static int RANDOM_PORT = StandaloneHelper.findAvailablePort(8081, 9000);
    
    @Test
    public void minimal() throws Exception {
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.minimal.conf")
            .port(RANDOM_PORT);
        
        try {
            assertThat(standalone.getPort(), is(RANDOM_PORT));
            assertThat(standalone.getHost(), is(nullValue()));
            assertThat(standalone.getContextPath(), is(nullValue()));
            assertThat(standalone.getNinjaMode(), is(NinjaMode.prod));
            
            standalone.start();
            
            assertThat(standalone.getServerUrls().get(0), is("http://localhost:" + RANDOM_PORT));
            assertThat(standalone.contextHandler, is(not(nullValue())));
            assertNotNull(standalone.ninjaServletListener);
            assertThat(standalone.contextHandler.isAvailable(), is(true));
            assertThat(standalone.contextHandler.isStarted(), is(true));
            assertThat(standalone.jetty.isStarted(), is(true));
            
            standalone.shutdown();
            
            assertThat(standalone.contextHandler.isStopped(), is(true));
            assertThat(standalone.jetty.isStopped(), is(true));
        } finally {
            standalone.shutdown();
        }
    }
    
    @Test
    public void minimalWithContext() throws Exception {
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.minimal.conf")
            .ninjaMode(NinjaMode.test)
            .port(RANDOM_PORT)
            .host("localhost")
            .contextPath("/mycontext");
        
        try {
            standalone.start();
            
            assertThat(standalone.getPort(), is(RANDOM_PORT));
            assertThat(standalone.getHost(), is("localhost"));
            assertThat(standalone.getContextPath(), is("/mycontext"));
            assertThat(standalone.getNinjaMode(), is(NinjaMode.test));
            
            assertEquals("/mycontext", standalone.contextHandler.getContextPath());
            
            assertThat(standalone.getContextPath(), is(not(nullValue())));
            assertThat(standalone.ninjaServletListener, is(not(nullValue())));
            assertThat(standalone.contextHandler.isAvailable(), is(true));
            assertThat(standalone.contextHandler.isStarted(), is(true));
            assertThat(standalone.jetty.isStarted(), is(true));
            
            standalone.shutdown();
            
            assertThat(standalone.contextHandler.isStopped(), is(true));
            assertThat(standalone.jetty.isStopped(), is(true));
        } finally {
            standalone.shutdown();
        }
    }
    
    @Test
    public void missingConfigurationThrowsException() throws Exception {
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.empty.conf")
            .port(RANDOM_PORT);
        
        try {
            standalone.start();
            fail("start() should have thrown exception");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("application.secret not set"));
        } finally {
            standalone.shutdown();
        }
    }
    
    @Test
    public void missingLanguageThrowsInjectorException() throws Exception {
        // bad configuration file will throw exception when creating NinjaPropertiesImpl
        // that exception occurs in NinjaBootstrap during injector creation
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.missinglang.conf")
            .port(RANDOM_PORT);
        
        try {
            standalone.start();
            fail("start() should have thrown exception");
        } catch (CreationException e) {
            assertThat(e.getMessage(), containsString("not retrieve application languages from ninjaProperties"));
        } finally {
            standalone.shutdown();
        }
    }
    
    
    @Test
    public void jettyConfiguration() throws Exception {
        // use test resource of "jetty.xml" but we need to swap into a new
        // random port and then write the file back out
        String jettyConfiguration = createJettyConfiguration("jetty.xml", RANDOM_PORT);
        
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.com.example.conf")
            .jettyConfiguration(jettyConfiguration);
        
        try {
            standalone.start();
            
            // port won't be correct b/c actually configured via jetty file
            assertThat(standalone.ninjaServletListener, is(not(nullValue())));
            assertThat(standalone.contextHandler.isAvailable(), is(true));
            assertThat(standalone.contextHandler.isStarted(), is(true));
            assertThat(standalone.jetty.isStarted(), is(true));
            
            String page = get("http://localhost:" + RANDOM_PORT + "/home");
            
            assertThat(page, containsString("Hello World!"));
        } finally {
            standalone.shutdown();
        }
    }
    
    
    @Test
    public void jettyConfigurationWithContext() throws Exception {
        // use test resource of "jetty.xml" but we need to swap into a new
        // random port and then write the file back out
        String jettyConfiguration = createJettyConfiguration("jetty.xml", RANDOM_PORT);
        
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.com.example.conf")
            .contextPath("/mycontext")
            .jettyConfiguration(jettyConfiguration);
        
        try {
            standalone.start();
            
            // port won't be correct b/c actually configured via jetty file
            assertThat(standalone.ninjaServletListener, is(not(nullValue())));
            assertThat(standalone.contextHandler.isAvailable(), is(true));
            assertThat(standalone.contextHandler.isStarted(), is(true));
            assertThat(standalone.jetty.isStarted(), is(true));
            
            String page;
            
            page = get("http://localhost:" + RANDOM_PORT + "/mycontext/home");
            
            assertThat(page, containsString("Hello World!"));
            
            
            page = get("http://localhost:" + RANDOM_PORT + "/mycontext/context_path");
            
            // requestPath removes contextPath
            assertThat(page, containsString("/mycontext"));
            
            
            page = get("http://localhost:" + RANDOM_PORT + "/mycontext/request_path");
            
            // requestPath removes contextPath
            assertThat(page, containsString("/request_path"));
            
            // is the port correct (otherwise logging will be wrong)
            assertThat(standalone.getPort(), is(RANDOM_PORT)); 
        } finally {
            standalone.shutdown();
        }
    }
    
    static public String createJettyConfiguration(String confName, int port) throws Exception {
        URL jettyConfig = NinjaJettyTest.class.getResource("/conf/" + confName);
        
        String jettyConfigString = IOUtils.toString(jettyConfig, "UTF-8");
        
        // replace port w/ random
        String jettyConfigStringReplaced
                = jettyConfigString.replace("\"8080\"", "\"" + port + "\"");
        
        File jettyConfigFile = new File(jettyConfig.toURI());
        
        File resourceDir = jettyConfigFile.getParentFile();
        
        File newJettyConfigFile = new File(resourceDir, jettyConfigFile.getName() + "-" + port + ".xml");
        
        IOUtils.write(jettyConfigStringReplaced, new FileOutputStream(newJettyConfigFile));
        
        return "conf/" + newJettyConfigFile.getName();
    }
    
    static public String get(String url) throws Exception {
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);

        try (InputStream is = conn.getInputStream()) {
            return IOUtils.toString(conn.getInputStream());
        }
    }
    
    @Test
    public void ssl() throws Exception {
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.minimal.conf")
            .ninjaMode(NinjaMode.test)
            .port(-1)
            .sslPort(RANDOM_PORT);
        
        try {
            standalone.start();
            
            assertThat(standalone.getPort(), is(-1));
            assertThat(standalone.getHost(), is(nullValue()));
            assertThat(standalone.getContextPath(), is(""));
            assertThat(standalone.getNinjaMode(), is(NinjaMode.test));
            assertThat(standalone.getSslPort(), is(RANDOM_PORT));
            assertThat(standalone.getSslKeystoreUri(), is(new URI(Standalone.DEFAULT_DEV_NINJA_SSL_KEYSTORE_URI)));
            assertThat(standalone.getSslKeystorePassword(), is(Standalone.DEFAULT_DEV_NINJA_SSL_KEYSTORE_PASSWORD));
            assertThat(standalone.getSslTruststoreUri(), is(new URI(Standalone.DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_URI)));
            assertThat(standalone.getSslTruststorePassword(), is(Standalone.DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_PASSWORD));

            assertThat(standalone.getServerUrls().get(0), is("https://localhost:" + RANDOM_PORT));
            assertThat(standalone.contextHandler, is(not(nullValue())));
            assertNotNull(standalone.ninjaServletListener);
            assertThat(standalone.contextHandler.isAvailable(), is(true));
            assertThat(standalone.contextHandler.isStarted(), is(true));
            assertThat(standalone.jetty.isStarted(), is(true));
            
            
            
            standalone.shutdown();
            
            assertThat(standalone.contextHandler.isStopped(), is(true));
            assertThat(standalone.jetty.isStopped(), is(true));
        } finally {
            standalone.shutdown();
        }
    }
    
    @Test
    public void sessionsAreNotSharedOnSingleResultInstances() throws Exception {
        // this test is not really specific to jetty, but its easier to test here
        NinjaJetty standalone = new NinjaJetty()
            .externalConfigurationPath("conf/jetty.com.session.conf")
            .port(RANDOM_PORT);
        
        try {
            standalone.start();
            
            // establish session with a set-cookie header
            HttpRequest client0 = 
                HttpRequest.get(standalone.getBaseUrls().get(0) + "/getOrCreateSession");
            
            assertThat(client0.code(), is(200));
            assertThat(client0.header("Set-Cookie"), is(not(nullValue())));
            
            // call redirect so its session is processed first time (triggers bug for issue #450)
            HttpRequest client1 = 
                HttpRequest.get(standalone.getBaseUrls().get(0) + "/badRoute")
                    .header("Cookie", client0.header("Set-Cookie"))
                    .followRedirects(false);
            
            assertThat(client1.code(), is(303));
            assertThat(client1.header("Set-Cookie"), is(not(nullValue())));
            
            // call redirect with a mock new browser (no cookie header) -- we
            // should not get a session value back
            HttpRequest client2 = 
                HttpRequest.get(standalone.getBaseUrls().get(0) + "/badRoute")
                    .followRedirects(false);
            
            assertThat(client2.code(), is(303));
            // if cookie is NOT null then someone elses cookie (from the previous
            // request above) got assigned to us!
            assertThat(client2.header("Set-Cookie"), is(nullValue()));
            
        } finally {
            standalone.shutdown();
        }
    }
    
    @Test
    public void directoryListingIsForbidden() throws Exception {
        NinjaJetty standalone = new NinjaJetty()
                .externalConfigurationPath("conf/jetty.minimal.conf")
                .port(RANDOM_PORT);
        try {
            standalone.start();
            String directoryLisingAllowed = standalone.contextHandler.getInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed");
            assertThat(directoryLisingAllowed, is("false"));
        } finally {
            standalone.shutdown();
        }
    }
    
    @Test
    public void checkThatSilentErrorHandlerIsRegistered() throws Exception {
        NinjaJetty standalone = new NinjaJetty()
                .externalConfigurationPath("conf/jetty.minimal.conf")
                .port(RANDOM_PORT);
        try {
            standalone.start();
            ErrorHandler errorHandler = standalone.contextHandler.getErrorHandler();
            assertThat(errorHandler, instanceOf(NinjaJetty.SilentErrorHandler.class));
        } finally {
            standalone.shutdown();
        }
    }
}
