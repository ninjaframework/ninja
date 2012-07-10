package ninja;

import ninja.Context.HTTP_STATUS;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import ninja.lifecycle.LifecycleService;

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
    private final LifecycleService lifecycleService;

	// something like views/notFound404.ftl.html
	// => named so the user can change it to path she likes
	private final String pathToViewNotFound;

	@Inject
	public NinjaImpl(Router router, Injector injector, LifecycleService lifecycleService,
			@Named("template404") String pathToViewNotFound) {

		this.router = router;
		this.injector = injector;
        this.lifecycleService = lifecycleService;
		this.pathToViewNotFound = pathToViewNotFound;

		// This system out println is intended.
		System.out.println(NINJA_LOGO);
	}

	/**
	 * I do all the main work.
	 * 
	 * @param context
	 *            context
	 */
	public void invoke(ContextImpl context) {
		
		String httpMethod = context.getHttpServletRequest().getMethod();

		Route route = router.getRouteFor(httpMethod, context.getHttpServletRequest()
				.getRequestURI());

        context.setRoute(route);

		if (route != null) {
            route.getFilterChain().next(context);
		} else {
			// throw a 404 "not found" because we did not find the route
			context.status(HTTP_STATUS.notFound404)
					.template(pathToViewNotFound).renderHtml();
		}
	}

    @Override
    public void start() {
        lifecycleService.start();
    }

    @Override
    public void shutdown() {
        lifecycleService.stop();
    }
}
