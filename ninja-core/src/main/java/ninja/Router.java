/**
 * Copyright (C) 2012- the original author or authors.
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ninja.utils.MethodReference;

public interface Router {

    /**
     * Get the route for the given method and URI
     * 
     * @param httpMethod
     *            The method
     * @param uri
     *            The URI
     * @return The route
     */
    public Route getRouteFor(String httpMethod, String uri);
    
    /**
     * Retrieves the reverse route for this controllerClass and method.
     * Does not work with routes that contain placeholders.
     * Use {@link Router#getReverseRoute(Class, String, Map)} in that case
     * 
     * @param clazz The controllerClass e.g. ApplicationController.class
     * @param methodName the methodName of the class e.g. "index"
     * @return The final url (without server, and without any prefixes)
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(Class<?> clazz, String methodName);
    
    /**
     * Retrieves the reverse route for this controllerClass and method.
     * The map contains pairs of url parameters.
     * 
     * Eg. a raw route like "/person/{id} will become /person/1 when
     * the map contains a pair like "id, "1".
     * 
     * @param clazz The controllerClass e.g. ApplicationController.class
     * @param methodName the methodName of the class e.g. "index"
     * @param map The map containing pairs with replacements for placeholders. 
     *          It's a String Object map so that it matches the map used to render a page.
     *          to get the value "toString()" is called on the object. Make sure that works for your object
     *          or simply use a String. If the raw uri does not contain the placeholders
     *          they will be added as query parameters ?key=value&key2=value2 and so on
     * @return The final url (without server, and without any prefixes)
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(Class<?> clazz, String methodName, Map<String, Object> parameterMap);

    /**
     * Retrieves the reverse route for this controllerClass and method.
     * The map contains pairs of url parameters.
     * 
     * Eg. a raw route like "/person/{id} will become /person/1 when
     * the map contains a pair like "id, "1".
     * 
     * @param clazz The controllerClass e.g. ApplicationController.class
     * @param methodName the methodName of the class e.g. "index"
     * @param parameterMap The map containing pairs with replacements for placeholders. 
     *          Always supply key and value pairs. Key as strings, Values as objects.
     *          To get the value "toString()" is called on the object. Make sure that works for your object
     *          or simply use a String. If the raw uri does not contain the placeholders
     *          they will be added as query parameters ?key=value&key2=value2 and so on
     * @return The final url (without server, and without any prefixes)
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(Class<?> clazz, String methodName, Object ... parameterMap);
    
    
    /**
     * Retrieves the reverse route for this controllerClass and method.
     * The map contains pairs of url parameters.
     * 
     * Eg. a raw route like "/person/{id} will become /person/1 when
     * the map contains a pair like "id, "1".
     * 
     * @param clazz The controllerClass e.g. ApplicationController.class
     * @param methodName the methodName of the class e.g. "index"
     * @param parameterMap An optinal map containing pairs with replacements for placeholders. 
     *          Always supply key and value pairs. Key as strings, Values as objects.
     *          To get the value "toString()" is called on the object. Make sure that works for your object
     *          or simply use a String. If the raw uri does not contain the placeholders
     *          they will be added as query parameters ?key=value&key2=value2 and so on
     * @return The final url (without server, and without any prefixes)
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(Class<?> controllerClass,
                                 String controllerMethodName,
                                 Optional<Map<String, Object>> parameterMap);
        
    /**
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(MethodReference controllerMethodRef);
    
    /**
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(MethodReference controllerMethodRef, Map<String, Object> parameterMap);
    
    /**
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(MethodReference controllerMethodRef, Object ... parameterMap);
    
    /**
     * @deprecated Reverse routing in the Router is not validated and does not
     *      URL-escape path or query parameters. Use <code>ninja.ReverseRouter</code>
     *      to build your reverse routes.
     */
    @Deprecated
    public String getReverseRoute(MethodReference controllerMethodRef, Optional<Map<String, Object>> parameterMap);
    
    /**
     * Compile all the routes that have been registered with the router. This
     * should be called once, during initialization, before the application
     * starts serving requests.
     */
    public void compileRoutes();

    /**
     * Returns the list of compiled routes.
     */
    public List<Route> getRoutes();
    
    public Optional<Route> getRouteForControllerClassAndMethod(
        Class<?> controllerClass, String controllerMethodName);

    // /////////////////////////////////////////////////////////////////////////
    // convenience methods to use the route in a DSL like way
    // router.GET().route("/index").with(.....)
    // /////////////////////////////////////////////////////////////////////////
    public RouteBuilder GET();

    public RouteBuilder POST();

    public RouteBuilder PUT();

    public RouteBuilder DELETE();

    public RouteBuilder OPTIONS();
    
    public RouteBuilder HEAD();
    
    public RouteBuilder WS();
    
    /**
     * To match any http method. E.g. METHOD("PROPFIND") would route PROPFIND methods.
     * @param method The http method like "GET" or "PROPFIND"
     * @return the routeBuilder for chaining.
     */
    public RouteBuilder METHOD(String method);
}