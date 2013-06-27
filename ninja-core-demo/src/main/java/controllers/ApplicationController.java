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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Contact;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Router;
import ninja.cache.Cache;
import ninja.cache.NinjaCache;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.servlet.util.Request;
import ninja.servlet.util.Response;
import ninja.validation.Required;
import ninja.validation.Validation;

import org.slf4j.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
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
    
    @Inject
    Messages messages;

    @Inject
    Router router;
    
    @Inject
    NinjaCache ninjaCache;

    public Result examples(@Request HttpServletRequest httpServletRequest,
                           @Response HttpServletResponse httpServletResponse,
                           Context context) {
        logger.info("In example ");
        
        // test that the injected httpservlet request and response are not null
        Preconditions.checkNotNull(httpServletRequest);
        Preconditions.checkNotNull(httpServletResponse);
        
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
        
        String reverseRoute = router.getReverseRoute(ApplicationController.class, "userDashboard", map);

        map.put("reverseRoute", reverseRoute);
        
        // and render page with both parameters:
        return Results.html().render(map);
    }

    public Result validation(Validation validation,
                             @Param("email") @Required String email) {

        if (validation.hasViolations()) {
            return Results.json()
                    .render(validation.getFieldViolations("email"));
        } else {
            return Results.json().render(email);
        }
    }

    public Result redirect(Context context) {
        // Redirects back to the main page simply call redirect
        return Results.redirect("/");

    }

    public Result session(Context context) {
        // Sets the username "kevin" in the session-cookie
        context.getSessionCookie().put("username", "kevin");

        return Results.html();

    }

    public Result flashSuccess(Context context) {
        
        Result result = Results.html();
        
        // sets a 18n flash message and adds a timestamp to make sure formatting works
        Optional<String> flashMessage = messages.get("flashSuccess", context, Optional.of(result), "PLACEHOLDER");
        if (flashMessage.isPresent()) {
            context.getFlashCookie().success(flashMessage.get());
        }

        return result;

    }
    
    public Result flashError(Context context) {
        Result result = Results.html();
        // sets a 18n flash message and adds a timestamp to make sure formatting works
        Optional<String> flashMessage = messages.get("flashError", context, Optional.of(result), "PLACEHOLDER");
        if (flashMessage.isPresent()) {
            context.getFlashCookie().error(flashMessage.get());
        }

        return result;

    }
    
    public Result flashAny(Context context) {
        Result result = Results.html();
        // sets a 18n flash message and adds a timestamp to make sure formatting works
        Optional<String> flashMessage = messages.get("flashAny", context, Optional.of(result), "PLACEHOLDER");
        if (flashMessage.isPresent()) {
            context.getFlashCookie().put("any", flashMessage.get());
        }

        return result;

    }

    public Result contactForm(Context context) {

        return Results.html();

    }

    public Result postContactForm(Context context, Contact contact) {
        // contact is parsed into the method
        // and automatically gets rendered via the html
        // templating engine.
        return Results.html().render(contact);
    }

    public Result htmlEscaping(Context context) {

        // just an example of html escaping in action.
        // just visit /htmlEscaping and check out the source
        // all problematic characters will be escaped
        String maliciousJavascript = "<script>alert('Hello');</script>";

        return Results.html().render("maliciousJavascript", maliciousJavascript);

    }
    
    
    public Result testCaching() {
        
        // Simple integration test to check if ehcache works:
        // Calling that route two times will lead to different results.
        
        String cacheKeyObject = ninjaCache.get("key", String.class);
        
        if (cacheKeyObject != null) {
            
            String cacheKey = cacheKeyObject;
            
            return Results.html().render("cacheKey", cacheKey);
            
        } else {
            
            ninjaCache.set("key", "cacheKeyValue", "10s");
            return Results.html();
        }
        

    }

}
