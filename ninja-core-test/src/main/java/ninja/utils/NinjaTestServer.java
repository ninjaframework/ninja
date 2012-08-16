package ninja.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

import ninja.NinjaServletDispatcher;

import org.apache.http.client.utils.URIBuilder;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;

/**
 * Starts a new server using an embedded jetty.
 * Startup is really fast and thus usable in integration tests.
 * 
 * @author rbauer
 */
public class NinjaTestServer {

	private final int port;
	private final Server server;
    private final URI serverUri;
	
	public NinjaTestServer() {
		
		this.port = findAvailablePort(1000, 10000);
        serverUri = createServerUri();
		server = new Server();
		
		try {			
			Connector con = new SelectChannelConnector();
			con.setPort(port);
			server.addConnector(con);
			Context context = new Context(server, "/");
			// We need a default servlet. because the dispatcher filter
			// is only decorating the servlet.
			context.addServlet(DefaultServlet.class, "/*");
            context.addFilter(new FilterHolder(new NinjaServletDispatcher(serverUri.toString())),
			        "/*", Handler.ALL);
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
            return new URIBuilder().setScheme("http").setHost("localhost").setPort(port).build();
        } catch (URISyntaxException e) {
            // should not be able to happen...
            return null;
        }
    }

    public void shutdown() {
        try {
            server.stop();
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
	    throw new IllegalStateException("Could not find available port in range "
	            + min + " to " + max);
	}

}
