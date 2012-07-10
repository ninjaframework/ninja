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

import com.google.inject.Injector;

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
		
		NinjaBootup ninjaBootup = new NinjaBootup();
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
