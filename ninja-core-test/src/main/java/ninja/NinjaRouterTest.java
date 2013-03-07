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

import static org.junit.Assert.assertTrue;
import ninja.servlet.NinjaBootstap;
import ninja.utils.NinjaConstant;

import org.junit.After;

public class NinjaRouterTest {

    /** The router - initiated from a real server. Routes are verified with this router */
    private Router router;

    @After
    public void teardown() {
        // Important. We are using system wide modes. We remove it at the end of this test.
        System.clearProperty(NinjaConstant.MODE_KEY_NAME);

    }
    
    /**
     * Start the server and load the routes.
     * 
     * No special mode is set. By default the mode "dev" is then used by the server.
     */
    public void startServer() {
        // in this case servletContext can be null
        NinjaBootstap ninjaBootup = new NinjaBootstap();
        ninjaBootup.boot();

        router = ninjaBootup.getInjector().getInstance(Router.class);

    }

    /**
     * Start a server in prod mode and load routes.
     */
    public void startServerInProdMode() {

        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_PROD);

        startServer();

    }

    /**
     * Start a server in dev mode and load routes.
     */
    public void startServerInDevMode() {

        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_DEV);

        startServer();

    }

    /**
     * Start a server in test mode and load routes.
     */
    public void startServerInTestMode() {

        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_TEST);

        startServer();

    }

    public class WhenRouterAnswer {

        private String httpMethod;
        private String url;

        public WhenRouterAnswer(String httpMethod, String url) {
            this.httpMethod = httpMethod;
            this.url = url;

        }

        /**
         * Verifies that a routes is handled by that class and controller method.
         * 
         * @param controllerClass The controller class.
         * @param controllerMethod The controller method.
         */
        public void isHandledBy(Class controllerClass, String controllerMethod) {

            assertTrue(isHttpMethodAndUrlMatchedByControllerClassAndControllerMethod(router,
                    httpMethod, url, controllerClass, controllerMethod));

        }

        /**
         * Verifies that a route is NOT handled.
         * 
         * For instance you would call this route to make sure _test routes are not handled
         * in production mode.
         */
        public void isNotHandledByRoutesInRouter() {
            assertTrue(router.getRouteFor(httpMethod, url) == null);

        }

    }

    /**
     * The worker method used internally. Checks if that route is active.
     * 
     * @param router The router to ask.
     * @param httpMethod The method to use (GET, POST...)
     * @param url The url to use ("/_test" or "/profile/myId" ...)
     * @param controllerClass The controller class supposed to handle the url.
     * @param controllerMethodName The controller method supposed to handle the url
     * @return true if handled by given configuration / false if not.
     */
    private static boolean isHttpMethodAndUrlMatchedByControllerClassAndControllerMethod(
            Router router, String httpMethod, String url, Class controllerClass,
            String controllerMethodName) {

        if (!router.getRouteFor(httpMethod, url).getControllerClass().equals(controllerClass)) {

            return false;

        }

        if (!router.getRouteFor(httpMethod, url).getControllerMethod().getName().equals(
                controllerMethodName)) {

            return false;

        }

        return true;

    }

    /**
     * 
     * A simple DSL that allows to test routes.
     * 
     * Simplifies the task to test if routes in certain modes are active only
     * in their correct modes.
     * 
     * For instance you don't want to have /_test routes be available in
     * prod mode.
     * 
     * @param httpMethod "GET", "POST", "DELETE", "PUT" and so on...
     * @param url the url to test. For instance "/_test". Does not contain a server prefix.
     * @return An answer for chaning. "isHandledBy(...)
     */
    public WhenRouterAnswer aRequestLike(String httpMethod, String url) {

        return new WhenRouterAnswer(httpMethod, url);

    }



}
