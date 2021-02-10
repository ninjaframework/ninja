/**
 * Copyright (C) the original author or authors.
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaModeHelper;
import ninja.utils.NinjaPropertiesImpl;

import org.junit.After;

/**
 * NinjaRouterTest had a lot of sideffects. Mainly because Ninja is started in
 * prod mode, what also means that - well - stuff is loaded in prod mode.
 * Which means real services are started, real logging is started and so on.
 * 
 * Therefore it is a bad idea using NinjaRouterTest.
 * 
 * @author ra
 * @deprecated
 */
@Deprecated
public class NinjaRouterTest {

    /** The router - initiated from a real server. Routes are verified with this router */
    public Router router;
    
    Bootstrap ninjaBootup;
    
    /**
     * Start the server and load the routes.
     */
    public void startServer(NinjaMode ninjaMode) {
        
        if (ninjaMode == null) {
            
            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(
                    NinjaModeHelper.determineModeFromSystemPropertiesOrProdIfNotSet());
            
            ninjaBootup = new Bootstrap(
                    ninjaProperties);
        } else {
            // in this case servletContext can be null
            NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(ninjaMode);
            
            ninjaBootup = new Bootstrap(ninjaProperties);
        }

        ninjaBootup.boot();

        router = ninjaBootup.getInjector().getInstance(Router.class);

    }
    
    @After
    public void stopServer() {
        
        if (ninjaBootup != null) {
            ninjaBootup.shutdown();
        }
        
    }
    
    
    /**
     * Start the server and load the routes.
     * 
     * No special mode is set. By default the mode "dev" is then used by the server.
     */
    public void startServer() {
        startServer(null);
    }

    /**
     * Start a server in prod mode and load routes.
     */
    public void startServerInProdMode() {

        startServer(NinjaMode.prod);

    }

    /**
     * Start a server in dev mode and load routes.
     */
    public void startServerInDevMode() {

        startServer(NinjaMode.dev);

    }

    /**
     * Start a server in test mode and load routes.
     */
    public void startServerInTestMode() {
        
        startServer(NinjaMode.test);

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
         * Verifies that a routes is handled by that class and controller method.
         * 
         * @param controllerClass The controller class.
         * @param controllerMethod The controller method.
         */
        public void isNotHandledBy(Class controllerClass, String controllerMethod) {

            assertFalse(isHttpMethodAndUrlMatchedByControllerClassAndControllerMethod(router,
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
