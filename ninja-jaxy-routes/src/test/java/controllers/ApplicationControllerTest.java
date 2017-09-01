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

package controllers;

import ninja.NinjaDocTester;
import ninja.Result;
import ninja.Route;
import ninja.RouterImpl;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import testapplication.conf.Routes;
import testapplication.controllers.ApplicationController;

import com.google.inject.Injector;
import com.google.inject.Provider;
import ninja.RouteBuilderImpl;
import ninja.utils.NinjaBaseDirectoryResolver;
import ninja.utils.NinjaProperties;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationControllerTest extends NinjaDocTester {

    @Mock
    Injector injector;

    NinjaBaseDirectoryResolver ninjaBaseDirectoryResolver = new NinjaBaseDirectoryResolver(Mockito.mock(NinjaProperties.class));

    @Test
    public void testGet() {

        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/get")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("get works."));

    }

    @Test
    public void testGet012() {

        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/0/1/2/get")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("get works."));

    }

    @Test
    public void testPut() {

        Response response = makeRequest(Request.PUT().url(
                testServerUrl().path("/base/middle/app/put")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("put works."));

    }

    @Test
    public void testPut012() {

        Response response = makeRequest(Request.PUT().url(
                testServerUrl().path("/0/1/2/put")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("put works."));

    }

    @Test
    public void testPost() {

        Response response = makeRequest(Request.POST().url(
                testServerUrl().path("/base/middle/app/post")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("post works."));

    }

    @Test
    public void testPost012() {

        Response response = makeRequest(Request.POST().url(
                testServerUrl().path("/0/1/2/post")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("post works."));

    }

    @Test
    public void testDelete() {

        Response response = makeRequest(Request.DELETE().url(
                testServerUrl().path("/base/middle/app/delete")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("delete works."));

    }

    @Test
    public void testDelete012() {

        Response response = makeRequest(Request.DELETE().url(
                testServerUrl().path("/0/1/2/delete")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("delete works."));

    }

    @Test
    public void testTestModeIndex() {

        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/mode/test")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("test mode works."));

    }

    @Test
    public void testTestModeIndex012() {

        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/0/1/2/mode/test")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("test mode works."));

    }

    @Test
    public void testDevModeIndexMissing() {
        // Server runs in Test mode making pure "dev" routes unavailable
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/mode/dev")));

        Assert.assertThat(response.httpStatus,
                CoreMatchers.equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testDevModeIndexMissing012() {
        // Server runs in Test mode making pure "dev" routes unavailable
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/0/1/2/mode/dev")));

        Assert.assertThat(response.httpStatus,
                CoreMatchers.equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testDevAndTestMode() {
        // Server runs in Test mode. This route is Dev & Test.
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/mode/dev/and/test")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("dev and test works."));

    }

    @Test
    public void testDevAndTestMode012() {
        // Server runs in Test mode. This route is Dev & Test.
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/0/1/2/mode/dev/and/test")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("dev and test works."));

    }

    @Test
    public void testProdMode() {
        // Server runs in Test mode. This route is Prod.
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/mode/prod")));

        Assert.assertThat(response.httpStatus,
                CoreMatchers.equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testProdMode012() {
        // Server runs in Test mode. This route is Prod.
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/0/1/2/mode/prod")));

        Assert.assertThat(response.httpStatus,
                CoreMatchers.equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testProdAndTestMode() {
        // Server runs in Test mode. This route is Prod & Test.
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/mode/prod/and/test")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("prod and test works."));

    }

    @Test
    public void testProdAndTestMode012() {
        // Server runs in Test mode. This route is Prod & Test.
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/0/1/2/mode/prod/and/test")));

        Assert.assertThat(response.payload,
                CoreMatchers.equalTo("prod and test works."));

    }

    @Test
    public void testRouteOrdering() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        Provider<RouteBuilderImpl> routeBuilderImplProvider = Mockito.mock(Provider.class);
        when(routeBuilderImplProvider.get()).thenAnswer(
                (invocation) -> new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver));
        RouterImpl router = new RouterImpl(injector, ninjaProperties, null, routeBuilderImplProvider);
        Routes routes = new Routes(ninjaProperties);
        routes.init(router);
        router.compileRoutes();

        Route route0 = router.getRoutes().get(0);
        Assert.assertThat(route0.getUri(),
                CoreMatchers.equalTo("/base/middle/app/get"));

        Route route8 = router.getRoutes().get(8);
        Assert.assertThat(route8.getUri(),
                CoreMatchers.equalTo("/base/middle/app/put"));

        Route route16 = router.getRoutes().get(16);
        Assert.assertThat(route16.getUri(),
                CoreMatchers.equalTo("/base/middle/app/post"));

    }

    @Test
    public void testMissingKeyedRoute() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        Provider<RouteBuilderImpl> routeBuilderImplProvider = Mockito.mock(Provider.class);
        when(routeBuilderImplProvider.get()).thenAnswer(
                (invocation) -> new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver));
        RouterImpl router = new RouterImpl(injector, ninjaProperties, null, routeBuilderImplProvider);
        Routes routes = new Routes(ninjaProperties);
        routes.init(router);
        router.compileRoutes();

        String route = router.getReverseRoute(ApplicationController.class,
                "testKeyedRoute");

        Assert.assertNull(route);

    }

    @Test
    public void testHasKeyedRoute() {
        NinjaPropertiesImpl ninjaProperties = new NinjaPropertiesImpl(NinjaMode.test);
        ninjaProperties.setProperty("testkey", "true");
        Provider<RouteBuilderImpl> routeBuilderImplProvider = Mockito.mock(Provider.class);
        when(routeBuilderImplProvider.get()).thenAnswer(
                (invocation) -> new RouteBuilderImpl(ninjaProperties, ninjaBaseDirectoryResolver));
        RouterImpl router = new RouterImpl(injector, ninjaProperties, null, routeBuilderImplProvider);
        Routes routes = new Routes(ninjaProperties);
        routes.init(router);
        router.compileRoutes();

        String route = router.getReverseRoute(ApplicationController.class,
                "testKeyedRoute");

        Assert.assertThat(route,
                CoreMatchers.equalTo("/base/middle/app/keyTest"));

    }

    @Test
    public void testWithoutMethodPath() throws Exception {
        Response response = makeRequest(Request.GET().url(
                testServerUrl().path("/base/middle/app/")));
        Assert.assertThat(response.payload, CoreMatchers.equalTo("route without method path works."));
    }
}
