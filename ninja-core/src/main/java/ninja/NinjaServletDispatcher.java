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

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

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

	public void init(FilterConfig filterConfig) throws ServletException {

		try {
			// By convention the initial configuration has to be placed in a
			// guice module in conf.Configuration.
			Class clazz = Class.forName("conf.Configuration");

			// Create a new instance
			Module configuration = (Module) clazz.newInstance();

			// And let the injector generate all instances and stuff:
			injector = Guice.createInjector(configuration);

			// And that's our nicely configured main handler for the framework:
			ninja = injector.getInstance(Ninja.class);

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

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

		// We don't need the injector and ninja any more. Destroy!
		injector = null;
		ninja = null;

	}

}
