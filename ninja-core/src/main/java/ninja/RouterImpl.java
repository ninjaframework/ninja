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

		allRoutes = new ArrayList<Route>();
	}

	public Route route(String url) {

		Route route = routeProvider.get();
		route.setUrl(url);

		allRoutes.add(route);

		return route;
	}

	public Route getRouteFor(String uri) {

		for (Route route : allRoutes) {

			if (route.matches(uri)) {

				return route;
			}

		}

		return null;

	}
	
	public Route getRouteFor(String uri, Map<String, String> parameterMap) {

		throw new UnsupportedOperationException();

	}



}
