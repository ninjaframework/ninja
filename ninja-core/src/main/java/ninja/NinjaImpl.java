package ninja;

import java.lang.annotation.Annotation;

import ninja.Context.HTTP_STATUS;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class NinjaImpl implements Ninja {

	Router router;
	private final Injector injector;
	
	//something like views/notFound404.ftl.html
	//=> named so the user can change it to path she likes
	private final String pathToViewNotFound;

	@Inject
	public NinjaImpl(
			Router router, 
			Injector injector,
			@Named("template404") String pathToViewNotFound) {

		this.router = router;
		this.injector = injector;
		this.pathToViewNotFound = pathToViewNotFound;

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
			processAnnotations(route);

			route.invoke(context);
		} else {
			// throw a 404
			context.status(HTTP_STATUS.notFound_404).template(pathToViewNotFound).html();

		}

	}

	public void processAnnotations(Route route) {

		Class controller = route.getController();
		String controllerMethod = route.getControllerMethod();

		try {
			for (Annotation annotation : controller.getMethod(controllerMethod,
					Context.class).getAnnotations()) {

				if (annotation.annotationType().equals(FilterWith.class)) {

					FilterWith filterWith = (FilterWith) annotation;

					Filter filter = (Filter) injector.getInstance(filterWith.value());

					filter.filter(null);

				}

			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
