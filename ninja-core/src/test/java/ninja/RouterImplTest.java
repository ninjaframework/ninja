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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import ninja.utils.NinjaProperties;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Provider;

import java.util.Map;

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
    Context context;

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
        router.GET().route("/u{userId: .*}/entries/{entryId: .*}").with(TestController.class, "entry");

        router.GET().route("/.*").with(Results.redirect("/"));

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
    public void testGetReverseRouteWithMultipleRegexWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "entry", "userId", 1, "entryId", 100);

        assertThat(route, equalTo("/u1/entries/100"));

    }

    @Test
    public void testRedirect() {
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        Route route = router.getRouteFor("GET", "/this-should-redirect");

        assertThat(route.getUrl(), equalTo("/.*"));

        FilterChain filterChain = route.getFilterChain();
        Result result = filterChain.next(context);

        Map<String, String> headers = result.getHeaders();
        assertTrue(headers.containsKey("Location"));
        assertThat(result.getStatusCode(), equalTo(303));
    }

    @Test
    public void testRedirectResultNotShared() {
        // Make sure redirects result in a new 'Result' object each time,
        // as the Result object can be modified by filters and by the default
        // SessionImpl when Cookies are saved.
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        Route route = router.getRouteFor("GET", "/redirect1");

        assertThat(route.getUrl(), equalTo("/.*"));

        FilterChain filterChain = route.getFilterChain();
        Result result = filterChain.next(context);
        result.addHeader("dummy", "value");

        route = router.getRouteFor("GET", "/redirect2");
        assertThat(route.getUrl(), equalTo("/.*"));

        filterChain = route.getFilterChain();
        result = filterChain.next(context);

        Map<String, String> headers = result.getHeaders();
        assertFalse(headers.containsKey("dummy"));
    }

    // Just a dummy TestController for mocking...
    public static class TestController {

        public Result index() {

            return Results.ok();

        }

        public Result user() {

            return Results.ok();

        }

        public Result entry() {

            return Results.ok();

        }

    }

}
