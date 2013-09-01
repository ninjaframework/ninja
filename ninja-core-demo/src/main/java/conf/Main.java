package conf;

import ninja.servlet.NinjaServletListener;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaPropertiesImpl;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;

public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {

        int port = 8080;
        Server server;
        ServletContextHandler context;

        server = new Server(port);

        try {
            ServerConnector http = new ServerConnector(server);

            server.addConnector(http);
            context = new ServletContextHandler(server, "/");

            // Set testmode for Ninja:
            System.setProperty(NinjaConstant.MODE_KEY_NAME,
                    NinjaConstant.MODE_TEST);

            // We are using an dembeded jetty for quick server testing. The
            // problem is that the port will change.
            // Therefore we inject the server name here:
            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();
            // ninjaProperties.setProperty(NinjaConstant.serverName,
            // serverUri.toString());

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

}
