/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import com.google.inject.Injector;
import ninja.servlet.NinjaServletListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ninja.servlet.NinjaServletFilter;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ninja standalone implemented with Jetty.
 */
public class NinjaJetty extends AbstractStandalone<NinjaJetty> {
    static final private Logger log = LoggerFactory.getLogger(NinjaJetty.class);
    
    static final public String KEY_NINJA_JETTY_CONFIGURATION = "ninja.jetty.configuration";

    static final public String DEFAULT_JETTY_CONFIGURATION = null;
    
    final protected NinjaServletListener ninjaServletListener;
    protected Server jetty;
    protected ServletContextHandler contextHandler;
    protected String jettyConfiguration;
    
    public NinjaJetty() {
        super("NinjaJetty");
        this.ninjaServletListener = new NinjaServletListener();
    }
    
    public static void main(String [] args) {
        // create new instance and run it
        new NinjaJetty().run();
    }
    
    @Override
    protected void doConfigure() throws Exception {
        // current value or system property or conf/application.conf or default value
        jettyConfiguration(overlayedNinjaProperties.get(
                KEY_NINJA_JETTY_CONFIGURATION, this.jettyConfiguration, DEFAULT_JETTY_CONFIGURATION));
        
        // build jetty server, context, and servlet
        if (this.jettyConfiguration != null) {
            
            String[] configs = this.jettyConfiguration.split(",");
            for (String config : configs) {
                jetty = buildServerOrApplyConfiguration(config, jetty);
            }
            
            // since we don't know host and port, try to get it from jetty
            tryToSetHostAndPortFromJetty();
            
        } else {
            
            // create very simple jetty configuration
            jetty = new Server();
        
            if (port > -1) {
                // build http cleartext connector
                ServerConnector http = new ServerConnector(jetty);

                http.setPort(port);
                http.setIdleTimeout(idleTimeout);
                if (host != null) {
                    http.setHost(host);
                }

                jetty.addConnector(http);
            }
            

            if (sslPort > -1) {
                // build https secure connector
                // http://git.eclipse.org/c/jetty/org.eclipse.jetty.project.git/tree/examples/embedded/src/main/java/org/eclipse/jetty/embedded/ManyConnectors.java
                HttpConfiguration httpConfig = new HttpConfiguration();
                httpConfig.setSecureScheme("https");
                httpConfig.setSecurePort(sslPort);
                httpConfig.setOutputBufferSize(32768);
                
                HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
                httpsConfig.addCustomizer(new SecureRequestCustomizer());

                // unfortunately jetty seems to only work when we pass a keystore
                // and truststore (as opposed to our own prepared SSLContext)
                // call createSSLContext() to simply verify configuration is correct
                this.createSSLContext();
                
                SslContextFactory sslContextFactory = new SslContextFactory();
                sslContextFactory.setKeyStore(StandaloneHelper.loadKeyStore(this.sslKeystoreUri, this.sslKeystorePassword.toCharArray()));
                sslContextFactory.setKeyManagerPassword(this.sslKeystorePassword);
                sslContextFactory.setTrustStore(StandaloneHelper.loadKeyStore(this.sslTruststoreUri, this.sslTruststorePassword.toCharArray()));
                
                ServerConnector https = new ServerConnector(
                    jetty,
                    new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                    new HttpConnectionFactory(httpsConfig));

                https.setPort(sslPort);
                https.setIdleTimeout(idleTimeout);
                
                jetty.addConnector(https);
            }
        }
        
        this.ninjaServletListener.setNinjaProperties(ninjaProperties);
        
        this.contextHandler = new ServletContextHandler(jetty, getContextPath());
        
        this.contextHandler.addEventListener(ninjaServletListener);
        this.contextHandler.addFilter(NinjaServletFilter.class, "/*", null);
        this.contextHandler.addServlet(DefaultServlet.class, "/");
        
        // Note: adding websockets support (e.g. upgrade filter) last (after user filters)
        // is how jetty, tomcat, and wildfly all do it. We mimic it here.
        WebSocketServerContainerInitializer.configureContext(this.contextHandler);
        
        // disable directory browsing
        this.contextHandler.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");
        // Add an error handler that does not print stack traces in case
        // something happens that is not under control of Ninja
        this.contextHandler.setErrorHandler(new SilentErrorHandler());
    }  

    @Override
    public void doStart() throws Exception {
        String version = this.jetty.getClass().getPackage().getImplementationVersion();
        
        try {
            logger.info("Trying to start jetty v{} {}", version, getLoggableIdentifier());
            this.jetty.start();
        } catch (Exception e) {
            // since ninja bootstrap actually boots inside a servlet listener
            // the underlying injector exception is wrapped - unwrap here!
            throw tryToUnwrapInjectorException(e);
        }
        
        logger.info("Started jetty v{} {}", version, getLoggableIdentifier());
    }
    
    @Override
    public void doJoin() throws Exception {
        this.jetty.join();
    }
    
    @Override
    public void doShutdown() {
        try {
            if (this.contextHandler != null) {
                this.contextHandler.stop();
                this.contextHandler.destroy();
            }
        } catch (Exception e) {
            // keep 
        }
           
        try {
            if (this.jetty != null) {
                logger.info("Trying to stop jetty {}", getLoggableIdentifier());
                this.jetty.stop();
                this.jetty.destroy();
                logger.info("Stopped jetty {}", getLoggableIdentifier());
            }
        } catch (Exception e) {
            log.error("Unable to cleanly stop jetty", e);
            //throw new RuntimeException(e);
        }
    }
    
    public String getJettyConfigureation() {
        return this.jettyConfiguration;
    }
    
    public NinjaJetty jettyConfiguration(String jettyConfiguration) {
        this.jettyConfiguration = jettyConfiguration;
        return this;
    }
    
    @Override
    public Injector getInjector() {
        // only available after configure()
        checkConfigured();
        return ninjaServletListener.getInjector();
    }
    
    private void tryToSetHostAndPortFromJetty() {
        // do best attempt at fetching port jetty will start with
        Connector[] connectors = jetty.getConnectors();
        if (connectors != null && connectors.length > 0 && connectors[0] instanceof ServerConnector) {
            ServerConnector connector = (ServerConnector)connectors[0];
            host(connector.getHost());
            port(connector.getPort());
        }
    }
    
    private Server buildServerOrApplyConfiguration(String jettyConfiguration, Server server) throws Exception {
        // try local file first
        Resource jettyConfigurationFile = Resource.newResource(jettyConfiguration);
        
        if (jettyConfigurationFile == null || !jettyConfigurationFile.exists()) {
            // fallback to classpath
            jettyConfigurationFile = Resource.newClassPathResource(jettyConfiguration);
            
            if (jettyConfigurationFile == null || !jettyConfigurationFile.exists()) {
                throw new FileNotFoundException("Unable to find jetty configuration file either locally or on classpath '" + jettyConfiguration + "'");
            }
        }
        
        logger.info("Applying jetty configuration '{}'", jettyConfigurationFile);
        
        try (InputStream is = jettyConfigurationFile.getInputStream()) {
            XmlConfiguration configuration = new XmlConfiguration(is);

            // create or apply to existing
            if (server == null) {
                return (Server)configuration.configure();
            } else {
                return (Server)configuration.configure(server);
            }
        }
    }
    
    /**
     * That error handler does not print out any stack traces.
     * 
     * In production it may happen that an exception occurs that is outside
     * of the control of Ninja. The default error handler happily prints out 
     * stacktraces. This is not what you want in production systems.
     * 
     * More here: https://virgo47.wordpress.com/2015/02/07/jetty-hardening/
     * 
     * Therefore we simply print out a very simple error and log the problem.
     */
    public static class SilentErrorHandler extends ErrorPageErrorHandler {
        private static final Logger log = LoggerFactory.getLogger(SilentErrorHandler.class);
        
        final String DEFAULT_RESPONSE_HTML =
            "<html>\n" +
            "  <head>\n" +
            "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>\n" +
            "    <title>Oops. An error occurred.</title>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <h1>Oops. An error occurred.</h1>\n" +
            "    <p>Please contact support if error persists.</p>\n" +       
            "  </body>\n" +
            "</html>";
        
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            log.error("An error occurred that cannot be handled by Ninja {}. Responsing with default error page.", target);
            response.getWriter().append(DEFAULT_RESPONSE_HTML);
        }
    }
}
