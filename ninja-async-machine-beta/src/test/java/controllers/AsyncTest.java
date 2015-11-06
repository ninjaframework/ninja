package controllers;

/**
 * Copyright (C) 2012-2015 the original author or authors.
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



import ninja.NinjaDocTester;
import ninja.utils.Message;

import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;



public class AsyncTest extends NinjaDocTester {

    @Test
    public void testThatAsyncWorks() {

        Response response = makeRequest(Request.GET().url(testServerUrl().path("async")));
        
        Assert.assertThat(response.httpStatus, CoreMatchers.equalTo(200));
        Assert.assertTrue(response.payload.contains("Async works!"));


    }
    
    
    @Test
    public void testThatErrorHandlingWorksHtml() {

        Response response = makeRequest(Request.GET().url(testServerUrl().path("throw_exception")));
        Assert.assertThat(response.httpStatus, CoreMatchers.equalTo(400));
        Assert.assertTrue(response.payload.contains("Oops. That&#39;s a bad request and all we know."));

    }
    
    @Test
    public void testThatErrorHandlingWorksJson() {

        Response response = makeRequest(
                Request.GET().addHeader("ACCEPT", "application/json")
                        .url(testServerUrl().path("throw_exception")));

        Assert.assertThat(response.httpStatus, CoreMatchers.equalTo(400));
        Message message = response.payloadJsonAs(Message.class);
        Assert.assertThat(message.text, CoreMatchers.equalTo("Oops. That's a bad request and all we know."));

    }
    
    

}
