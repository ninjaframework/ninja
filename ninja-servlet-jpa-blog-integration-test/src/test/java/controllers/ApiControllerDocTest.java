/**
 * Copyright (C) 2013 the original author or authors.
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
import ninja.NinjaApiDocTest;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import de.devbliss.apitester.ApiResponse;

public class ApiControllerDocTest extends NinjaApiDocTest {
    
    String GET_ARTICLES_URL = "/api/{username}/articles.json";
    String POST_ARTICLE_URL = "/api/{username}/article.json";
    String LOGIN_URL = "/login";
    
    String USER = "bob@gmail.com";

    @Test
    public void testGetAndPostArticleViaJson() throws Exception {

        // /////////////////////////////////////////////////////////////////////
        // Test initial data:
        // /////////////////////////////////////////////////////////////////////
        
        sayNextSection("Retrieving articles for a user (Json)");
        
        say("Retrieving all articles of a user is a GET request to " + GET_ARTICLES_URL);
        
        ApiResponse apiResponse = makeGetRequest(buildUri(GET_ARTICLES_URL.replace("{username}", "bob@gmail.com")));

        ArticlesDto articlesDto = getGsonWithLongToDateParsing().fromJson(apiResponse.payload, ArticlesDto.class);

        assertEqualsAndSay(3, articlesDto.articles.size(), "We get back all 3 articles of that user");

        // /////////////////////////////////////////////////////////////////////
        // Post new article:
        // /////////////////////////////////////////////////////////////////////
        sayNextSection("Posting new article (Json)");
        
        say("Posting a new article is a post request to " + POST_ARTICLE_URL);
        say("Please note that you have to be authenticated in order to be allowed to post.");
        
        ArticleDto articleDto = new ArticleDto();
        articleDto.content = "contentcontent";
        articleDto.title = "new title new title";

        apiResponse = makePostRequest(buildUri(POST_ARTICLE_URL.replace("{username}", USER)), articleDto);
        assertEqualsAndSay(403, apiResponse.httpStatus, "You have to be authenticated in order to post articles");
        
        doLogin();

        say("Now we are authenticated and expect the post to succeed...");
        apiResponse = makePostRequest(buildUri(POST_ARTICLE_URL.replace("{username}", USER)), articleDto);
        assertEqualsAndSay(200, apiResponse.httpStatus, "After successful login we are able to post articles");

        // /////////////////////////////////////////////////////////////////////
        // Fetch articles again => assert we got a new one ...
        // /////////////////////////////////////////////////////////////////////
        
        say("If we now fetch the articles again we are getting a new article (the one we have posted successfully");
        apiResponse = makeGetRequest(buildUri(GET_ARTICLES_URL.replace("{username}", "bob@gmail.com")));

        articlesDto = getGsonWithLongToDateParsing().fromJson(apiResponse.payload, ArticlesDto.class);
        // one new result:
        assertEqualsAndSay(4, articlesDto.articles.size(), "We are now getting 4 articles.");

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

    private void doLogin() throws Exception {

        say("To authenticate we send our credentials to " + LOGIN_URL);
        say("We are then issued a cookie from the server that authenticates us in further requests");

        Map<String, String> formParameters = Maps.newHashMap();
        formParameters.put("username", "bob@gmail.com");
        formParameters.put("password", "secret");
        
        makePostRequest(buildUri(LOGIN_URL, formParameters));

    }

    @Override
    public String getFileName() {
        return this.getClass().getSimpleName();
    }

}
