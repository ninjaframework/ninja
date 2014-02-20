package ninja.standalone;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import ninja.servlet.NinjaServletListener;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class NinjaJetty {
    
    static final int DEFAULT_PORT = 8080;

    static final String DEFAULT_CONTEXT_PATH = "/";
    
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
        port = 8080;
        serverUri = URI.create("http://localhost:" + port);
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
            ServerConnector http = new ServerConnector(server);

            server.addConnector(http);
            context = new ServletContextHandler(server, ninjaContextPath);

            // We are using an embeded jetty for quick server testing. The
            // problem is that the port will change.
            // Therefore we inject the server name here:
            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
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
            String portAsString = System.getProperty("ninja.port");
            port = Integer.parseInt(portAsString);
        } catch (Exception e) {
            
            return DEFAULT_PORT;
        }
        
        return port;
        
    }

    public static String tryToGetContextPathFromSystemPropertyOrReturnDefault() {
        try {
            String contextPath = System.getProperty("ninja.context");
            return contextPath != null ? contextPath : DEFAULT_CONTEXT_PATH;
        } catch (Exception e) {
            return DEFAULT_CONTEXT_PATH;
        }
    }
}
