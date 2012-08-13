package ninja;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import ninja.params.ControllerMethodInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

class RouteBuilderImpl implements RouteBuilder {

	private static final Logger log = LoggerFactory
			.getLogger(RouteBuilder.class);

	private String httpMethod;
	private String uri;
	private Class controller;
	private Method controllerMethod;

	public RouteBuilderImpl GET() {
		httpMethod = "GET";
		return this;
	}

	public RouteBuilderImpl POST() {
		httpMethod = "POST";
		return this;
	}

	public RouteBuilderImpl PUT() {
		httpMethod = "PUT";
		return this;
	}

	public RouteBuilderImpl DELETE() {
		httpMethod = "DELETE";
		return this;
	}

	public RouteBuilderImpl OPTION() {
		httpMethod = "OPTION";
		return this;
	}

	@Override
	public void with(Class controller, String controllerMethod) {
		this.controller = controller;
		this.controllerMethod = verifyThatControllerAndMethodExists(controller,
				controllerMethod);
	}

	@Override
	public RouteBuilder route(String uri) {
		this.uri = uri;
		return this;
	}

	/**
	 * Routes are usually defined in conf/Routes.java as
	 * router.GET().route("/teapot").with(FilterController.class, "teapot");
	 * 
	 * Unfortunately "teapot" is not checked by the compiler. We do that here at
	 * runtime.
	 * 
	 * We are reloading when there are changes. So this is almost as good as
	 * compile time checking.
	 * 
	 * @param controller
	 *            The controller class
	 * @param controllerMethod
	 *            The method
	 * @return The actual method
	 */
	private Method verifyThatControllerAndMethodExists(
			Class controller,
			String controllerMethod) {

		try {
			
			Method methodFromQueryingClass = null;
			
			// 1. Make sure method is in class
			// 2. Make sure only one method is there. Otherwise we cannot really know what
			//    to do with the parameters.
			for (Method method : controller.getMethods()) {
				
				if (method.getName().equals(controllerMethod)) {
					if (methodFromQueryingClass == null) {
						methodFromQueryingClass = method;
					} else {
						throw new NoSuchMethodException();
					}
					
				}
				
			}
			
			if (methodFromQueryingClass == null) {
				throw new NoSuchMethodException();
			}

			//make sure that the return type of that controller method
			//is of type Result.
			if (methodFromQueryingClass.getReturnType().isAssignableFrom(Result.class)) {
				return methodFromQueryingClass;
			} else {
				throw new NoSuchMethodException();
			}

		} catch (SecurityException e) {
			log.error(
					"Error while checking for valid Controller / controllerMethod combination",
					e);
		} catch (NoSuchMethodException e) {

			log.error("Error in route configuration!!!");
			log.error("Can not find Controller " + controller.getName()
					+ " and method " + controllerMethod);
			log.error("Hint: make sure the controller returns a ninja.Result!");
			log.error("Hint: Ninja does not allow more than one method with the same name!");
		}
		return null;
	}

	/**
	 * Build the route
	 * 
	 * @param injector
	 *            The injector to build the route with
	 */
	public Route buildRoute(Injector injector) {
		// Calculate filters
		LinkedList<Class<? extends Filter>> filters = new LinkedList<Class<? extends Filter>>();
        filters.addAll(calculateFiltersForClass(controller));
        FilterWith filterWith = controllerMethod
                .getAnnotation(FilterWith.class);
        if (filterWith != null) {
            filters.addAll(Arrays.asList(filterWith.value()));
        }
		return new Route(httpMethod, uri, controller, controllerMethod,
				buildFilterChain(injector, filters, controller,
						controllerMethod));
	}

	private FilterChain buildFilterChain(Injector injector,
			LinkedList<Class<? extends Filter>> filters, Class<?> controller,
			Method controllerMethod) {
	    
		if (filters.isEmpty()) {
		    
			return new FilterChainEnd(injector.getProvider(controller),
					ControllerMethodInvoker.build(controllerMethod, injector));
			
		} else {
		    
			Class<? extends Filter> filter = filters.pop();
			
			return new FilterChainImpl(injector.getProvider(filter),
					buildFilterChain(injector, filters, controller,
							controllerMethod));
			
		}
	}

	private Set<Class<? extends Filter>> calculateFiltersForClass(
			Class controller) {
		LinkedHashSet<Class<? extends Filter>> filters = new LinkedHashSet<Class<? extends Filter>>();
		// First step up the superclass tree, so that superclass filters come
		// first
		// Superclass
		if (controller.getSuperclass() != null) {
			filters.addAll(calculateFiltersForClass(controller.getSuperclass()));
		}
		// Interfaces
		if (controller.getInterfaces() != null) {
			for (Class clazz : controller.getInterfaces()) {
				filters.addAll(calculateFiltersForClass(clazz));
			}
		}
		// Now add from here
		FilterWith filterWith = (FilterWith) controller
				.getAnnotation(FilterWith.class);
		if (filterWith != null) {
			filters.addAll(Arrays.asList(filterWith.value()));
		}
		// And return
		return filters;
	}
}
