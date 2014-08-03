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

package controllers;

import ninja.NinjaDocTester;
import ninja.Result;

import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;



public class ApplicationControllerTest extends NinjaDocTester {


    @Test
    public void testGet() {

        Response response = makeRequest(
                Request.GET().url(testServerUrl().path("/get")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("get works."));

    }

    @Test
    public void testPut() {

        Response response = makeRequest(
                Request.PUT().url(testServerUrl().path("/put")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("put works."));

    }

    @Test
    public void testPost() {

        Response response = makeRequest(
                Request.POST().url(testServerUrl().path("/post")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("post works."));

    }

    @Test
    public void testDelete() {

        Response response = makeRequest(
                Request.DELETE().url(testServerUrl().path("/delete")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("delete works."));

    }

    @Test
    public void testTestModeIndex() {

        Response response = makeRequest(
                Request.GET().url(testServerUrl().path("/mode/test")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("test mode works."));

    }

    @Test
    public void testDevModeIndexMissing() {
    	// Server runs in Test mode making pure "dev" routes unavailable
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path("/mode/dev")));

        Assert.assertThat(response.httpStatus, CoreMatchers.equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testDevAndTestMode() {
    	// Server runs in Test mode. This route is Dev & Test.
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path("/mode/dev/and/test")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("dev and test works."));

    }

    @Test
    public void testProdMode() {
    	// Server runs in Test mode. This route is Prod.
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path("/mode/prod")));

        Assert.assertThat(response.httpStatus, CoreMatchers.equalTo(Result.SC_404_NOT_FOUND));

    }

    @Test
    public void testProdAndTestMode() {
    	// Server runs in Test mode. This route is Prod & Test.
        Response response = makeRequest(
                Request.GET().url(testServerUrl().path("/mode/prod/and/test")));

        Assert.assertThat(response.payload, CoreMatchers.equalTo("prod and test works."));

    }

}
