package ninja;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.utils.NinjaConstant;

import com.google.inject.Injector;
import ninja.utils.NinjaPropertiesImpl;

/**
 * A simple servlet filter that allows us to run Ninja inside any servlet container.
 * 
 * This dispatcher targets Servlet 2.5.
 * 
 * @author ra
 * 
 */
public class NinjaServletDispatcher implements Filter {

	/**
	 * Main injector for the class.
	 */
	private Injector injector;

	/**
	 * Our implementation for Ninja. Handles the complete lifecycle of the app.
	 * Dispatches routes. Applies filters and so on.
	 */
	private Ninja ninja;

	private final String serverName;

	/**
	 * Special constructor for usage in JUnit tests.
	 * 
	 * We are using an embeded jetty for quick server testing. The problem is
	 * that the port will change.
	 * 
	 * Therefore we inject the server name here:
	 * 
	 * @param serverName
	 *            The injected server name. Will override property serverName in
	 *            Ninja properties.
	 */
	public NinjaServletDispatcher(String serverName) {
		this.serverName = serverName;
	}

	public NinjaServletDispatcher() {
		// default constructor used in PROD and DEV modes.
		// Especially serverName will be set from application.conf.
		this.serverName = null; // intentionally null.
	}

	public void init(FilterConfig filterConfig) throws ServletException {

        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl();
        // force set serverName when in test mode:
        if (serverName != null) {
            ninjaProperties.setProperty(NinjaConstant.serverName, serverName);
        }

        NinjaBootup ninjaBootup = new NinjaBootup(ninjaProperties);

        injector = ninjaBootup.getInjector();

        ninja = injector.getInstance(Ninja.class);
        ninja.start();

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
	        FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		// We generate a Ninja compatible context element
		ContextImpl context = (ContextImpl) injector.getProvider(Context.class).get();
		

		// And populate it
		context.init(request, response);


		// And invoke ninja on it.
		// Ninja handles all defined routes, filters and much more:
		ninja.invoke(context);

	}

	public void destroy() {

        ninja.shutdown();
		// We don't need the injector and ninja any more. Destroy!
		injector = null;
		ninja = null;

	}
	


}
