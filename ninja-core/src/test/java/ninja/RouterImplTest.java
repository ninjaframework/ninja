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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import ninja.utils.NinjaProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.Collections;
import ninja.ControllerMethods.ControllerMethod0;
import ninja.ControllerMethods.ControllerMethod1;
import ninja.params.Param;
import ninja.params.ParamParsers;
import ninja.utils.MethodReference;
import ninja.validation.ValidationImpl;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

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
    @SuppressWarnings("Convert2Lambda")
    public void before() {
        when(testControllerProvider.get()).thenReturn(new TestController());
        when(injector.getProvider(TestController.class)).thenReturn(testControllerProvider);
        when(injector.getInstance(ParamParsers.class)).thenReturn(new ParamParsers(Collections.emptySet()));
        router = new RouterImpl(injector, ninjaProperties);

        // add route:
        router.GET().route("/testroute").with(TestController.class, "index");
        router.GET().route("/user/{email}/{id: .*}").with(TestController.class, "user");
        router.GET().route("/u{userId: .*}/entries/{entryId: .*}").with(TestController.class, "entry");
        
        // second route to index should not break reverse routing matching the first
        router.GET().route("/testroute/another_url_by_index").with(TestController.class, "index");
        router.GET().route("/ref").with(new MethodReference(TestController.class, "ref"));
        
        // functional interface / lambda routing
        TestController testController1 = new TestController("Hi!");
        router.GET().route("/any_instance_method_ref").with(TestController::home);
        router.GET().route("/any_instance_method_ref_exception").with(TestController::exception);
        router.GET().route("/any_instance_method_ref2").with(ControllerMethods.of(TestController::home));
        router.GET().route("/specific_instance_method_ref").with(testController1::message);
        router.GET().route("/specific_instance_method_ref_annotations").with(testController1::status);
        router.GET().route("/anonymous_method_ref").with(() -> Results.status(202));
        Result staticResult = Results.status(208);
        router.GET().route("/anonymous_method_ref_captured").with(() -> staticResult);
        router.GET().route("/anonymous_method_ref_context").with((Context context) -> Results.status(context.getParameterAsInteger("status")));
        router.GET().route("/anonymous_class").with(new ControllerMethod0() {
            @Override
            public Result apply() {
                return Results.status(203);
            }
        });
        router.GET().route("/anonymous_class_annotations").with(new ControllerMethod1<Integer>() {
            @Override
            public Result apply(@Param("status") Integer status) {
                return Results.status(status);
            }
        });
        
        router.compileRoutes();
    }

    @Test
    public void getReverseRouteWithNoContextPathWorks() {
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "index");

        assertThat(route, is("/testroute"));
        
    }
    
    @Test
    public void getReverseRouteContextPathWorks() {
        String contextPath = "/myappcontext";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "index");

        assertThat(route, is("/myappcontext/testroute"));

    }

    @Test
    public void getReverseRouteWithRegexWorks() {
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(
                TestController.class,
                "user",
                "email",
                "me@me.com",
                "id",
                10000);

        assertThat(route, is("/user/me@me.com/10000"));

    }

    @Test
    public void getReverseRouteWithRegexAndQueryParametersWorks() {

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
    public void getReverseRouteWithMultipleRegexWorks() {

        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "entry", "userId", 1, "entryId", 100);

        assertThat(route, equalTo("/u1/entries/100"));

    }
    
    @Test
    public void getReverseRouteWithMethodReference() {
        String contextPath = "";
        when(ninjaProperties.getContextPath()).thenReturn(contextPath);

        String route = router.getReverseRoute(TestController.class, "ref");
        
        String route2 = router.getReverseRoute(new MethodReference(TestController.class, "ref"));

        assertThat(route, is("/ref"));
        assertThat(route2, is("/ref"));
    }

    @Test
    public void routeForAnyInstanceMethodReference() {
        Route route = router.getRouteFor("GET", "/any_instance_method_ref");

        Result result = route.getFilterChain().next(null);
        
        assertThat(result.getStatusCode(), is(201));
    }
    
    @Test
    public void routeForAnyInstanceMethodReferenceThrowsException() {
        Route route = router.getRouteFor("GET", "/any_instance_method_ref_exception");

        try {
            Result result = route.getFilterChain().next(null);
            fail();
        } catch (Exception e) {
            assertThat(e.getCause().getMessage(), is("test"));
        }
    }
    
    @Test
    public void routeForAnyInstanceMethodReference2() {
        Route route = router.getRouteFor("GET", "/any_instance_method_ref2");

        Result result = route.getFilterChain().next(null);
        
        assertThat(result.getStatusCode(), is(201));
    }
    
    @Test
    public void routeForSpecificInstanceMethodReference() {
        Route route = router.getRouteFor("GET", "/specific_instance_method_ref");

        Result result = route.getFilterChain().next(null);
        
        // message set on specific instance
        assertThat(result.getRenderable(), is("Hi!"));
    }
    
    @Test
    public void routeForSpecificInstanceMethodReferenceWithAnnotations() {
        Context context = mock(Context.class);
        when(context.getParameter("status")).thenReturn("207");
        when(context.getValidation()).thenReturn(new ValidationImpl());
        
        Route route = router.getRouteFor("GET", "/specific_instance_method_ref_annotations");

        Result result = route.getFilterChain().next(context);
        
        // message set on specific instance
        assertThat(result.getStatusCode(), is(207));
        assertThat(result.getRenderable(), is("Hi!"));
    }
    
    @Test
    public void routeForAnonymoumsMethodReference() {
        Route route = router.getRouteFor("GET", "/anonymous_method_ref");

        Result result = route.getFilterChain().next(null);
        
        assertThat(result.getStatusCode(), is(202));
    }
    
    @Test
    public void routeForAnonymoumsMethodReferenceWithCaptured() {
        Context context = mock(Context.class);
        
        Route route = router.getRouteFor("GET", "/anonymous_method_ref_captured");

        Result result = route.getFilterChain().next(context);
        
        assertThat(result.getStatusCode(), is(208));
    }
    
    @Test
    public void routeForAnonymoumsMethodReferenceWithContext() {
        Context context = mock(Context.class);
        when(context.getParameterAsInteger("status")).thenReturn(206);
        
        Route route = router.getRouteFor("GET", "/anonymous_method_ref_context");

        Result result = route.getFilterChain().next(context);
        
        assertThat(result.getStatusCode(), is(206));
    }
    
    @Test
    public void routeForAnonymoumsClassInstance() {
        Route route = router.getRouteFor("GET", "/anonymous_class");

        Result result = route.getFilterChain().next(null);
        
        assertThat(result.getStatusCode(), is(203));
    }
    
    @Test
    public void routeForAnonymoumsClassInstanceWithAnnotations() {
        Context context = mock(Context.class);
        when(context.getParameter("status")).thenReturn("205");
        when(context.getValidation()).thenReturn(new ValidationImpl());
        
        Route route = router.getRouteFor("GET", "/anonymous_class_annotations");

        Result result = route.getFilterChain().next(context);
        
        assertThat(result.getStatusCode(), is(205));
    }

    /**
     * A dummy TestController for mocking.
     */
    public static class TestController {
        
        private final String message;
        
        public TestController() {
            this("not set");
        }
        
        public TestController(String message) {
            this.message = message;
        }
        
        public Result index() {
            return Results.ok();
        }

        public Result user() {
            return Results.ok();
        }

        public Result entry() {
            return Results.ok();
        }

        public Result ref() {
            return Results.ok();
        }

        public Result home() {
            return Results.status(201);
        }
        
        public Result message() {
            return Results.ok().render(message);
        }
        
        public Result status(@Param("status") Integer status) {
            return Results.status(status).render(message);
        }
        
        public Result exception() throws Exception {
            throw new Exception("test");
        }
    }
    
}
