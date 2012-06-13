package ninja;

import java.lang.annotation.Annotation;

import ninja.Context.HTTP_STATUS;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class NinjaImpl implements Ninja {

	public final String NINJA_LOGO = " _______  .___ _______        ____.  _____   \n"
			+ " \\      \\ |   |\\      \\      |    | /  _  \\  \n"
			+ " /   |   \\|   |/   |   \\     |    |/  /_\\  \\ \n"
			+ "/    |    \\   /    |    \\/\\__|    /    |    \\\n"
			+ "\\____|__  /___\\____|__  /\\________\\____|__  /\n"
			+ "     web\\/framework   \\/                  \\/v0.1 \n";

	Router router;
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

		System.out.println(NINJA_LOGO);

	}

	/**
	 * I want to get a session request etc pp...
	 * 
	 * @param uri
	 */
	public void invoke(Context context) {

		Route route = router.getRouteFor(context.getHttpServletRequest()
				.getServletPath());

		if (route != null) {
			//process annotations (filters)
			boolean continueExecution = processAnnotations(route, context);

			//If we are allowed to continue we execute
			//the route itself.
			//
			//It might be for instance that a user is not authenticated and
			//therefore the route itself is not allowed to be executed.
			if (continueExecution) {
				route.invoke(context);

			}
		} else {
			// throw a 404 "not found" because we did not find the route
			context.status(HTTP_STATUS.notFound404)
					.template(pathToViewNotFound).html();

		}

	}

	/**
	 * Processes the FilterWith annotations on top of a router method.
	 * 
	 * If a filter breaks the execution this method will return "false".
	 * 
	 * Therefore the code in the route itself will NOT be executed.
	 * 
	 * @param route
	 * @param context
	 * @return
	 */
	public boolean processAnnotations(Route route, Context context) {

		boolean continueExecution = true;

		Class controller = route.getController();
		String controllerMethod = route.getControllerMethod();

		try {
			for (Annotation annotation : controller.getMethod(controllerMethod,
					Context.class).getAnnotations()) {

				if (annotation.annotationType().equals(FilterWith.class)) {

					FilterWith filterWith = (FilterWith) annotation;

					Filter filter = injector.getInstance(filterWith.value());

					filter.filter(context);

					if (!filter.continueExecution()) {
						continueExecution = false;
						break;
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
