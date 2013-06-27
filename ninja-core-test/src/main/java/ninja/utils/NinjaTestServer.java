/**
 * Copyright (C) 2013 the original author or authors.
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

import ninja.servlet.NinjaServletListener;

import org.apache.http.client.utils.URIBuilder;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;

import com.google.inject.servlet.GuiceFilter;

/**
 * Starts a new server using an embedded jetty. Startup is really fast and thus
 * usable in integration tests.
 * 
 * @author rbauer
 */
public class NinjaTestServer {

    private final int port;
    private final Server server;
    private final Context context;
    private final URI serverUri;

    public NinjaTestServer() {

        this.port = findAvailablePort(1000, 10000);
        serverUri = createServerUri();
        server = new Server();

        try {
            Connector con = new SelectChannelConnector();
            con.setPort(port);
            server.addConnector(con);
            context = new Context(server, "/", Context.SESSIONS);
            
            // Set testmode for Ninja:
            System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_TEST);

            // We are using an embeded jetty for quick server testing. The
            // problem is
            // that the port will change.
            // Therefore we inject the server name here:
            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();
            ninjaProperties.setProperty(NinjaConstant.serverName, serverUri.toString());
            
            //context.addServlet(NinjaServletDispatcher.class, "/*");
            NinjaServletListener ninjaServletListener = new NinjaServletListener();
            ninjaServletListener.setNinjaProperties(ninjaProperties);
            
            context.addEventListener(ninjaServletListener);
            context.addFilter(GuiceFilter.class, "/*", Handler.ALL);
            context.addServlet(DefaultServlet.class, "/");
            
            server.start();
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
        try {
            
            server.stop();
            server.destroy(); 
            context.stop();
            context.destroy();
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
