/**
 * Copyright (C) 2013 the original author or authors.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.utils.NinjaProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterImpl implements Router {
    
    @Inject
    NinjaProperties ninjaProperties;

    private final Logger logger = LoggerFactory.getLogger(RouterImpl.class);

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
    
    public String getReverseRoute(Class<?> controllerClass,
                                  String controllerMethodName) {
        
        Map<String, Object> map = Maps.newHashMap();
        return getReverseRoute(controllerClass, controllerMethodName, map);
        
        
    }
    
    public String getReverseRoute(Class<?> controllerClass,
                             String controllerMethodName,
                             Object ... parameterMap) {
        
        if (parameterMap.length % 2 != 0) {
            logger.error("Always provide key (as String) value (as Object) pairs in parameterMap. That means providing e.g. 2, 4, 6... objects.");
            return null;
            
        }
        
        Map<String, Object> map = Maps.newHashMap();
        for (int i = 0;  i < parameterMap.length; i+=2) {
            map.put((String) parameterMap[i], parameterMap[i+1]);
        }

        
        
        return getReverseRoute(controllerClass, controllerMethodName, map);

    }

    public String getReverseRoute(Class<?> controllerClass,
                                 String controllerMethodName,
                                 Map<String, Object> parameterMap) {
        if (routes == null) {
            throw new IllegalStateException(
                    "Attempt to get route when routes not compiled");
        }

        for (Route route : routes) {

            if (route.getControllerClass() != null
                    && route.getControllerClass().equals(controllerClass)
                    && route.getControllerMethod().getName().equals(controllerMethodName)) {
                
                // The original url. Something like route/user/{id}/{email}/userDashboard
                String urlWithReplacedPlaceholders = route.getUrl();
                
                Map<String, Object> queryParameterMap = Maps.newHashMap();
                
                for (Entry<String, Object> parameterPair : parameterMap.entrySet()) {
                    
                    // The original regex. For the example above this results in {id}
                    String originalRegex = String.format("{%s}", parameterPair.getKey());
                    String originalRegexEscaped = String.format("\\{%s\\}", parameterPair.getKey());
                    
                    // The value that will be added into the regex => myId for instance...
                    String resultingRegexReplacement = parameterPair.getValue().toString();
                    
                    // If regex is in the url as placeholder we replace the placeholder
                    if (urlWithReplacedPlaceholders.contains(originalRegex)) {
                        
                        urlWithReplacedPlaceholders = urlWithReplacedPlaceholders.replaceAll(
                                originalRegexEscaped, 
                                resultingRegexReplacement);
                    
                    // If the parameter is not there as placeholder we add it as queryParameter
                    } else {
                    
                        queryParameterMap.put(parameterPair.getKey(), parameterPair.getValue());
                        
                    }
   
                }
                
                
                // now prepare the query string for this url if we got some query params
                if (queryParameterMap.entrySet().size() > 0) {
                    
                    StringBuffer queryParameterStringBuffer = new StringBuffer();
                    
                    // The uri is now replaced => we now have to add potential query parameters
                    for (Iterator<Entry<String, Object>> iterator = queryParameterMap.entrySet().iterator(); 
                            iterator.hasNext(); ) {
                        
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
                
                // Respect the context path
                String contextPath = ninjaProperties.getContextPath().orNull();
                if (contextPath != null) {
                    return contextPath + urlWithReplacedPlaceholders;
                }
                
                
                return urlWithReplacedPlaceholders;
                
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

}
