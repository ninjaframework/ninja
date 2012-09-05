/**
 * Copyright (C) 2012 the original author or authors.
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.params.Param;
import ninja.params.PathParam;

import ninja.validation.Required;
import ninja.validation.Validation;
import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ApplicationController {

    /**
     * This is the system wide logger. You can still use any config you like. Or
     * create your own custom logger.
     * 
     * But often this is just a simple solution:
     */
    @Inject
    public Logger logger;

    @Inject
    Lang lang;

    public Result examples(Context context) {
        logger.info("In example ");
        // Default rendering is simple by convention
        // This renders the page in views/ApplicationController/index.ftl.html
        return Results.html();

    }
    
    public Result testPage() {
        return Results.html();

    }

    public Result index(Context context) {
        // Default rendering is simple by convention
        // This renders the page in views/ApplicationController/index.ftl.html
        return Results.html();

    }

    public Result userDashboard(@PathParam("email") String email,
                                @PathParam("id") Integer id,
                                Context context) {

        Map<String, Object> map = new HashMap<String, Object>();
        // generate tuples, convert integer to string here because Freemarker
        // does it in locale
        // dependent way with commas etc
        map.put("id", Integer.toString(id));
        map.put("email", email);

        // and render page with both parameters:
        return Results.html().render(map);
    }

    public Result validation(Validation validation,
                             @Param("email") @Required String email) {
        if (validation.hasViolations()) {
            return Results.contentType("text/plain").render(
                    validation.getFieldViolationMessage("email", "en"));
        } else {
            return Results.contentType("text/plain").render(email);
        }
    }

    public Result redirect(Context context) {
        // Redirects back to the main page simply call redirect
        return Results.redirect("/");

    }

    public Result session(Context context) {
        context.getSessionCookie().put("username", "kevin");

        return Results.html().render(context.getSessionCookie().getData());

    }

    public Result htmlEscaping(Context context) {

        // just an example of html escaping in action.
        // just visit /htmlEscaping and check out the source
        // all problematic characters will be escaped
        String maliciousJavascript = "<script>alert('Hello');</script>";

        Map<String, String> renderMap = Maps.newHashMap();
        renderMap.put("maliciousJavascript", maliciousJavascript);

        return Results.html(renderMap);

    }

}
