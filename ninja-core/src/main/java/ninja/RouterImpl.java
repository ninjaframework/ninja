package ninja;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class RouterImpl implements Router {

	private List<Route> allRoutes;

	private Provider<Route> routeProvider;

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
	public Route getRouteFor(String httpMethod, String uri, Map<String, String> parameterMap) {

		throw new UnsupportedOperationException();

	}

	@Override
	public Route GET() {

		Route route = routeProvider.get().GET();
		allRoutes.add(route);

		return route;
	}

	@Override
	public Route POST() {
		Route route = routeProvider.get().POST();
		allRoutes.add(route);

		return route;
	}

	@Override
	public Route PUT() {
		Route route = routeProvider.get().PUT();
		allRoutes.add(route);

		return route;
	}

	@Override
	public Route DELETE() {
		Route route = routeProvider.get().DELETE();
		allRoutes.add(route);

		return route;
	}

	@Override
	public Route OPTIONS() {
		Route route = routeProvider.get().OPTION();
		allRoutes.add(route);

		return route;
	}

}
