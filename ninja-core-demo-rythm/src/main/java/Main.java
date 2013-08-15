import ninja.utils.NinjaConstant;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {

	public static void main(String[] args) throws Exception {
	 // System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_PROD);
	  System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
	  System.setProperty("org.eclipse.jetty.LEVEL", "WARN");

	  
	  System.out.println("System " + System.getProperty("java.io.tmpdir"));
	  
		String webappDirLocation = "src/main/webapp/";

		Server server = new Server();

		QueuedThreadPool p = (QueuedThreadPool) server.getThreadPool();
		System.out.println("p.getMaxThreads() " + p.getMaxThreads());
		System.out.println("p.isLowOnThreads() " + p.isLowOnThreads());
		
//		server1.setThreadPool(threadPool);
//		LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(maxQueueSize);
//		ExecutorThreadPool pool = new ExecutorThreadPool(minThreads, maxThreads, maxIdleTime, TimeUnit.MILLISECONDS, queue);
//		server.setAttribute(name, attribute)
		
		ServerConnector http = new ServerConnector(server);
        http.setHost("localhost");
        http.setPort(8081);
        http.setIdleTimeout(9000000);
        server.addConnector(http);
		WebAppContext root = new WebAppContext();

		root.setContextPath("/");
		root.setDescriptor(webappDirLocation + "/WEB-INF/web.xml");
		root.setResourceBase(webappDirLocation);
 
		root.setParentLoaderPriority(true);

		server.setStopTimeout(90000000);
		server.setHandler(root);

		server.start();
		server.join();
	}

/*	public static void main1(String[] args) {
		 // use Map to store the configuration
		Map<String, Object> map = new HashMap<String, Object>();
		// tell rythm where to find the template files
		map.put("home.template", "resources");
		// init Rythm with our predefined configuration
		Rythm.init(map);
		//System.out.println(Rythm.engine().render("helloworld.html", "rythm"));
	}
*/
}