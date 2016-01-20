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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

@RunWith(MockitoJUnitRunner.class)
public class RouteBuilderImplTest {

    @Mock
    Injector injector;

    @Test
    public void testBasicGETRoute() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("GET", "/index"));

    }

    @Test
    public void testBasicPOSTRoute() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.POST().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("POST", "/index"));

    }

    @Test
    public void testBasicPUTRoute() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.PUT().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("PUT", "/index"));

    }

    @Test
    public void testBasicRoutes() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.OPTIONS().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("OPTIONS", "/index"));

    }
    
    @Test
    public void testBasisHEAD() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.HEAD().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("HEAD", "/index"));

    }
    
    @Test
    public void testBasicAnyHttpMethod() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.METHOD("PROPFIND").route("/index");

        assertTrue(buildRoute(routeBuilder).matches("PROPFIND", "/index"));

    }

    @Test
    public void testBasicRoutesWithRegex() {

        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/.*");

        Route route = buildRoute(routeBuilder);
        // make sure the route catches everything:
        assertTrue(route.matches("GET", "/index"));
        assertTrue(route.matches("GET", "/stylesheet.css"));
        assertTrue(route.matches("GET", "/public/stylesheet.css"));
        assertTrue(route.matches("GET", "/public/bootstrap.js"));

    }

    @Test
    public void testBasicPlaceholersAndParameters() {

        // /////////////////////////////////////////////////////////////////////
        // One parameter:
        // /////////////////////////////////////////////////////////////////////
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/{name}/dashboard");

        Route route = buildRoute(routeBuilder);
        assertFalse(route.matches("GET", "/dashboard"));

        assertTrue(route.matches("GET", "/John/dashboard"));

        Map<String, String> map = route
                .getPathParametersEncoded("/John/dashboard");

        assertEquals(1, map.entrySet().size());
        assertEquals("John", map.get("name"));

        // /////////////////////////////////////////////////////////////////////
        // More parameters
        // /////////////////////////////////////////////////////////////////////
        routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/{name}/{id}/dashboard");
        route = buildRoute(routeBuilder);

        assertFalse(route.matches("GET", "/dashboard"));

        assertTrue(route.matches("GET", "/John/20/dashboard"));

        map = route.getPathParametersEncoded("/John/20/dashboard");

        assertEquals(2, map.entrySet().size());
        assertEquals("John", map.get("name"));
        assertEquals("20", map.get("id"));

    }

    @Test
    public void testBasicPlaceholersParametersAndRegex() {

        // test that parameter parsing works in conjunction with
        // regex expressions...
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/John/{id}/.*");
        Route route = buildRoute(routeBuilder);
        assertTrue(route.matches("GET", "/John/20/dashboard"));
        Map<String, String> map = route
                .getPathParametersEncoded("/John/20/dashboard");
        assertEquals(1, map.entrySet().size());
        assertEquals("20", map.get("id"));

        assertTrue(route.matches("GET", "/John/20/admin"));
        map = route.getPathParametersEncoded("/John/20/admin");
        assertEquals(1, map.entrySet().size());
        assertEquals("20", map.get("id"));

        assertTrue(route.matches("GET", "/John/20/mock"));
        map = route.getPathParametersEncoded("/John/20/mock");
        assertEquals(1, map.entrySet().size());
        assertEquals("20", map.get("id"));

    }

        @Test
    public void testBasicPlaceholersParametersAndRegexInsideVariableParts() {

        // test that parameter parsing works in conjunction with
        // regex expressions...
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/assets/{file: .*}");
        Route route = buildRoute(routeBuilder);

        String pathUnderTest = "/assets/css/app.css";
        assertTrue(route.matches("GET", pathUnderTest));
        Map<String, String> map = route.getPathParametersEncoded(pathUnderTest);
        assertEquals(1, map.entrySet().size());
        assertEquals("css/app.css", map.get("file"));

        pathUnderTest = "/assets/javascripts/main.js";
        assertTrue(route.matches("GET", pathUnderTest));
        map = route.getPathParametersEncoded(pathUnderTest);
        assertEquals(1, map.entrySet().size());
        assertEquals("javascripts/main.js", map.get("file"));

        pathUnderTest = "/assets/robots.txt";
        assertTrue(route.matches("GET", pathUnderTest));
        map = route.getPathParametersEncoded(pathUnderTest);
        assertEquals(1, map.entrySet().size());
        assertEquals("robots.txt", map.get("file"));

        // multiple parameter parsing with regex expressions
        routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/{name: .+}/photos/{id: [0-9]+}");
        route = buildRoute(routeBuilder);

        pathUnderTest = "/John/photos/2201";
        assertTrue(route.matches("GET", pathUnderTest));
        assertFalse(route.matches("GET", "John/photos/first"));
        map = route.getPathParametersEncoded(pathUnderTest);
        assertEquals(2, map.size());
        assertEquals("John", map.get("name"));
        assertEquals("2201", map.get("id"));
    }

    @Test
    public void testParametersDontCrossSlashes() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/{id2}/{id3}/morestuff/at/the/end");
        Route route = buildRoute(routeBuilder);
        // this must match
        assertTrue(route
                .matches("GET", "/blah/id/id2/id3/morestuff/at/the/end"));
        // this should not match as the last "end" is missing
        assertFalse(route.matches("GET", "/blah/id/id2/id3/morestuff/at/the"));
    }

    @Test
    public void testPointsInRegexDontCrashRegexInTheMiddleOfTheRoute() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/myname");
        Route route = buildRoute(routeBuilder);

        // the "." in the route should not make any trouble:
        String routeFromServer = "/blah/my.id/myname";

        assertTrue(route.matches("GET", routeFromServer));
        assertEquals(1, route.getPathParametersEncoded(routeFromServer)
                .entrySet().size());
        assertEquals("my.id", route.getPathParametersEncoded(routeFromServer)
                .get("id"));

        // and another slightly different route
        routeFromServer = "/blah/my.id/myname/should_not_match";
        assertFalse(route.matches("GET", routeFromServer));
        assertEquals(0, route.getPathParametersEncoded(routeFromServer)
                .entrySet().size());
    }

    @Test
    public void testPointsInRegexDontCrashRegexAtEnd() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}");
        Route route = buildRoute(routeBuilder);
        // the "." in the route should not make any trouble:
        // even if it's the last part of the route
        String routeFromServer = "/blah/my.id";
        assertTrue(route.matches("GET", "/blah/my.id"));
        assertEquals(1, route.getPathParametersEncoded(routeFromServer)
                .entrySet().size());
        assertEquals("my.id", route.getPathParametersEncoded(routeFromServer)
                .get("id"));
    }

    @Test
    public void testRegexInRouteWorksWithEscapes() {
        // Test escaped constructs in regex
        // regex with escaped construct in a route
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/customers/\\d+");
        Route route = buildRoute(routeBuilder);
        assertTrue(route.matches("GET", "/customers/1234"));
        assertFalse(route.matches("GET", "/customers/12ab"));

        // regex with escaped construct in a route with variable parts
        routeBuilder.GET().route("/customers/{id: \\d+}");
        route = buildRoute(routeBuilder);
        assertTrue(route.matches("GET", "/customers/1234"));
        assertFalse(route.matches("GET", "/customers/12x"));

        Map<String, String> map = route.getPathParametersEncoded("/customers/1234");
        assertEquals(1, map.size());
        assertEquals("1234", map.get("id"));
    }

    @Test
    public void testRegexInRouteWorksWithoutSlashAtTheEnd() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/.*");
        Route route = buildRoute(routeBuilder);

        // the "." in the real route should work without any problems:
        String routeFromServer = "/blah/my.id/and/some/more/stuff";

        assertTrue(route.matches("GET", routeFromServer));
        assertEquals(1, route.getPathParametersEncoded(routeFromServer)
                .entrySet().size());
        assertEquals("my.id", route.getPathParametersEncoded(routeFromServer)
                .get("id"));

        // another slightly different route.
        routeFromServer = "/blah/my.id/";
        assertTrue(route.matches("GET", "/blah/my.id/"));
        assertEquals(1, route.getPathParametersEncoded(routeFromServer)
                .entrySet().size());
        assertEquals("my.id", route.getPathParametersEncoded(routeFromServer)
                .get("id"));

        assertFalse(route.matches("GET", "/blah/my.id"));

    }

    @Test
    public void testRouteWithUrlEncodedSlashGetsChoppedCorrectly() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/.*");
        Route route = buildRoute(routeBuilder);

        // Just a simple test to make sure everything works on a not encoded
        // uri:
        // decoded this would be /blah/my/id/and/some/more/stuff
        String routeFromServer = "/blah/my%2fid/and/some/more/stuff";

        assertTrue(route.matches("GET", routeFromServer));
        assertEquals(1, route.getPathParametersEncoded(routeFromServer)
                .entrySet().size());
        assertEquals("my%2fid", route.getPathParametersEncoded(routeFromServer)
                .get("id"));

    }

    @Test
    public void testRouteWithResult() {
        String template = "/directly_result/stuff";
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/directly_result/route").with(Results.html().template(template));

        Route route = routeBuilder.buildRoute(injector);
        assertTrue(route.matches("GET", "/directly_result/route"));

        Result result = route.getFilterChain().next(null);
        assertEquals(result.getTemplate(), template);
    }

    @Test
    public void testFailedControllerRegistration() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/failure").with(MockController.class, "DoesNotExist");

        try {	
            Route route = routeBuilder.buildRoute(injector);
            assertTrue(route == null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }

    private Route buildRoute(RouteBuilderImpl builder) {
        builder.with(MockController.class, "execute");
        return builder.buildRoute(injector);
    }

    public static class MockController {
        public Result execute() {
            return null;
        }
    }

}
