package ninja;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class RouterImpl implements Router {

	private List<Route> allRoutes;

	private Provider<Route> routeProvider;

	// private final Injector injector;

	@Inject
	public RouterImpl(Provider<Route> routeProvider) {

		this.routeProvider = routeProvider;
		this.allRoutes = new ArrayList<Route>();

	}

	@Override
	public Route getRouteFor(String httpMethod, String uri) {

		for (Route route : allRoutes) {

			if (route.matches(httpMethod, uri)) {

				return route;
			}

		}

		return null;

	}

	@Override
	public Route getRouteFor(String httpMethod, String uri,
			Map<String, String> parameterMap) {

		throw new UnsupportedOperationException();

	}

	@Override
	public Route GET() {

		Route route = routeProvider.get().GET();
		checkIfRouteExistsAndAdd(route);

		return route;
	}

	@Override
	public Route POST() {
		Route route = routeProvider.get().POST();
		checkIfRouteExistsAndAdd(route);

		return route;
	}

	@Override
	public Route PUT() {
		Route route = routeProvider.get().PUT();
		checkIfRouteExistsAndAdd(route);

		return route;
	}

	@Override
	public Route DELETE() {
		Route route = routeProvider.get().DELETE();
		checkIfRouteExistsAndAdd(route);

		return route;
	}

	@Override
	public Route OPTIONS() {
		Route route = routeProvider.get().OPTION();
		checkIfRouteExistsAndAdd(route);

		return route;
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
	 * @param route
	 */
	private void checkIfRouteExistsAndAdd(Route route) {
		
		// Does not work that way...
		// Problem is binding and reflection resulting in an error...
		// Should be done... not sure how...

//		try {
//			
//			Class controller = route.getController().getClass();
//
//			Class partypes[] = new Class[1];
//			partypes[0] = Context.class;
//			Method meth = controller.getMethod(route.getControllerMethod(), partypes);
			allRoutes.add(route);
			

//			System.out.println("error => method does not exist: "
//					+ route.getController() + " - " + route.getControllerMethod());
//			
//			
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		


	}

}
