//package ninja;
//
//import org.apache.maven.plugin.AbstractMojo;
//import org.apache.maven.plugin.MojoExecutionException;
//import org.apache.maven.plugins.annotations.Mojo;
//import org.apache.maven.plugins.annotations.ResolutionScope;
//import org.mortbay.jetty.Server;
//import org.mortbay.jetty.webapp.WebAppContext;
//
//@Mojo(name = "once", 
//requiresDependencyCollection = ResolutionScope.RUNTIME_PLUS_SYSTEM,
//requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
//public class NinjaOnceMojo extends AbstractMojo{
//    public void execute() throws MojoExecutionException {
//        
//        long first = System.currentTimeMillis();
//        System.out.println("starting up server");
//
//        Server server = new Server(8080);
//
//        WebAppContext root = new WebAppContext();
//        root.setWar("src/main/webapp/");
//        root.setContextPath("/");
//        server.addHandler(root);
//        try {
//            server.start();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        System.out.println("server starting took: "
//                + (System.currentTimeMillis() - first) + " - "
//                + Server.getVersion());
//    }
//}