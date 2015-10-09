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

import com.google.inject.CreationException;
import com.google.inject.Injector;
import ninja.servlet.NinjaServletListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;

public class NinjaJetty extends AbstractStandalone<NinjaJetty> {
    
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
        jettyConfiguration(configurationHelper.get(
                KEY_NINJA_JETTY_CONFIGURATION, this.jettyConfiguration, DEFAULT_JETTY_CONFIGURATION));
        
        // build jetty server, context, and servlet
        if (this.jettyConfiguration != null) {
            
            String[] configs = this.jettyConfiguration.split(",");
            for (String config : configs) {
                jetty = buildServerOrApplyConfiguration(config, jetty);
            }
            
        } else {
            
            // create very simple jetty configuration
            jetty = new Server();
        
            // build connector
            ServerConnector http = new ServerConnector(jetty);

            http.setPort(port);
            http.setIdleTimeout(idleTimeout);
            if (host != null) {
                http.setHost(host);
            }

            // set the connector
            jetty.addConnector(http);
        }
        
        this.ninjaServletListener.setNinjaProperties(ninjaProperties);
        
        this.contextHandler = new ServletContextHandler(jetty, getContextPath());
        this.contextHandler.addEventListener(ninjaServletListener);
        this.contextHandler.addFilter(GuiceFilter.class, "/*", null);
        this.contextHandler.addServlet(DefaultServlet.class, "/");
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
            // keep trying
        }
           
        try {
            if (this.jetty != null) {
                logger.info("Trying to stop jetty {}", getLoggableIdentifier());
                this.jetty.stop();
                this.jetty.destroy();
                logger.info("Stopped jetty {}", getLoggableIdentifier());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
}
