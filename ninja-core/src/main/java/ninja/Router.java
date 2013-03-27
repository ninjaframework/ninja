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
     * Compile all the routes that have been registered with the router. This
     * should be called once, during initialisation, before the application
     * starts serving requests.
     */
    public void compileRoutes();

    // /////////////////////////////////////////////////////////////////////////
    // convenience methods to use the route in a DSL like way
    // router.GET().route("/index").with(.....)
    // /////////////////////////////////////////////////////////////////////////
    public RouteBuilder GET();

    public RouteBuilder POST();

    public RouteBuilder PUT();

    public RouteBuilder DELETE();

    public RouteBuilder OPTIONS();

}