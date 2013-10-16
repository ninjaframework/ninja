//package ninja;
//
//import ninja.servlet.NinjaServletListener;
//import ninja.utils.NinjaConstant;
//import ninja.utils.NinjaPropertiesImpl;
//
//import org.mortbay.jetty.Connector;
//import org.mortbay.jetty.Handler;
//import org.mortbay.jetty.Server;
//import org.mortbay.jetty.nio.SelectChannelConnector;
//import org.mortbay.jetty.servlet.Context;
//import org.mortbay.jetty.servlet.DefaultServlet;
//
//import com.google.inject.servlet.GuiceFilter;
//
//public class NServer {
//
//
//    public static void main(String[] a) throws Exception {
//
//
//        long first = System.currentTimeMillis();
//        System.out.println("starting up server");
//
//
//        Server server = new Server();
//
//        try {
//            Connector con = new SelectChannelConnector();
//            con.setPort(8080);
//            server.addConnector(con);
//            Context context = new Context(server, "/", Context.SESSIONS);
//            
//            // Set testmode for Ninja:
//            System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_DEV);
//
//            // We are using an embeded jetty for quick server testing. The
//            // problem is
//            // that the port will change.
//            // Therefore we inject the server name here:
//            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();
//            ninjaProperties.setProperty(NinjaConstant.serverName, "servername");
//            
//            //context.addServlet(NinjaServletDispatcher.class, "/*");
//            NinjaServletListener ninjaServletListener = new NinjaServletListener();
//            ninjaServletListener.setNinjaProperties(ninjaProperties);
//            
//            context.addEventListener(ninjaServletListener);
//            context.addFilter(GuiceFilter.class, "/*", Handler.ALL);
//            context.addServlet(DefaultServlet.class, "/");
//            
//            server.start();
//            
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//
//        System.out.println("server starting took: "
//                + (System.currentTimeMillis() - first) + " - "
//                + Server.getVersion());
//    }
//
//}
