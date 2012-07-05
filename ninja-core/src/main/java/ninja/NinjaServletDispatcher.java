package ninja;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.application.ApplicationRoutes;

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
		    List<Module> modulesToLoad = new ArrayList<Module>();  
		    
			// Get base configuration of Ninja:
			Class ninjaConfigurationClass = Class.forName("ninja.Configuration");
			Module ninjaConfiguration = (Module) ninjaConfigurationClass.newInstance();
			modulesToLoad.add(ninjaConfiguration);
			
			// Load main application module:
			if (doesClassExist("conf.Configuration")) {
			    Class applicationConfigurationClass = Class.forName("conf.Configuration");
	            Module applicationConfiguration = (Module) applicationConfigurationClass.newInstance();
	            modulesToLoad.add(applicationConfiguration);
			}
			

			// And let the injector generate all instances and stuff:
			injector = Guice.createInjector(modulesToLoad);

			// And that's our nicely configured main handler for the framework:
			ninja = injector.getInstance(Ninja.class);
			
			
	         // Init routes
            if (doesClassExist("conf.Routes")) {
                Class clazz = Class.forName("conf.Routes");
                ApplicationRoutes applicationRoutes = (ApplicationRoutes) injector.getInstance(clazz);
                
                //System.out.println("init routes");
                Router router = injector.getInstance(Router.class);
                
                applicationRoutes.init(router);
                
            }
	

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
	
	/**
	 * TODO => I want to live somewhere else...
	 * 
	 * 
	 */
	private boolean doesClassExist(String nameWithPackage) {
	    
	    boolean exists = false;
	    
	    try {
            Class.forName(nameWithPackage, false, this.getClass().getClassLoader());
            exists = true;
        } catch (ClassNotFoundException e) {
            exists = false;
        }
	    
	    return exists;
	    
	}

}
