/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import com.google.common.base.Optional;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.Route.HttpMethod;
import ninja.utils.NinjaProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static ninja.Route.PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterImpl implements Router {

    private final NinjaProperties ninjaProperties;

    private final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

    private final List<RouteBuilderImpl> allRouteBuilders = new ArrayList<>();
    private final Injector injector;

    private List<Route> routes;

    // This regex works for both {myParam} AND {myParam: .*} (with regex)
    private final String VARIABLE_PART_PATTERN_WITH_PLACEHOLDER = "\\{(%s)(:\\s(.*))?\\}";

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

        return null;

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

    @Override
    public void register(final Class<?> controllerClass) {
        Map<String, Method> bindings = Maps.newHashMap();
        try {
            // 1. Validate specified priority.
            // 2. Make sure only one named method is there. Otherwise we cannot really
            //    know what to do with the parameters for named method reverse routing.
            // 3. Ensure method return type is ninja.Result
            for (Method method : controllerClass.getDeclaredMethods()) {
                RouteDef def = method.getAnnotation(RouteDef.class);
                if (def != null) {
                    if (def.order() < 1) {
                        String msg = String.format("Invalid priority %d specified for %s.%s()",
                                def.order(), controllerClass.getName(), method.getName());
                        throw new NoSuchMethodException(msg);
                    }
                    if (bindings.containsKey(method.getName())) {
                        String msg = String.format("Multiple methods named '%s' in %s",
                                method.getName(), controllerClass.getName());
                        throw new NoSuchMethodException(msg);
                    } else {
                        // make sure that the return type of that controller method
                        // is of type Result.
                        if (method.getReturnType().isAssignableFrom(Result.class)) {
                            bindings.put(method.getName(), method);
                        } else {
                            String msg = String.format("%s.%s() does not return ninja.Result",
                                    controllerClass.getName(), method.getName());
                            throw new NoSuchMethodException(msg);
                        }
                    }
                }
            }

            if (bindings.isEmpty()) {
                String msg = String.format("Can not find any @%s annotated methods in %s",
                        RouteDef.class.getSimpleName(), controllerClass.getName());
                throw new NoSuchMethodException(msg);
            }

            // group by http method & sort by order
            List<Method> methods = new ArrayList<Method>(bindings.values());
            Collections.sort(methods, new Comparator<Method>() {

                @Override
                public int compare(Method m1, Method m2) {
                    RouteDef def1 = m1.getAnnotation(RouteDef.class);
                    RouteDef def2 = m2.getAnnotation(RouteDef.class);
                    int methodComparison = def1.method().compareTo(def2.method());
                    if (methodComparison == 0) {
                        if (def1.order() < def2.order()) {
                            return -1;
                        } else if (def1.order() > def2.order()) {
                            return 1;
                        }
                        String msg = String.format("Ambiguous order for %s routes '%s.%s()' and '%s.%s()'",
                                def1.method(),
                                controllerClass.getName(), m1.getName(),
                                controllerClass.getName(), m2.getName());
                        throw new RuntimeException(msg);
                    }
                    return methodComparison;
                }
            });

            // register sorted routes
            for (Method method : methods) {
                RouteDef def = method.getAnnotation(RouteDef.class);
                for (String uri : def.uri()) {
                    METHOD(def.method()).route(uri).with(controllerClass, method.getName());
                }
            }

        } catch (SecurityException e) {
            logger.error(
                    "Error while checking for a valid annotated Controller", e);
        } catch (RuntimeException|NoSuchMethodException e) {
            logger.error("Error in route configuration!!!");
            logger.error(e.getMessage());
        }
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

    private List<Route> getRoutesForControllerClassAndHttpMethod(
            Class<?> controllerClass,
            String httpMethod) {

        List<Route> list = new ArrayList<>();
        for (Route route : routes) {

            if (route.getControllerClass() != null
                    && route.getControllerClass().equals(controllerClass)
                    && route.getHttpMethod().equals(httpMethod)) {

                list.add(route);

            }

        }

        return list;

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

    @Override
    public String getReverseGET(Class<?> controllerClass) {
        Optional<Map<String, Object>> parameterMap = Optional.absent();
        return getReverseMETHOD(HttpMethod.GET, controllerClass, parameterMap);
    }

    @Override
    public String getReverseGET(Class<?> controllerClass, Object... parameterMap) {
        return getReverseMETHOD(HttpMethod.GET, controllerClass, parameterMap);
    }

    @Override
    public String getReverseGET(Class<?> controllerClass, Map<String, Object> parameterMap) {
        return getReverseMETHOD(HttpMethod.GET, controllerClass, parameterMap);
    }

    @Override
    public String getReverseGET(Class<?> controllerClass, Optional<Map<String, Object>> parameterMap) {
        return getReverseMETHOD(HttpMethod.GET, controllerClass, parameterMap);
    }

    @Override
    public String getReversePUT(Class<?> controllerClass, Object... parameterMap) {
        return getReverseMETHOD(HttpMethod.PUT, controllerClass, parameterMap);
    }

    @Override
    public String getReversePUT(Class<?> controllerClass, Map<String, Object> parameterMap) {
        return getReverseMETHOD(HttpMethod.PUT, controllerClass, parameterMap);
    }

    @Override
    public String getReversePUT(Class<?> controllerClass, Optional<Map<String, Object>> parameterMap) {
        return getReverseMETHOD(HttpMethod.PUT, controllerClass, parameterMap);
    }

    @Override
    public String getReversePOST(Class<?> controllerClass, Object... parameterMap) {
        return getReverseMETHOD(HttpMethod.POST, controllerClass, parameterMap);
    }

    @Override
    public String getReversePOST(Class<?> controllerClass, Map<String, Object> parameterMap) {
        return getReverseMETHOD(HttpMethod.POST, controllerClass, parameterMap);
    }

    @Override
    public String getReversePOST(Class<?> controllerClass, Optional<Map<String, Object>> parameterMap) {
        return getReverseMETHOD(HttpMethod.POST, controllerClass, parameterMap);
    }

    @Override
    public String getReverseDELETE(Class<?> controllerClass, Object... parameterMap) {
        return getReverseMETHOD(HttpMethod.DELETE, controllerClass, parameterMap);
    }

    @Override
    public String getReverseDELETE(Class<?> controllerClass, Map<String, Object> parameterMap) {
        return getReverseMETHOD(HttpMethod.DELETE, controllerClass, parameterMap);
    }

    @Override
    public String getReverseDELETE(Class<?> controllerClass, Optional<Map<String, Object>> parameterMap) {
        return getReverseMETHOD(HttpMethod.DELETE, controllerClass, parameterMap);
    }

    @Override
    public String getReverseMETHOD(String httpMethod, Class<?> controllerClass) {
        Optional<Map<String, Object>> parameterMapOptional = Optional.absent();

        return getReverseMETHOD(httpMethod, controllerClass, parameterMapOptional);

    }

    @Override
    public String getReverseMETHOD(String httpMethod, Class<?> controllerClass, Object... parameterMap) {

        if (parameterMap.length % 2 != 0) {
            logger.error("Odd parameter count! Always provide key-value pairs for reverse route generation.");
        }

        Map<String, Object> map = new HashMap<>(parameterMap.length / 2);
        for (int i = 0; i < parameterMap.length; i += 2) {
            map.put((String) parameterMap[i], parameterMap[i + 1]);
        }

        return getReverseMETHOD(httpMethod, controllerClass, map);
    }

    @Override
    public String getReverseMETHOD(String httpMethod, Class<?> controllerClass, Map<String, Object> parameterMap) {
        Optional<Map<String, Object>> parameterMapOptional
        = Optional.fromNullable(parameterMap);

        return getReverseMETHOD(httpMethod, controllerClass, parameterMapOptional);

    }

    @Override
    public String getReverseMETHOD(String httpMethod, Class<?> controllerClass, Optional<Map<String, Object>> parameterMap) {
        return urlFor(httpMethod, controllerClass, parameterMap);
    }

    private String urlFor(String httpMethod, Class<?> controllerClass, Optional<Map<String, Object>> parameterMap) {
        if (routes == null) {
            throw new IllegalStateException(
                    "Attempt to get route when routes not compiled");
        }

        List<Route> routes = getRoutesForControllerClassAndHttpMethod(
                controllerClass,
                httpMethod);

        if (!routes.isEmpty()) {

            String url = null;
            int urlQueryParameterCount = Integer.MAX_VALUE;

            // identify the "best-fit" reverse route for the http method
            // by choosing the route with the fewest query parameters
            for (Route route : routes) {

                // The original url. Something like route/user/{id}/{email}/userDashboard/{name: .*}
                String urlWithReplacedPlaceholders
                = replaceVariablePartsOfUrlWithValuesProvidedByUser(
                        route.getUrl(),
                        parameterMap);

                // count the query parameters
                int queryParameterCount = 0;
                for (char c : urlWithReplacedPlaceholders.toCharArray()) {
                    if ('?' == c || '&' == c) {
                        queryParameterCount++;
                    }
                }

                if (queryParameterCount < urlQueryParameterCount) {
                    url = urlWithReplacedPlaceholders;
                    urlQueryParameterCount = queryParameterCount;
                }
            }

            // return the best-fit url
            if (url != null) {
                String finalUrl = addContextPathToUrlIfAvailable(
                        url,
                        ninjaProperties);

                return finalUrl;
            }

        }

        return null;
    }
}
