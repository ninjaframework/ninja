/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
