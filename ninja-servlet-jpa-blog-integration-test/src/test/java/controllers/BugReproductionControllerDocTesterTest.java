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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import models.ArticleDto;
import models.ArticlesDto;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import models.Article;
import ninja.NinjaDocTester;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Assert;
import static org.junit.Assert.assertThat;

public class BugReproductionControllerDocTesterTest extends NinjaDocTester {
    
    String GET_TEST_AGAINST_BUG_157_TEST_1 = "/test_against_bug_157_test1";
    String GET_TEST_AGAINST_BUG_157_TEST_2 = "/test_against_bug_157_test2";

    @Test
    public void testThatBug157IsFixedTest1() {
        
        Response response 
                = makeRequest(Request.GET().url(testServerUrl().path(GET_TEST_AGAINST_BUG_157_TEST_1)));
        
        assertThat(response.payload, equalTo("ok"));
        
    } 
    
    @Test
    public void testThatBug157IsFixedTest2() {
        
        Response response 
                = makeRequest(Request.GET().url(testServerUrl().path(GET_TEST_AGAINST_BUG_157_TEST_2)));
        
        assertThat(response.payload, equalTo("ok"));
        
    }

}
