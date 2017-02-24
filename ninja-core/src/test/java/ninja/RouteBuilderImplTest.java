/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import com.google.common.collect.Lists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;
import com.google.inject.Provider;
import java.util.List;
import ninja.utils.MethodReference;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import org.hamcrest.Matchers;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import ninja.application.ApplicationFilters;

@RunWith(MockitoJUnitRunner.class)
public class RouteBuilderImplTest {

    @Mock
    Injector injector;
    
    @Mock
    NinjaProperties ninjaProperties;
    
    NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver;
    
    RouteBuilderImpl routeBuilder;
    
    @Before
    public void before() {
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);
        this.routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
    }

    @Test
    public void basicGETRoute() {
        routeBuilder.GET().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("GET", "/index"));
    }

    @Test
    public void basicPOSTRoute() {
        routeBuilder.POST().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("POST", "/index"));
    }

    @Test
    public void basicPUTRoute() {
        routeBuilder.PUT().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("PUT", "/index"));
    }

    @Test
    public void basicRoutes() {
        routeBuilder.OPTIONS().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("OPTIONS", "/index"));
    }
    
    @Test
    public void basisHEAD() {
        routeBuilder.HEAD().route("/index");

        assertTrue(buildRoute(routeBuilder).matches("HEAD", "/index"));
    }
    
    @Test
    public void basicAnyHttpMethod() {
        routeBuilder.METHOD("PROPFIND").route("/index");

        assertTrue(buildRoute(routeBuilder).matches("PROPFIND", "/index"));
    }

    @Test
    public void basicRoutesWithRegex() {
        routeBuilder.GET().route("/.*");

        Route route = buildRoute(routeBuilder);
        
        // make sure the route catches everything
        assertTrue(route.matches("GET", "/index"));
        assertTrue(route.matches("GET", "/stylesheet.css"));
        assertTrue(route.matches("GET", "/public/stylesheet.css"));
        assertTrue(route.matches("GET", "/public/bootstrap.js"));
    }

    @Test
    public void basicPlaceholersAndParameters() {
        // /////////////////////////////////////////////////////////////////////
        // One parameter:
        // /////////////////////////////////////////////////////////////////////
        routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
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
        routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
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
    public void basicPlaceholersParametersAndRegex() {
        // test that parameter parsing works in conjunction with
        // regex expressions...
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
    public void basicPlaceholersParametersAndRegexInsideVariableParts() {
        // test that parameter parsing works in conjunction with
        // regex expressions...
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
        routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
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
    public void parametersDontCrossSlashes() {
        routeBuilder.GET().route("/blah/{id}/{id2}/{id3}/morestuff/at/the/end");
        Route route = buildRoute(routeBuilder);
        // this must match
        assertTrue(route
                .matches("GET", "/blah/id/id2/id3/morestuff/at/the/end"));
        // this should not match as the last "end" is missing
        assertFalse(route.matches("GET", "/blah/id/id2/id3/morestuff/at/the"));
    }

    @Test
    public void pointsInRegexDontCrashRegexInTheMiddleOfTheRoute() {
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
    public void pointsInRegexDontCrashRegexAtEnd() {
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
    public void regexInRouteWorksWithEscapes() {
        // Test escaped constructs in regex
        // regex with escaped construct in a route
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
    public void regexInRouteWorksWithoutSlashAtTheEnd() {
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
    public void routeWithUrlEncodedSlashGetsChoppedCorrectly() {
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
    public void routeWithResult() {
        Context context = mock(Context.class);
        
        String template = "/directly_result/stuff";
        routeBuilder.GET().route("/directly_result/route").with(Results.html().template(template));

        Route route = routeBuilder.buildRoute(injector);
        assertTrue(route.matches("GET", "/directly_result/route"));

        Result result = route.getFilterChain().next(context);
        assertEquals(result.getTemplate(), template);
    }

    @Test
    public void failedControllerRegistration() {
        routeBuilder.GET().route("/failure").with(MockController.class, "DoesNotExist");

        try {	
            Route route = routeBuilder.buildRoute(injector);
            assertTrue(route == null);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalStateException);
        }
    }
    
    @Test
    public void routeWithMethodReference() throws Exception {
        routeBuilder.GET().route("/method_reference").with(new MethodReference(MockController.class, "execute"));

        Route route = routeBuilder.buildRoute(injector);
        assertTrue(route.matches("GET", "/method_reference"));

        assertThat(route.getControllerClass(), is(MockController.class));
    }

    private Route buildRoute(RouteBuilderImpl builder) {
        builder.with(MockController.class, "execute");
        return builder.buildRoute(injector);
    }

    public static class MockController {
        public Result execute() {
            return null;
        }
        public Result execute2(Context context) {
            return null;
        }
        static public Result execute3(Context context) {
            return null;
        }
    }
    
    @Test
    public void routeToAnyInstanceMethodReference() throws Exception {
        routeBuilder.GET().route("/execute").with(MockController::execute);
        Route route = routeBuilder.buildRoute(injector);
        
        assertTrue(route.matches("GET", "/execute"));
        assertThat(route.getControllerClass(), is(MockController.class));
        assertThat(route.getControllerMethod().getName(), is("execute"));
    }
    
    @Test
    public void routeToSpecificInstanceMethodReference() throws Exception {
        MockController controller = new MockController();
        
        routeBuilder.GET().route("/execute").with(controller::execute);
        Route route = routeBuilder.buildRoute(injector);
        
        assertTrue(route.matches("GET", "/execute"));
        assertThat(route.getControllerClass().getCanonicalName(), startsWith(this.getClass().getCanonicalName()));
        assertThat(route.getControllerMethod().getName(), is("apply"));
    }
    
    @Test
    public void routeToStaticMethodReference() throws Exception {
        routeBuilder.GET().route("/execute").with(MockController::execute3);
        Route route = routeBuilder.buildRoute(injector);
        
        assertTrue(route.matches("GET", "/execute"));
        assertThat(route.getControllerClass(), is(MockController.class));
        assertThat(route.getControllerMethod().getName(), is("execute3"));
    }
    
    @Test
    @SuppressWarnings("Convert2Lambda")
    public void routeToAnonymousClassReference() throws Exception {
        
        routeBuilder.GET().route("/execute").with(new ControllerMethods.ControllerMethod0() {
            @Override
            public Result apply() {
                return Results.redirect("/");
            }
        });
        
        Route route = routeBuilder.buildRoute(injector);
        
        assertTrue(route.matches("GET", "/execute"));
        assertThat(route.getControllerClass().isAnonymousClass(), is(true));
        assertThat(route.getControllerMethod().getName(), is("apply"));
    }
    
    @Test
    public void routeToAnonymousMethodReference() throws Exception {
        routeBuilder.GET().route("/execute").with(() -> Results.redirect("/"));
        Route route = routeBuilder.buildRoute(injector);
        
        assertTrue(route.matches("GET", "/execute"));
        // should be a class within this test class as a real lambda
        assertThat(route.getControllerClass().getCanonicalName(), startsWith(this.getClass().getCanonicalName()));
        assertThat(route.getControllerMethod().getName(), is("apply"));
    }
    
    
    private class DummyFilter implements Filter {
        int executed = 0;
        @Override
        public Result filter(FilterChain filterChain, Context context) {
            executed++;
            return filterChain.next(context);
        }
    }
    
    private class DummyFilter2 extends DummyFilter {}
    
    
    @Test
    public void testGlobalFilters() throws Exception {
        // given
        // different setup that uses com.example packages and thus reads the Filters there
        Mockito.when(ninjaProperties.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
               .thenReturn("com.example");
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);
        this.routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
                       
        DummyFilter dummyFilter = new DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        com.example.conf.Filters filters = new com.example.conf.Filters(DummyFilter.class);
        
        Mockito.when(injector.getInstance(com.example.conf.Filters.class)).thenReturn(filters);
        Mockito.when(injector.getProvider(DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        
        routeBuilder.GET().route("/").with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);

        FilterChain filterChain = route.getFilterChain();
        
        // when
        Result result = filterChain.next(context);
 
        // then
        Mockito.verify(injector).getInstance(com.example.conf.Filters.class);
        assertThat(dummyFilter.executed, Matchers.equalTo(1));
        assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }
    
        
    @Test
    public void testThatGlobalFiltersInRouteReplaceGlobalFiltersInConfFilters() throws Exception {
        // DummyFilter is defined in conf.Filters, but .globalFilters(DummyFilter2.class) should
        // override that.
        
        // given
        // different setup that uses com.example packages and thus reads the Filters there
        Mockito.when(ninjaProperties.get(NinjaConstant.APPLICATION_MODULES_BASE_PACKAGE))
               .thenReturn("com.example");
        this.ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(ninjaProperties);
        this.routeBuilder = new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver);
                       
        DummyFilter2 dummyFilter2 = new DummyFilter2();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        
        Mockito.when(injector.getProvider(DummyFilter2.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter2);
        
        routeBuilder.GET().route("/").globalFilters(DummyFilter2.class).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);

        FilterChain filterChain = route.getFilterChain();
        
        // when
        Result result = filterChain.next(context);
 
        // then
        Mockito.verify(injector, Mockito.never()).getProvider(DummyFilter.class);
        Mockito.verify(injector).getProvider(DummyFilter2.class);
        assertThat(dummyFilter2.executed, Matchers.equalTo(1));
        assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }
    
    @Test
    public void testWithFiltersClass() throws Exception {
        // given        
        DummyFilter dummyFilter = new DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        
        Mockito.when(injector.getProvider(DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        
        routeBuilder.GET().route("/").filters(DummyFilter.class).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);

        FilterChain filterChain = route.getFilterChain();
        
        // when
        Result result = filterChain.next(context);
 
        // then
        assertThat(dummyFilter.executed, Matchers.equalTo(1));
        assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }
    
    @Test
    public void testWithFiltersList() throws Exception {
        // given        
        DummyFilter dummyFilter = new DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        
        Mockito.when(injector.getProvider(DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        
        routeBuilder.GET().route("/").filters(Lists.newArrayList(DummyFilter.class)).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);

        FilterChain filterChain = route.getFilterChain();
        
        // when
        Result result = filterChain.next(context);
 
        // then
        assertThat(dummyFilter.executed, Matchers.equalTo(1));
        assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }
    
    @Test
    public void testWithGlobalFiltersClass() throws Exception {
        // given        
        DummyFilter dummyFilter = new DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        
        Mockito.when(injector.getProvider(DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        
        routeBuilder.GET().route("/").globalFilters(DummyFilter.class).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);

        FilterChain filterChain = route.getFilterChain();
        
        // when
        Result result = filterChain.next(context);
 
        // then
        assertThat(dummyFilter.executed, Matchers.equalTo(1));
        assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }
    
    @Test
    public void testWithGlobalFiltersList() throws Exception {
        // given        
        DummyFilter dummyFilter = new DummyFilter();
        Result expectedResult = Mockito.mock(Result.class);
        Context context = Mockito.mock(Context.class);
        Provider filterProvider = Mockito.mock(Provider.class);
        
        Mockito.when(injector.getProvider(DummyFilter.class)).thenReturn(filterProvider);
        Mockito.when(filterProvider.get()).thenReturn(dummyFilter);
        
        routeBuilder.GET().route("/").globalFilters(Lists.newArrayList(DummyFilter.class)).with(() -> expectedResult);
        Route route = routeBuilder.buildRoute(injector);

        FilterChain filterChain = route.getFilterChain();
        
        // when
        Result result = filterChain.next(context);
 
        // then
        assertThat(dummyFilter.executed, Matchers.equalTo(1));
        assertThat(result, org.hamcrest.Matchers.equalTo(expectedResult));
    }

}
