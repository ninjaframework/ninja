/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import ninja.utils.MethodReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RouterImpl implements Router {
    static private final Logger logger = LoggerFactory.getLogger(RouterImpl.class);
    
    private final NinjaProperties ninjaProperties;
    private final List<RouteBuilderImpl> allRouteBuilders = new ArrayList<>();
    private final Injector injector;
    private List<Route> routes;
    // for fast reverse route lookups
    private Map<MethodReference,Route> reverseRoutes;
    // This regex works for both {myParam} AND {myParam: .*} (with regex)
    private final String VARIABLE_PART_PATTERN_WITH_PLACEHOLDER = "\\{(%s)(:\\s([^}]*))?\\}"; 

    @Inject
    public RouterImpl(Injector injector,
                      NinjaProperties ninjaProperties) {
        this.injector = injector;
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public Route getRouteFor(String httpMethod, String uri) {
        if (routes == null) {
            throw new IllegalStateException(
                    "Attempt to get route when routes not compiled");
        }

        for (Route route : routes) {
            if (route.matches(httpMethod, uri)) {
                return route;
            }
        }

        return null;

    }

    @Override
    public String getReverseRoute(
            Class<?> controllerClass,
            String controllerMethodName) {

        Optional<Map<String, Object>> parameterMap = Optional.absent();

        return getReverseRoute(controllerClass, controllerMethodName, parameterMap);

    }

    @Override
    public String getReverseRoute(Class<?> controllerClass,
            String controllerMethodName,
            Object... parameterMap) {

        if (parameterMap.length % 2 != 0) {
            logger.error("Always provide key (as String) value (as Object) pairs in parameterMap. That means providing e.g. 2, 4, 6... objects.");
            return null;

        }

        Map<String, Object> map = new HashMap<>(parameterMap.length / 2);
        for (int i = 0; i < parameterMap.length; i += 2) {
            map.put((String) parameterMap[i], parameterMap[i + 1]);
        }

        return getReverseRoute(controllerClass, controllerMethodName, Optional.of(map));

    }

    @Override
    public String getReverseRoute(Class<?> controllerClass,
            String controllerMethodName,
            Map<String, Object> parameterMap) {
        Optional<Map<String, Object>> parameterMapOptional
                = Optional.fromNullable(parameterMap);

        return getReverseRoute(
                controllerClass, controllerMethodName,
                parameterMapOptional);
    }
    
    @Override
    public String getReverseRoute(
            Class<?> controllerClass,
            String controllerMethodName,
            Optional<Map<String, Object>> parameterMap) {

        try {
            ReverseRouter.Builder reverseRouteBuilder
                = new ReverseRouter(ninjaProperties, this)
                    .with(controllerClass, controllerMethodName);
            
            if (parameterMap.isPresent()) {
                // pathOrQueryParams are not escaped with the deprecated method of creating
                // reverse routes.  use ReverseRouter!
                parameterMap.get().forEach((name, value) -> {
                    // path or query param?
                    if (reverseRouteBuilder.getRoute().getParameters().containsKey(name)) {
                        reverseRouteBuilder.rawPathParam(name, value);
                    } else {
                        reverseRouteBuilder.rawQueryParam(name, value);
                    }
                });
            }
            
            return reverseRouteBuilder.build();
        } catch (IllegalArgumentException e) {
            logger.error("Unable to cleanly build reverse route", e);
            return null;
        }
    }
    
    @Override
    public String getReverseRoute(MethodReference controllerMethodRef) {
        return getReverseRoute(
            controllerMethodRef.getDeclaringClass(),
            controllerMethodRef.getMethodName());
    }
    
    @Override
    public String getReverseRoute(MethodReference controllerMethodRef, Map<String, Object> parameterMap) {
        return getReverseRoute(
            controllerMethodRef.getDeclaringClass(),
            controllerMethodRef.getMethodName(),
            parameterMap);
    }
    
    @Override
    public String getReverseRoute(MethodReference controllerMethodRef, Object ... parameterMap) {
        return getReverseRoute(
            controllerMethodRef.getDeclaringClass(),
            controllerMethodRef.getMethodName(),
            parameterMap);
    }
    
    @Override
    public String getReverseRoute(MethodReference controllerMethodRef, Optional<Map<String, Object>> parameterMap) {
        return getReverseRoute(
            controllerMethodRef.getDeclaringClass(),
            controllerMethodRef.getMethodName(),
            parameterMap);
    }

    @Override
    public void compileRoutes() {
        if (routes != null) {
            throw new IllegalStateException("Routes already compiled");
        }
        List<Route> routesLocal = new ArrayList<>();
        
        for (RouteBuilderImpl routeBuilder : allRouteBuilders) {
            routesLocal.add(routeBuilder.buildRoute(injector));
        }
        
        this.routes = ImmutableList.copyOf(routesLocal);
        
        // compile reverse routes for O(1) lookups
        this.reverseRoutes = new HashMap<>(this.routes.size());
        
        for (Route route : this.routes) {
            // its possible this route is a Result instead of a controller method
            if (route.getControllerClass() != null) {
                MethodReference controllerMethodRef
                    = new MethodReference(
                        route.getControllerClass(),
                        route.getControllerMethod().getName());
                
                if (this.reverseRoutes.containsKey(controllerMethodRef)) {
                    // the first one wins with reverse routing so we ignore this route
                } else {
                    this.reverseRoutes.put(controllerMethodRef, route);
                }
            }
        }

        logRoutes();
    }

    @Override
    public List<Route> getRoutes() {
        verifyRoutesCompiled();
        return routes;
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
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().OPTIONS();
        allRouteBuilders.add(routeBuilder);

        return routeBuilder;
    }

    @Override
    public RouteBuilder HEAD() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().HEAD();
        allRouteBuilders.add(routeBuilder);

        return routeBuilder;
    }

    @Override
    public RouteBuilder METHOD(String method) {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl().METHOD(method);
        allRouteBuilders.add(routeBuilder);

        return routeBuilder;
    }
    
    private void verifyRoutesCompiled() {
        if (routes == null) {
            throw new IllegalStateException(
                "Routes not compiled!");
        }
    }

    @Override
    public Optional<Route> getRouteForControllerClassAndMethod(
            Class<?> controllerClass,
            String controllerMethodName) {

        verifyRoutesCompiled();

        MethodReference reverseRouteKey
            = new MethodReference(controllerClass, controllerMethodName);
        
        Route route = this.reverseRoutes.get(reverseRouteKey);
        
        return Optional.fromNullable(route);
    }
    
    private void logRoutes() {
        // determine the width of the columns
        int maxMethodLen = 0;
        int maxPathLen = 0;
        int maxControllerLen = 0;

        for (Route route : getRoutes()) {

            maxMethodLen = Math.max(maxMethodLen, route.getHttpMethod().length());
            maxPathLen = Math.max(maxPathLen, route.getUri().length());

            if (route.getControllerClass() != null) {

                int controllerLen = route.getControllerClass().getName().length()
                    + route.getControllerMethod().getName().length();
                maxControllerLen = Math.max(maxControllerLen, controllerLen);

            }

        }

        // log the routing table
        int borderLen = 10 + maxMethodLen + maxPathLen + maxControllerLen;
        String border = Strings.padEnd("", borderLen, '-');

        logger.info(border);
        logger.info("Registered routes");
        logger.info(border);

        for (Route route : getRoutes()) {

            if (route.getControllerClass() != null) {

                logger.info("{} {}  =>  {}.{}()",
                    Strings.padEnd(route.getHttpMethod(), maxMethodLen, ' '),
                    Strings.padEnd(route.getUri(), maxPathLen, ' '),
                    route.getControllerClass().getName(),
                    route.getControllerMethod().getName());

            } else {

              logger.info("{} {}", route.getHttpMethod(), route.getUri());

            }

        }

    }
}