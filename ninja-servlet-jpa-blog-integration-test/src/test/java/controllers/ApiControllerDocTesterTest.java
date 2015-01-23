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

package controllers;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import models.Article;
import models.ArticleDto;
import models.ArticlesDto;
import ninja.NinjaDocTester;

import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class ApiControllerDocTesterTest extends NinjaDocTester {
    
    String GET_ARTICLES_URL = "/api/{username}/articles.json";
    String GET_ARTICLE_URL = "/api/{username}/article/{id}.json";
    String POST_ARTICLE_URL = "/api/{username}/article.json";
    String LOGIN_URL = "/login";
    
    String USER = "bob@gmail.com";

    @Test
    public void testGetAndPostArticleViaJson() {

        // /////////////////////////////////////////////////////////////////////
        // Test initial data:
        // /////////////////////////////////////////////////////////////////////
        
        sayNextSection("Retrieving articles for a user (Json)");
        
        say("Retrieving all articles of a user is a GET request to " + GET_ARTICLES_URL);
        
        Response response = sayAndMakeRequest(
                Request.GET().url(
                        testServerUrl().path(GET_ARTICLES_URL.replace("{username}", "bob@gmail.com"))));

        ArticlesDto articlesDto = getGsonWithLongToDateParsing().fromJson(response.payload, ArticlesDto.class);

        sayAndAssertThat("We get back all 3 articles of that user",
                articlesDto.articles.size(), 
                CoreMatchers.is(3));

        // /////////////////////////////////////////////////////////////////////
        // Post new article:
        // /////////////////////////////////////////////////////////////////////
        sayNextSection("Posting new article (Json)");
        
        say("Posting a new article is a post request to " + POST_ARTICLE_URL);
        say("Please note that you have to be authenticated in order to be allowed to post.");
        
        ArticleDto articleDto = new ArticleDto();
        articleDto.content = "contentcontent";
        articleDto.title = "new title new title";

        response = sayAndMakeRequest(
                Request.POST().url(
                    testServerUrl().path(POST_ARTICLE_URL.replace("{username}", USER)))
                .payload(articleDto));
        
        sayAndAssertThat(
                "You have to be authenticated in order to post articles" 
                , response.httpStatus 
                , CoreMatchers.is(403));
        
        doLogin();

        say("Now we are authenticated and expect the post to succeed...");
        response = sayAndMakeRequest(Request.POST().url(
                testServerUrl().path(POST_ARTICLE_URL.replace("{username}", USER)))
                .contentTypeApplicationJson()
                .payload(articleDto));

        sayAndAssertThat("After successful login we are able to post articles"
                , response.httpStatus
                , CoreMatchers.is(200));

        // /////////////////////////////////////////////////////////////////////
        // Fetch articles again => assert we got a new one ...
        // /////////////////////////////////////////////////////////////////////
        
        say("If we now fetch the articles again we are getting a new article (the one we have posted successfully");
        response = sayAndMakeRequest(Request.GET().url(testServerUrl().path(GET_ARTICLES_URL.replace("{username}", "bob@gmail.com"))));

        articlesDto = getGsonWithLongToDateParsing().fromJson(response.payload, ArticlesDto.class);
        // one new result:
        sayAndAssertThat("We are now getting 4 articles."
                , articlesDto.articles.size()
                , CoreMatchers.is(4));
        
        
        
        // /////////////////////////////////////////////////////////////////////
        // Fetch single article
        // /////////////////////////////////////////////////////////////////////
        say("We can also fetch an individual article via the Json Api.");
        say("That's a GET request to: " + GET_ARTICLE_URL);
        response = sayAndMakeRequest(
                Request.GET().url(
                        testServerUrl().path(
                                GET_ARTICLE_URL
                                        .replace("{username}", "bob@gmail.com")
                                        .replace("{id}", "1"))));

        Article article = getGsonWithLongToDateParsing().fromJson(response.payload, Article.class);
        // one new result:
        sayAndAssertThat("And we got back the first article"
                , article.id
                , CoreMatchers.is(1L));
    }



    private Gson getGsonWithLongToDateParsing() {
        // Creates the json object which will manage the information received
        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json,
                                    Type typeOfT,
                                    JsonDeserializationContext context)
                    throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();

        return gson;
    }

    private void doLogin() {

        say("To authenticate we send our credentials to " + LOGIN_URL);
        say("We are then issued a cookie from the server that authenticates us in further requests");

        Map<String, String> formParameters = Maps.newHashMap();
        formParameters.put("username", "bob@gmail.com");
        formParameters.put("password", "secret");
        
        makeRequest(
                Request.POST().url(
                    testServerUrl().path(LOGIN_URL))
                .addFormParameter("username", "bob@gmail.com")
                .addFormParameter("password", "secret"));
         }

}
