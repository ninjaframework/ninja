package ninja;

import java.io.IOException;

import ninja.lifecycle.LifecycleService;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;
import ninja.utils.ResponseStreams;

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
	private final LifecycleService lifecycleService;

	// something like views/notFound404.ftl.html
	// => named so the user can change it to path she likes
	private final String pathToViewNotFound;

	private final TemplateEngineManager templateEngineManager;

	@Inject
	public NinjaImpl(Injector injector, LifecycleService lifecycleService,
			Router router, TemplateEngineManager templateEngineManager,
			@Named("template404") String pathToViewNotFound) {

		this.router = router;
		this.injector = injector;
		this.lifecycleService = lifecycleService;
		this.templateEngineManager = templateEngineManager;
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

		Route route = router.getRouteFor(httpMethod, context
				.getHttpServletRequest().getRequestURI());

		context.setRoute(route);

		if (route != null) {

			Result result = route.getFilterChain().next(context);

			invokeResult(result, context);

		} else {
			// throw a 404 "not found" because we did not find the route

			Result result = Results.html(Result.SC_404_NOT_FOUND)
					.template(pathToViewNotFound).html();

			invokeResult(result, context);
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

	private void invokeResult(Result result, Context context) {
		// if the object is a renderable it should do everything itself...:
		// make sure to call context.finalizeHeaders(result) with the results
		// you want to set...
		Object object = result.getRenderable();
		if (object instanceof Renderable) {

			Renderable renderable = (Renderable) object;
			renderable.render(context, result);

		} else {

			// if content type is not yet set in result we copy it over from the
			// request
			// otherwise we are using TEXT/HTML as fallback...
			if (result.getContentType() == null) {
				
				if (context.getRequestContentType() != null) {

					result.contentType(context.getRequestContentType());
					
				} else {

					result.contentType(Result.TEXT_HTML);
				}

			}

			// try to get a suitable rendering engine...
			TemplateEngine templateEngine = templateEngineManager
					.getTemplateEngineForContentType(result.getContentType());

			if (templateEngine != null) {

				templateEngine.invoke(context, result);

			} else {

				if (result.getRenderable() instanceof String) {

					// Simply write it out
					try {

						ResponseStreams responseStreams = context
								.finalizeHeaders(result);

						responseStreams.getWriter().write(
								(String) result.getRenderable());

					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else if (result.getRenderable() instanceof byte[]) {
					// Simply write it out
					try {

						ResponseStreams responseStreams = context
								.finalizeHeaders(result);

						responseStreams.getOutputStream().write(
								(byte[]) result.getRenderable());

					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				} else {

					throw new IllegalArgumentException(
							"No template engine found for content type "
									+ result.getContentType());
				}
			}


		}

	}

}
