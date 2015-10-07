/*
 * Copyright 2015 Joe Lauer, Fizzed, Inc.
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
package ninja.standalone;

import com.google.inject.CreationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import org.apache.commons.io.IOUtils;
import static org.hamcrest.CoreMatchers.containsString;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Joe Lauer (http://twitter.com/jjlauer)
 */
public class NinjaJettyTest {

    static int RANDOM_PORT = StandaloneHelper.findAvailablePort(8081, 9000);
    
    @After
    public void tearDown() {
        // make sure the external conf property is removed after the test
        System.clearProperty(NinjaProperties.NINJA_EXTERNAL_CONF);
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);
    }
    
    @Test
    public void startAndShutdownWithDefaults() throws Exception {
        // absolute minimal working version of application.conf
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/minimal.conf");
        
        // only way to make this test not rely on a port is to at least set the port
        NinjaJetty nj = new NinjaJetty()
            .port(RANDOM_PORT);
        
        try {
            assertEquals(new Integer(RANDOM_PORT), nj.getPort());
            assertNull(nj.getHost());
            assertNull(nj.getContext());
            assertEquals(NinjaMode.prod, nj.getNinjaMode());
            
            nj.start();
            
            assertNotNull("http://localhost:" + RANDOM_PORT, nj.getNinjaProperties().get(NinjaConstant.serverName));
            assertNotNull(nj.contextHandler);
            assertNotNull(nj.ninjaServletListener);
            assertTrue(nj.contextHandler.isAvailable());
            assertTrue(nj.contextHandler.isStarted());
            assertTrue(nj.jettyServer.isStarted());
            
            nj.shutdown();
            
            assertTrue(nj.contextHandler.isStopped());
            assertTrue(nj.jettyServer.isStopped());
            
        } finally {
            nj.shutdown();
        }
    }
    
    @Test
    public void startAndShutdownWithEverythingConfigured() throws Exception {
        // absolute minimal working version of application.conf
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/minimal.conf");
        System.setProperty(NinjaConstant.MODE_KEY_NAME, "test");
        
        NinjaJetty nj = new NinjaJetty()
            .port(RANDOM_PORT)
            .host("localhost")
            .context("mycontext");
        
        try {
            assertEquals(new Integer(RANDOM_PORT), nj.port);
            assertEquals("localhost", nj.host);
            assertEquals("mycontext", nj.context);
            assertEquals(NinjaMode.test, nj.ninjaMode);
            
            nj.start();
            
            assertNotNull("http://localhost:" + RANDOM_PORT, nj.ninjaProperties.get(NinjaConstant.serverName));
            assertTrue(nj.ninjaProperties.isTest());
            assertEquals("mycontext", nj.contextHandler.getContextPath());
            
            assertNotNull(nj.context);
            assertNotNull(nj.ninjaServletListener);
            assertTrue(nj.contextHandler.isAvailable());
            assertTrue(nj.contextHandler.isStarted());
            assertTrue(nj.jettyServer.isStarted());
            
            nj.shutdown();
            
            assertTrue(nj.contextHandler.isStopped());
            assertTrue(nj.jettyServer.isStopped());
            
        } finally {
            nj.shutdown();
        }
    }
    
    @Test
    public void missingConfigurationThrowsException() throws Exception {
        // bad configuration file will throw exception when creating NinjaPropertiesImpl
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/empty.conf");
        
        NinjaJetty nj = new NinjaJetty()
            .port(RANDOM_PORT);
        
        try {
            nj.start();
            fail("start() should have thrown exception");
        } catch (RuntimeException e) {
            // expected exception 
        } finally {
            // this helps make tests more resilent to failures
            nj.shutdown();
        }
    }
    
    @Test
    public void missingLanguageThrowsInjectorException() throws Exception {
        // bad configuration file will throw exception when creating NinjaPropertiesImpl
        // that exception occurs in NinjaBootstrap during injector creation
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/missinglang.conf");
        
        NinjaJetty nj = new NinjaJetty()
            .port(RANDOM_PORT);
        
        try {
            nj.start();
            fail("start() should have thrown exception");
        } catch (CreationException e) {
            // with the special setup of the servlet in standalone mode -- we
            // request the injector exception to be logged and accessible -- and
            // that is the exception we expect to the thrown from start()
        } finally {
            // this helps make tests more resilent to failures
            nj.shutdown();
        }
    }
    
    @Test
    public void systemPropertiesConfiguresNinjaJetty() throws Exception {
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/minimal.conf");
        
        // as though we called on command-line with following system properties
        System.setProperty("ninja.mode", "test");
        System.setProperty("ninja.port", Integer.toString(RANDOM_PORT));
        System.setProperty("ninja.context", "mycontext");
        System.setProperty("ninja.host", "localhost");
        System.setProperty("ninja.idle.timeout", "60000");
        
        final NinjaJetty nj = new NinjaJetty();
        
        try {
            
            // since run() method joins() the server -- it's now a blocking
            // method and won't return -- we need to do that in another thread
            Thread runThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    nj.run(); 
                }
            });
            
            try {
                runThread.start();
                
                // hackish way to wait for jetty server to start for up to 4 seconds
                long waitTill = System.currentTimeMillis() + 4000;
                while ((nj.jettyServer == null || nj.jettyServer.isStarting()) && System.currentTimeMillis() <= waitTill) {
                    Thread.sleep(50);
                }
                
                assertNotNull(nj.jettyServer);
                assertNotNull(nj.ninjaProperties);
                assertNotNull("http://localhost:" + RANDOM_PORT, nj.ninjaProperties.get(NinjaConstant.serverName));
                assertEquals(new Long(60000L), nj.idleTimeout);
                assertTrue(nj.ninjaProperties.isTest());
                assertEquals("mycontext", nj.contextHandler.getContextPath());
                
                assertNotNull(nj.context);
                assertNotNull(nj.ninjaServletListener);
                assertTrue(nj.contextHandler.isAvailable());
                assertTrue(nj.contextHandler.isStarted());
                assertTrue(nj.jettyServer.isStarted());
                
                nj.shutdown();
            
                assertTrue(nj.contextHandler.isStopped());
                assertTrue(nj.jettyServer.isStopped());
            
            } finally {
                
                runThread.interrupt();
                nj.shutdown();
                
            }
            
        } finally {
            // this helps make tests more resilent to failures
            nj.shutdown();
        }
    }
    
    @Test
    public void startWithJettyConfiguration() throws Exception {
        // use test resource of "jetty.xml" but we need to swap into a new
        // random port and then write the file back out
        URL jettyConfig = this.getClass().getResource("/conf/jetty.xml");
        
        String jettyConfigString = IOUtils.toString(jettyConfig, "UTF-8");
        
        // replace port w/ random
        String jettyConfigStringReplaced
                = jettyConfigString.replace("\"8080\"", "\"" + RANDOM_PORT + "\"");
        
        File jettyConfigFile = new File(jettyConfig.toURI());
        
        File resourceDir = jettyConfigFile.getParentFile();
        
        File newJettyConfigFile = new File(resourceDir, "jetty-new.xml");
        
        IOUtils.write(jettyConfigStringReplaced, new FileOutputStream(newJettyConfigFile));
        
        // absolute minimal working version of application.conf
        System.setProperty(NinjaProperties.NINJA_EXTERNAL_CONF, "conf/minimal.conf");
        
        NinjaJetty nj = new NinjaJetty()
            .jettyConfiguration("conf/jetty-new.xml");
        
        try {
            nj.start();
            
            // confirm we started?
            URL testUrl = new URL("http://localhost:" + RANDOM_PORT + "/");
            
            URLConnection conn = testUrl.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            
            try {
                String testContents = IOUtils.toString(conn.getInputStream());
            } catch (IOException e) {
                // we expect a 500 error since no app really exists
                assertThat(e.getMessage(), containsString("500"));
            }
            
            assertNotNull(nj.context);
            assertNotNull(nj.ninjaServletListener);
            assertTrue(nj.contextHandler.isAvailable());
            assertTrue(nj.contextHandler.isStarted());
            assertTrue(nj.jettyServer.isStarted());
            
            nj.shutdown();
            
            assertTrue(nj.contextHandler.isStopped());
            assertTrue(nj.jettyServer.isStopped());
            
        } finally {
            nj.shutdown();
        }
    }
    
}
