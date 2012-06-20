package ninja;

import org.fluentlenium.adapter.FluentTest;
import org.junit.BeforeClass;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public abstract class NinjaIntegrationTest extends FluentTest {

	public WebDriver webDriver = new HtmlUnitDriver();
	
	@BeforeClass
	public static void startup() {

		  try {
		        Server server = new Server();
		        Connector con = new SelectChannelConnector();
		        con.setPort(8080);
		        server.addConnector(con);
		        Context context = new Context(server, "/");
		        // We need a default servlet. because the dispatcher filter
		        // is only decorating the servlet.
		        context.addServlet(DefaultServlet.class, "/*") ;
		        context.addFilter(new FilterHolder(new NinjaServletDispatcher()), "/*", Handler.ALL);
		        server.start();
		    } catch (Exception ex) {
		        System.err.println(ex);
		    }
	}
	
	
    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

}
