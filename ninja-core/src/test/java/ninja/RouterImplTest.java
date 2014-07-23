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
import com.google.inject.Injector;
import com.google.inject.Provider;
import ninja.params.PathParam;
import ninja.Route.HttpMethod;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * => Most tests are done via class RoutesTest in project
 * ninja-servlet-integration-test.
 */
@RunWith(MockitoJUnitRunner.class)
public class RouterImplTest {

    Router router;

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Injector injector;

    @Mock
    Provider<TestController> testControllerProvider;

    @Before
    public void before() {

        when(testControllerProvider.get()).thenReturn(new TestController());
        when(injector.getProvider(TestController.class)).thenReturn(testControllerProvider);
        router = new RouterImpl(injector, ninjaProperties);

        // add route:
        router.GET().route("/testroute").with(TestController.class, "index");
        router.GET().route("/user/{email}/{id: .*}").with(TestController.class, "user");

        // add routes mapped by http method annotation:
        router.register(AnnotatedController.class);

        router.compileRoutes();
    }

    @Test
    public void testGetReverseRouteWithNoContextPathWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "index");

        assertThat(route, CoreMatchers.equalTo("/testroute"));

    }

    @Test
    public void testGetReverseRouteContextPathWorks() {

        String contextPath = "/myappcontext";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "index");

        assertThat(route, equalTo("/myappcontext/testroute"));

    }

    @Test
    public void testGetReverseRouteWithRegexWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(
                TestController.class,
                "user",
                "email",
                "me@me.com",
                "id",
                10000);

        assertThat(route, equalTo("/user/me@me.com/10000"));

    }

    @Test
    public void testGetReverseRouteWithRegexAndQueryParametersWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(
                TestController.class,
                "user",
                "email",
                "me@me.com",
                "id",
                10000,
                "q",
                "froglegs");

        assertThat(route, equalTo("/user/me@me.com/10000?q=froglegs"));

    }

    @Test
    public void testGetReverseAnnotatedRouteWithNoContextPathWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseGET(AnnotatedController.class);

        assertThat(route, CoreMatchers.equalTo("/testroute"));

    }

    @Test
    public void testGetReverseAnnotatedRouteContextPathWorks() {

        String contextPath = "/myappcontext";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseGET(AnnotatedController.class);

        assertThat(route, equalTo("/myappcontext/testroute"));

    }

    @Test
    public void testGetReverseAnnotatedRouteWithRegexWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReversePUT(AnnotatedController.class,
                "email", "me@me.com", "id", 10000);

        assertThat(route, equalTo("/user/me@me.com/10000"));

    }

    @Test
    public void testGetReverseAnnotatedRouteWithRegexAndQueryParametersWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReversePUT(AnnotatedController.class,
                "email", "me@me.com", "id", 10000, "q", "froglegs");

        assertThat(route, equalTo("/user/me@me.com/10000?q=froglegs"));

    }

    @Test
    public void testBasicGETAnnotatedRoute() {

        Router router = buildAnnotatedRouter(MockAnnotatedController.class);
        assertEquals("/get", router.getReverseGET(MockAnnotatedController.class));

    }

    @Test
    public void testBasicPOSTAnnotatedRoute() {

        Router router = buildAnnotatedRouter(MockAnnotatedController.class);
        assertEquals("/post", router.getReversePOST(MockAnnotatedController.class));

    }

    @Test
    public void testBasicPUTAnnotatedRoute() {

        Router router = buildAnnotatedRouter(MockAnnotatedController.class);
        assertEquals("/put", router.getReversePUT(MockAnnotatedController.class));

    }

    @Test
    public void testBasicOPTIONSAnnotatedRoute() {

        Router router = buildAnnotatedRouter(MockAnnotatedController.class);
        assertEquals("/options", router.getReverseMETHOD(HttpMethod.OPTIONS, MockAnnotatedController.class));

    }

    @Test
    public void testBasicHEADAnnotatedRoute() {

        Router router = buildAnnotatedRouter(MockAnnotatedController.class);
        assertEquals("/head", router.getReverseMETHOD(HttpMethod.HEAD, MockAnnotatedController.class));

    }

    @Test
    public void testBestFitRoutes() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        Router router = buildAnnotatedRouter(UsersController.class);

        String index = router.getReverseGET(UsersController.class);
        String user = router.getReverseGET(UsersController.class, "username", "james");

        assertThat(index, CoreMatchers.equalTo("/users"));
        assertThat(user, CoreMatchers.equalTo("/users/james"));

    }

    @Test
    public void testAnnotatedAmbiguousPriority() {

        Router router = buildAnnotatedRouter(AmbiguousOrderController.class);
        assertEquals(0, router.getRoutes().size());
    }

    @Test
    public void testAnnotatedInvalidPriority() {

        Router router = buildAnnotatedRouter(InvalidOrderController.class);
        assertEquals(0, router.getRoutes().size());
    }

    @Test
    public void testDuplicateNames() {

        Router router = buildAnnotatedRouter(DuplicateNamesController.class);
        assertEquals(0, router.getRoutes().size());
    }

    @Test
    public void testInvalidReturnType() {

        Router router = buildAnnotatedRouter(InvalidReturnTypeController.class);
        assertEquals(0, router.getRoutes().size());
    }

    @Test
    public void testNonAnnotatedControllerRegistration() {

        Router router = buildAnnotatedRouter(TestController.class);
        assertEquals(0, router.getRoutes().size());
    }

    // Just a dummy TestController for mocking...
    public static class TestController {

        public Result index() {

            return Results.ok();

        }

        public Result user() {

            return Results.ok();

        }

    }

    // Just a dummy AnnotatedController for mocking...
    public static class AnnotatedController {

        @RouteDef(uri="/testroute")
        public Result index() {

            return Results.ok();

        }

        @RouteDef(uri="/user/{email}/{id: .*}", method=HttpMethod.PUT)
        public Result user(@PathParam("email") String email,
                                      @PathParam("id") String id) {

            return Results.ok();

        }
    }


    private Router buildAnnotatedRouter(Class<?> controllerClass) {
        RouterImpl router = new RouterImpl(injector, new NinjaPropertiesImpl(NinjaMode.dev));
        router.register(controllerClass);
        router.compileRoutes();
        return router;
    }

    public static class MockAnnotatedController {

        @RouteDef(uri="/options", method=HttpMethod.OPTIONS)
        public Result options() {
            return null;
        }

        @RouteDef(uri="/head", method=HttpMethod.HEAD)
        public Result head() {
            return null;
        }

        @RouteDef(uri="/get", method=HttpMethod.GET)
        public Result get() {
            return null;
        }

        @RouteDef(uri="/put", method=HttpMethod.PUT)
        public Result put() {
            return null;
        }

        @RouteDef(uri="/post", method=HttpMethod.POST)
        public Result post() {
            return null;
        }

        @RouteDef(uri="/delete", method=HttpMethod.DELETE)
        public Result delete() {
            return null;
        }

    }

    public static class UsersController {

        @RouteDef(uri="/users")
        public Result index() {

            return Results.ok();

        }

        @RouteDef(uri="/users/{username}", order=2)
        public Result user(@PathParam("username") String username) {

            return Results.ok();

        }
    }

    public static class AmbiguousOrderController {

        @RouteDef(uri="/get", method=HttpMethod.GET)
        public Result get() {
            return null;
        }

        @RouteDef(uri="/get2", method=HttpMethod.GET)
        public Result get2() {
            return null;
        }

    }

    public static class InvalidOrderController {

        @RouteDef(uri="/get", method=HttpMethod.GET)
        public Result get() {
            return null;
        }

        @RouteDef(uri="/get2", method=HttpMethod.GET, order=0)
        public Result get2() {
            return null;
        }

    }

    public static class DuplicateNamesController {

        @RouteDef(uri="/get", method=HttpMethod.GET)
        public Result get() {
            return null;
        }

        @RouteDef(uri="/get2", method=HttpMethod.GET, order=2)
        public Result get(String test) {
            return null;
        }

    }

    public static class InvalidReturnTypeController {

        @RouteDef(uri="/get", method=HttpMethod.GET)
        public String get() {
            return null;
        }

    }

}
