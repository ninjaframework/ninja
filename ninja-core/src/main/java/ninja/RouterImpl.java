/**
 * Copyright (C) 2012-2015 the original author or authors.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class RouterImpl implements Router {

    private final NinjaProperties ninjaProperties;

    private final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

    private final List<RouteBuilderImpl> allRouteBuilders = new ArrayList<>();
    private final Injector injector;

    private List<Route> routes;

    // This regex works for both {myParam} AND {myParam: .*} (with regex)
    private final String VARIABLE_PART_PATTERN_WITH_PLACEHOLDER = "\\{(%s)(:\\s([^}]*))?\\}"; 

    @Inject
    public RouterImpl(
            Injector injector,
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
    public String getReverseRoute(
            Class<?> controllerClass,
            String controllerMethodName,
            Optional<Map<String, Object>> parameterMap) {

        if (routes == null) {
            throw new IllegalStateException(
                    "Attempt to get route when routes not compiled");
        }

        Optional<Route> route
                = getRouteForControllerClassAndMethod(
                        controllerClass,
                        controllerMethodName);

        if (route.isPresent()) {

            // The original url. Something like route/user/{id}/{email}/userDashboard/{name: .*}
            String urlWithReplacedPlaceholders
                    = replaceVariablePartsOfUrlWithValuesProvidedByUser(
                            route.get().getUrl(),
                            parameterMap);

            String finalUrl = addContextPathToUrlIfAvailable(
                    urlWithReplacedPlaceholders,
                    ninjaProperties);

            return finalUrl;

        }
        else {
            logger.info(
                    "Could not find any reverse route for the method {} of the Controller class {}",
                    controllerMethodName, controllerClass.getSimpleName());
            return null;
        }
    }

    @Override
    public String getReverseRoute(Class<?> controllerClass,
            String controllerMethodName,
            Map<String, Object> parameterMap) {

        Optional<Map<String, Object>> parameterMapOptional
                = Optional.fromNullable(parameterMap);

        return getReverseRoute(
                controllerClass,
                controllerMethodName,
                parameterMapOptional);

    }

    @Override
    public void compileRoutes() {
        if (routes != null) {
            throw new IllegalStateException("Routes already compiled");
        }
        List<Route> routes = new ArrayList<>();
        for (RouteBuilderImpl routeBuilder : allRouteBuilders) {
            routes.add(routeBuilder.buildRoute(injector));
        }
        this.routes = ImmutableList.copyOf(routes);

        logRoutes();
    }

    @Override
    public List<Route> getRoutes() {
        if (routes == null) {
            throw new IllegalStateException("Routes have not been compiled");
        }
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

    private Optional<Route> getRouteForControllerClassAndMethod(
            Class<?> controllerClass,
            String controllerMethodName) {

        for (Route route : routes) {

            if (route.getControllerClass() != null
                    && route.getControllerClass().equals(controllerClass)
                    && route.getControllerMethod().getName().equals(controllerMethodName)) {

                return Optional.of(route);

            }

        }

        return Optional.absent();

    }

    private String replaceVariablePartsOfUrlWithValuesProvidedByUser(
            String routeUrlWithVariableParts,
            Optional<Map<String, Object>> parameterMap) {

        String urlWithReplacedPlaceholders = routeUrlWithVariableParts;

        if (parameterMap.isPresent()) {

            Map<String, Object> queryParameterMap = new HashMap<>(parameterMap.get().size());

            for (Entry<String, Object> parameterPair : parameterMap.get().entrySet()) {

                boolean foundAsPathParameter = false;

                StringBuffer stringBuffer = new StringBuffer();

                String buffer = String.format(
                        VARIABLE_PART_PATTERN_WITH_PLACEHOLDER,
                        parameterPair.getKey());

                Pattern PATTERN = Pattern.compile(buffer);
                Matcher matcher = PATTERN.matcher(urlWithReplacedPlaceholders);

                while (matcher.find()) {

                    String resultingRegexReplacement = parameterPair.getValue().toString();

                    matcher.appendReplacement(stringBuffer, resultingRegexReplacement);

                    foundAsPathParameter = true;
                }

                matcher.appendTail(stringBuffer);
                urlWithReplacedPlaceholders = stringBuffer.toString();

                if (!foundAsPathParameter) {

                    queryParameterMap.put(parameterPair.getKey(), parameterPair.getValue());

                }

            }

            // now prepare the query string for this url if we got some query params
            if (queryParameterMap.size() > 0) {

                StringBuffer queryParameterStringBuffer = new StringBuffer();

                // The uri is now replaced => we now have to add potential query parameters
                for (Iterator<Entry<String, Object>> iterator = queryParameterMap.entrySet().iterator();
                        iterator.hasNext();) {

                    Entry<String, Object> queryParameterEntry = iterator.next();
                    queryParameterStringBuffer.append(queryParameterEntry.getKey());
                    queryParameterStringBuffer.append("=");
                    queryParameterStringBuffer.append(queryParameterEntry.getValue());

                    if (iterator.hasNext()) {
                        queryParameterStringBuffer.append("&");
                    }

                }

                urlWithReplacedPlaceholders = urlWithReplacedPlaceholders
                        + "?"
                        + queryParameterStringBuffer.toString();

            }

        }

        return urlWithReplacedPlaceholders;
    }

    private String addContextPathToUrlIfAvailable(
            String routeWithoutContextPath,
            NinjaProperties ninjaProperties) {



        // contextPath can only be empty. never null.
        return ninjaProperties.getContextPath()
                    + routeWithoutContextPath;


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