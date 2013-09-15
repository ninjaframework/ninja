package ninja.standalone;

import java.net.URI;

import ninja.servlet.NinjaServletListener;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

public class NinjaJetty {
    
    static final int DEFAULT_PORT = 8080;
    
    Integer port;
    
    URI serverUri;
    
    NinjaMode ninjaMode;
    
    Server server;
    
    ServletContextHandler context;
    
    public static void main(String [] args) {
        
        NinjaMode ninjaMode = NinjaModeHelper.determineModeFromSystemPropertiesOrDevIfNotSet();
        
        int port = tryToGetPortFromSystemPropertyOrReturnDefault();
        
        NinjaJetty ninjaJetty = new NinjaJetty();
        ninjaJetty.setNinjaMode(ninjaMode);
        ninjaJetty.setPort(port);
        
        ninjaJetty.start();
        
    }
    
    public NinjaJetty() {
        
        //some sensible defaults
        port = 8080;
        serverUri = URI.create("http://localhost:" + port);
        ninjaMode = NinjaMode.dev;
        
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
    
    public void start() {

        server = new Server(port);

        try {
            ServerConnector http = new ServerConnector(server);

            server.addConnector(http);
            context = new ServletContextHandler(server, "/");

            // Set testmode for Ninja:
            System.setProperty(NinjaConstant.MODE_KEY_NAME,
                    NinjaConstant.MODE_TEST);

            // We are using an embeded jetty for quick server testing. The
            // problem is that the port will change.
            // Therefore we inject the server name here:
            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
            ninjaProperties.setProperty(NinjaConstant.serverName, serverUri.toString());

            NinjaServletListener ninjaServletListener = new NinjaServletListener();
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
            String portAsString = System.getProperty("ninja.port");
            port = Integer.parseInt(portAsString);
        } catch (Exception e) {
            
            return DEFAULT_PORT;
        }
        
        return port;
        
    }


}
