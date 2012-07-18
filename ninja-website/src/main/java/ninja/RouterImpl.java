package ninja;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RouterImpl implements Router {

	private final List<RouteBuilderImpl> allRouteBuilders = new ArrayList<RouteBuilderImpl>();
    private final Injector injector;

    private List<Route> routes;

    @Inject
    public RouterImpl(Injector injector) {
        this.injector = injector;
    }

    @Override
	public Route getRouteFor(String httpMethod, String uri) {
        if (routes == null) {
            throw new IllegalStateException("Attempt to get route when routes not compiled");
        }

		for (Route route : routes) {
			if (route.matches(httpMethod, uri)) {
				return route;
			}
		}

		return null;

	}

    public void compileRoutes() {
        if (routes != null) {
            throw new IllegalStateException("Routes already compiled");
        }
        List<Route> routes = new ArrayList<Route>();
        for (RouteBuilderImpl routeBuilder : allRouteBuilders) {
            routes.add(routeBuilder.buildRoute(injector));
        }
        this.routes = ImmutableList.copyOf(routes);
    }

	@Override
	public RouteBuilder GET() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl().GET();
		allRouteBuilders.add(routeBuilder);

		return routeBuilder;
	}

	@Override
	public RouteBuilder POST() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().POST();
		allRouteBuilders.add(routeBuilder);

		return routeBuilder;
	}

	@Override
	public RouteBuilder PUT() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().PUT();
		allRouteBuilders.add(routeBuilder);

		return routeBuilder;
	}

	@Override
	public RouteBuilder DELETE() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().DELETE();
		allRouteBuilders.add(routeBuilder);

		return routeBuilder;
	}

	@Override
	public RouteBuilder OPTIONS() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().OPTION();
		allRouteBuilders.add(routeBuilder);

		return routeBuilder;
	}

}
