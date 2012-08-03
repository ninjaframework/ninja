package ninja;


import ninja.lifecycle.LifecycleService;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import ninja.utils.NinjaConstant;
import ninja.utils.ResultHandler;

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

    private final LifecycleService lifecycleService;
    private final Router router;
    private final ResultHandler resultHandler;

	@Inject
	public NinjaImpl(LifecycleService lifecycleService,
            Router router, ResultHandler resultHandler) {

		this.router = router;
		this.lifecycleService = lifecycleService;
        this.resultHandler = resultHandler;

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

		Route route = router.getRouteFor(httpMethod, context.getRequestPath());

		context.setRoute(route);

		if (route != null) {

			Result result = route.getFilterChain().next(context);

            resultHandler.handleResult(result, context);

		} else {
			// throw a 404 "not found" because we did not find the route

			Result result = Results.html(Result.SC_404_NOT_FOUND)
					.template(NinjaConstant.LOCATION_VIEW_FTL_HTML_NOT_FOUND);

			resultHandler.handleResult(result, context);
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
