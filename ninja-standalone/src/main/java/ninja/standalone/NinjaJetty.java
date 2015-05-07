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

package ninja.standalone;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.google.inject.CreationException;
import ninja.servlet.NinjaServletListener;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import java.io.FileNotFoundException;
import java.net.URI;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NinjaJetty {
    static private final Logger logger = LoggerFactory.getLogger(NinjaJetty.class);
    
    public final static String COMMAND_LINE_PARAMETER_NINJA_CONTEXT = "ninja.context";
    public final static String COMMAND_LINE_PARAMETER_NINJA_PORT = "ninja.port";
    public final static String COMMAND_LINE_PARAMETER_NINJA_HOST = "ninja.host";
    public final static String COMMAND_LINE_PARAMETER_NINJA_IDLE_TIMEOUT = "ninja.idle.timeout";
    public final static String COMMAND_LINE_PARAMETER_NINJA_JETTY_CONFIGURATION = "ninja.jetty.configuration";
    
    static final int DEFAULT_PORT = 8080;
    static final String DEFAULT_HOST = null;                // bind to any
    static final long DEFAULT_IDLE_TIMEOUT = 30000;  // set to Jetty 9 default
    
    // configuration
    Integer port;
    String host;
    URI serverUri;
    long idleTimeout;
    NinjaMode ninjaMode;
    String ninjaContextPath;
    String jettyConfiguration;
    // once started
    Server server;
    ServletContextHandler context;
    NinjaPropertiesImpl ninjaProperties;
    final NinjaServletListener ninjaServletListener;

    public static void main(String [] args) {
        
        // create new instance and run it (easier to unit test this way)
        new NinjaJetty().run(args);
        
    }
    
    public NinjaJetty() {
        this.port = DEFAULT_PORT;
        this.host = DEFAULT_HOST;
        this.idleTimeout = DEFAULT_IDLE_TIMEOUT;
        ninjaMode = NinjaMode.prod;
        ninjaServletListener = new NinjaServletListener();
    }
    
    public void run(String[] args) {
        
        // configure self from system properties
        setNinjaMode(NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
        
        setPort(tryToGetPortFromSystemPropertyOrReturnDefault());
        
        setHost(System.getProperty(COMMAND_LINE_PARAMETER_NINJA_HOST, DEFAULT_HOST));
        
        setIdleTimeout(tryToGetIdleTimeoutFromSystemPropertyOrReturnDefault());
        
        setNinjaContextPath(tryToGetContextPathFromSystemPropertyOrReturnDefault());
        
        setJettyConfiguration(System.getProperty(COMMAND_LINE_PARAMETER_NINJA_JETTY_CONFIGURATION));
        
        try {
            
            this.start();
        
        } catch (Exception e) {
            
            logger.error("Unable to start server.", e);
            System.exit(1);
            
        }
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            
            @Override
            public void run() {
                shutdown();
            }
            
        });
        
        try {
            
            // do not simply exit main() -- join server
            server.join();
        
        } catch (InterruptedException e) {
            
            logger.error("Server interrupted (likely server is just shutting down)", e);
            
        }
    }    

    public Injector getInjector() {
        return ninjaServletListener.getInjector();
    }
    
    public NinjaJetty setPort(int port) {
        this.port = port;
        return this;
    }
    
    public NinjaJetty setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }
    
    public NinjaJetty setHost(String host) {
        this.host = host;
        return this;
    }

    public NinjaJetty setJettyConfiguration(String jettyConfiguration) {
        this.jettyConfiguration = jettyConfiguration;
        return this;
    }

    public void setServerUri(URI serverUri) {
        this.serverUri = serverUri;
    }
    
    public String createServerName() {
        String h = this.host;
        if (this.host == null) {
            h = "localhost";
        }
        return "http://" + h + ":" + this.port;
    }
    
    public NinjaJetty setNinjaMode(NinjaMode ninjaMode) {
        this.ninjaMode = ninjaMode;
        return this;
    }

    public NinjaJetty setNinjaContextPath(String ninjaContextPath) {
        this.ninjaContextPath = ninjaContextPath;
        return this;
    }
    
    static private Server createServerOrApplyConfiguration(String jettyConfiguration, Server server) throws Exception {
        
        // try local file first
        Resource jettyConfigurationFile = Resource.newResource(jettyConfiguration);
        
        if (jettyConfigurationFile == null || !jettyConfigurationFile.exists()) {
            
            // fallback to classpath
            jettyConfigurationFile = Resource.newClassPathResource(jettyConfiguration);
            
            if (jettyConfigurationFile == null || !jettyConfigurationFile.exists()) {
            
                throw new FileNotFoundException("Unable to find jetty configuration file either locally or on classpath [" + jettyConfiguration + "]");
                
            }
        }
        
        logger.info("Using jetty configuration file to configure server: " + jettyConfigurationFile);
        
        XmlConfiguration configuration = new XmlConfiguration(jettyConfigurationFile.getInputStream());
        
        // create or apply to existing
        if (server == null) {
            
            return (Server)configuration.configure();
            
        } else {
            
            return (Server)configuration.configure(server);
            
        }
        
        
    }
    
    public void start() throws Exception {

        if (this.jettyConfiguration != null && this.jettyConfiguration.length() > 0) {
            
            String[] configs = this.jettyConfiguration.split(",");
            
            for (String config : configs) {
            
                server = createServerOrApplyConfiguration(config, server);
                
            }
            
        } else {
            
            // create very simple jetty configuration
             server = new Server();
        
            // HTTP connector
            ServerConnector http = new ServerConnector(server);

            http.setPort(port);
            
            http.setIdleTimeout(idleTimeout);

            if (host != null) {
                http.setHost(host);
            }

            // set the connector
            server.addConnector(http);
            
        }
            
        context = new ServletContextHandler(server, ninjaContextPath);
        
        ninjaProperties = new NinjaPropertiesImpl(ninjaMode);

        // We are using an embeded jetty for quick server testing. The
        // problem is that the port will change.
        // Therefore we inject the server name here:
        String serverName = (this.serverUri != null
                                ? this.serverUri.toString() : createServerName());
        
        ninjaProperties.setProperty(NinjaConstant.serverName, serverName);

        ninjaServletListener.setNinjaProperties(ninjaProperties);

        context.addEventListener(ninjaServletListener);
        context.addFilter(GuiceFilter.class, "/*", null);
        context.addServlet(DefaultServlet.class, "/");
            
        try {

            server.start();

        } catch (Exception e) {
            
            // inner exception on guice exception during start?
            if (e.getCause() != null && e.getCause() instanceof CreationException) {
            
                // the injector exception is actually what we want thrown
                throw (CreationException)e.getCause();
                
            } else {
                
                throw e;
                
            }
        }
    }
    
    public void shutdown() {
        
        try {
            
            // shutdown should be safe to call even if server did not start
            if (context != null) {
                context.stop();
                context.destroy();
            }
            
            if (server != null) {
                server.stop();
                server.destroy();
            }
            
        } catch (Exception e) {
            
            throw new RuntimeException(e);
        
        }
    }
    
    public static int tryToGetPortFromSystemPropertyOrReturnDefault() {

        Integer port;

        try {
            
            String portAsString = System.getProperty(COMMAND_LINE_PARAMETER_NINJA_PORT);
            port = Integer.parseInt(portAsString);
        
        } catch (Exception e) {

            return DEFAULT_PORT;
        }

        return port;

    }
    
    public static long tryToGetIdleTimeoutFromSystemPropertyOrReturnDefault() {
        long idleTimeout;
        
        try {
            
            String idleTimeoutAsString = System.getProperty(COMMAND_LINE_PARAMETER_NINJA_IDLE_TIMEOUT);
            idleTimeout = Long.parseLong(idleTimeoutAsString);
            
        } catch(Exception e) {
            
            return DEFAULT_IDLE_TIMEOUT;
        }
        
        return idleTimeout;
    }

    public static String tryToGetContextPathFromSystemPropertyOrReturnDefault() {

        try {

            return System.getProperty(COMMAND_LINE_PARAMETER_NINJA_CONTEXT);

        } catch (Exception e) {

            return null;
        }
    }
    
}
