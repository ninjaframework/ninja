package ninja;

import java.lang.annotation.Annotation;

import ninja.Context.HTTP_STATUS;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

/**
 * Main implementation of the ninja framework.
 * 
 * Roughly works in the following order:
 * 
 * - Gets a request
 * 
 * - Searches for a matching route
 * 
 * - Applies filters
 * 
 * - Executes matching controller
 * 
 * - Returns result
 * 
 * @author ra
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class NinjaImpl implements Ninja {

	/**
	 * The most important thing: A cool logo.
	 */
	private final String NINJA_LOGO = " _______  .___ _______        ____.  _____   \n"
			+ " \\      \\ |   |\\      \\      |    | /  _  \\  \n"
			+ " /   |   \\|   |/   |   \\     |    |/  /_\\  \\ \n"
			+ "/    |    \\   /    |    \\/\\__|    /    |    \\\n"
			+ "\\____|__  /___\\____|__  /\\________\\____|__  /\n"
			+ "     web\\/framework   \\/                  \\/ \n";

	private final Router router;
	private final Injector injector;

	// something like views/notFound404.ftl.html
	// => named so the user can change it to path she likes
	private final String pathToViewNotFound;

	@Inject
	public NinjaImpl(Router router, Injector injector,
			@Named("template404") String pathToViewNotFound) {

		this.router = router;
		this.injector = injector;
		this.pathToViewNotFound = pathToViewNotFound;

		// This system out println is intended.
		System.out.println(NINJA_LOGO);
	}

	/**
	 * I do all the main work.
	 * 
	 * @param Context
	 *            context
	 */
	public void invoke(Context context) {
		
		String httpMethod = context.getHttpServletRequest().getMethod();

		Route route = router.getRouteFor(httpMethod, context.getHttpServletRequest()
				.getRequestURI());

		if (route != null) {
			// process annotations (filters)
			boolean continueExecution = doApplyFilers(route, context);

			// If we are allowed to continue we execute
			// the route itself.
			//
			// It might be for instance that a user is not authenticated and
			// therefore the route itself is not allowed to be executed.
			if (continueExecution) {
				route.invoke(context);

			}
		} else {
			// throw a 404 "not found" because we did not find the route
			context.status(HTTP_STATUS.notFound404)
					.template(pathToViewNotFound).renderHtml();

		}
		
		// We have finished this cycle. We close the streams by force:
		try {
			context.getHttpServletResponse().getOutputStream().close();
			context.getHttpServletResponse().getWriter().close();
			
		} catch (Exception e) {
			//forget the exception if there is one...
		}
		
		

	}

	/**
	 * Processes the FilterWith annotation on top of a router method.
	 * 
	 * If a filter breaks the execution this method will return "false". It will
	 * also stop further executing all remaining filter.
	 * 
	 * Therefore the code in the route itself should not be executed.
	 * 
	 * @param route
	 * @param context
	 * @return
	 */
	public boolean doApplyFilers(Route route, Context context) {

		// default value... continue that execution
		boolean continueExecution = true;

		Class controller = route.getController();
		String controllerMethod = route.getControllerMethod();

		try {
			for (Annotation annotation : controller.getMethod(controllerMethod,
					Context.class).getAnnotations()) {

				if (annotation.annotationType().equals(FilterWith.class)) {

					FilterWith filterWith = (FilterWith) annotation;

					Class[] filters = filterWith.value();

					for (Class filterClass : filters) {

						Filter filter = (Filter) injector
								.getInstance(filterClass);

						filter.filter(context);

						if (!filter.continueExecution()) {
							continueExecution = false;
							break;
						}

					}

				}

			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return continueExecution;

	}

}
