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
import ninja.utils.NinjaProperties;
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

        router.compileRoutes();
    }

    @Test
    public void testSubRoutersWorkBasic() {
        router = new RouterImpl(injector, ninjaProperties);
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        Router usersRouter = router.route("/users");
        usersRouter.GET().route("/login").with(TestController.class, "user");
        Router documentRouter = router.route("/documents");
        documentRouter.GET().route("/index").with(TestController.class, "index");

        router.compileRoutes();

        String route = router.getReverseRoute(TestController.class, "user");
        assertThat(route, CoreMatchers.equalTo("/users/login"));

        route = router.getReverseRoute(TestController.class, "index");
        assertThat(route, CoreMatchers.equalTo("/documents/index"));
    }

    @Test
    public void testSubRoutersWorkAdvanced() {
        router = new RouterImpl(injector, ninjaProperties);
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        Router usersRouter = router.route("/users/{email}");
        usersRouter.GET().route("/notify").with(TestController.class, "user");

        Router usersDocumentRouter = usersRouter.route("/document/{id: .*}");
        usersDocumentRouter.GET().route("/delete").with(TestController.class, "index");

        router.compileRoutes();

        String route = router.getReverseRoute(
                TestController.class,
                "user",
                "email",
                "me@me.com");
        assertThat(route, CoreMatchers.equalTo("/users/me@me.com/notify"));

        route = router.getReverseRoute(
                TestController.class,
                "index",
                "email",
                "me@me.com",
                "id",
                10000);

        assertThat(route, CoreMatchers.equalTo("/users/me@me.com/document/10000/delete"));
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

    private Route buildRoute(RouteBuilderImpl builder) {
        builder.with(TestController.class, "index");
        return builder.buildRoute(injector);
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

}
