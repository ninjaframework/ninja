/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package testapplication.controllers;

import ninja.Result;
import ninja.Results;
import ninja.jaxy.DELETE;
import ninja.jaxy.Dev;
import ninja.jaxy.GET;
import ninja.jaxy.Order;
import ninja.jaxy.PATCH;
import ninja.jaxy.POST;
import ninja.jaxy.PUT;
import ninja.jaxy.Path;
import ninja.jaxy.Prod;
import ninja.jaxy.Requires;
import ninja.jaxy.Test;

import com.google.inject.Singleton;

@Singleton
@Path({ "/app", "/2" })
public class ApplicationController extends MiddleController {

    /*
     * HTTP METHOD TEST ROUTES
     */
    @Path("/get")
    @GET
    @Order(1)
    public Result testAnnotatedGetRoute() {

        return Results.text().render("get works.");

    }

    @Path("/put")
    @PUT
    @Order(2)
    public Result testAnnotatedPutRoute() {

        return Results.text().render("put works.");

    }

    @Path("/post")
    @POST
    @Order(3)
    public Result testAnnotatedPostRoute() {

        return Results.text().render("post works.");

    }

    @Path("/patch")
    @PATCH
    @Order(4)
    public Result testAnnotatedPatchRoute() {

        return Results.text().render("patch works.");

    }

    @Path("/delete")
    @DELETE
    @Order(5)
    public Result testAnnotatedDeleteRoute() {

        return Results.text().render("delete works.");

    }

    /*
     * MODE TEST ROUTES
     */
    @Path("/mode/test")
    @GET
    @Test
    public Result testAnnotatedTestRoute() {

        return Results.text().render("test mode works.");

    }

    @Path("/mode/dev")
    @GET
    @Dev
    public Result testAnnotatedDevRoute() {

        return Results.text().render("dev mode works.");

    }

    @Path("/mode/dev/and/test")
    @GET
    @Dev
    @Test
    public Result testAnnotatedDevAndTestRoute() {

        return Results.text().render("dev and test works.");

    }

    @Path("/mode/prod")
    @Prod
    public Result testAnnotatedProdRoute() {

        return Results.text().render("prod works.");

    }

    @Path("/mode/prod/and/test")
    @Prod
    @Test
    public Result testAnnotatedProdAndTestRoute() {

        return Results.text().render("prod and test works.");

    }

    @Path("/keyTest")
    @Requires("testkey")
    public Result testKeyedRoute() {

        return Results.text().render("keyed route works.");

    }

    @GET
    public Result testWithoutMethodPath() {
        return Results.text().render("route without method path works.");
    }

}
