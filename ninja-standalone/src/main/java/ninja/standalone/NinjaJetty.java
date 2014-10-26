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

package ninja.standalone;

import java.net.URI;

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

public class NinjaJetty {
    
    public final static String COMMAND_LINE_PARAMETER_NINJA_CONTEXT = "ninja.context";
    public final static String COMMAND_LINE_PARAMETER_NINJA_PORT = "ninja.port";
    
    static final int DEFAULT_PORT = 8080;
    
    Integer port;
    
    URI serverUri;
    
    NinjaMode ninjaMode;
    
    Server server;
    
    ServletContextHandler context;

    String ninjaContextPath;

    NinjaServletListener ninjaServletListener;

    public static void main(String [] args) {
        
        NinjaMode ninjaMode = NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet();
        
        int port = tryToGetPortFromSystemPropertyOrReturnDefault();
        String contextPath = tryToGetContextPathFromSystemPropertyOrReturnDefault();
        
        final NinjaJetty ninjaJetty = new NinjaJetty();
        ninjaJetty.setNinjaMode(ninjaMode);
        ninjaJetty.setPort(port);
        ninjaJetty.setNinjaContextPath(contextPath);
        
        ninjaJetty.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
            
            @Override
            public void run() {
                ninjaJetty.shutdown();
            }
            
        });
        
    }
    
    public NinjaJetty() {
        
        //some sensible defaults
        serverUri = URI.create("http://localhost:" + DEFAULT_PORT);
        ninjaMode = NinjaMode.dev;
        ninjaServletListener = new NinjaServletListener();
    }

    public Injector getInjector() {
        return ninjaServletListener.getInjector();
    }
    
    public NinjaJetty setPort(int port) {
        
        this.port = port;
        return this;
        
    }
    
    public NinjaJetty setServerUri(URI serverUri) {
        
        this.serverUri = serverUri;
        return this;
    }
    
    public NinjaJetty setNinjaMode(NinjaMode ninjaMode) {
        
        this.ninjaMode = ninjaMode;
        return this;
    }

    public NinjaJetty setNinjaContextPath(String ninjaContextPath) {

        this.ninjaContextPath = ninjaContextPath;
        return this;
    }
    
    public void start() {

        server = new Server(port);

        try {
            
            context = new ServletContextHandler(server, ninjaContextPath);
            
            NinjaPropertiesImpl ninjaProperties 
                    = new NinjaPropertiesImpl(ninjaMode);
            // We are using an embeded jetty for quick server testing. The
            // problem is that the port will change.
            // Therefore we inject the server name here:
            ninjaProperties.setProperty(NinjaConstant.serverName, serverUri.toString());

            ninjaServletListener.setNinjaProperties(ninjaProperties);

            context.addEventListener(ninjaServletListener);

            context.addFilter(GuiceFilter.class, "/*", null);
            context.addServlet(DefaultServlet.class, "/");

            server.start();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        
    }
    
    public void shutdown() {
        
        try {
            
            server.stop();
            server.destroy(); 
            context.stop();
            context.destroy();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getServerAddress() {
        return serverUri.toString() + "/";
    }

    public URI getServerAddressAsUri() {
        return serverUri;
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

    public static String tryToGetContextPathFromSystemPropertyOrReturnDefault() {
        
        try {
            
            String contextPath = System.getProperty(COMMAND_LINE_PARAMETER_NINJA_CONTEXT);
            
            return contextPath;
            
        } catch (Exception e) {
            
            return null;
        }
    }
    
}
