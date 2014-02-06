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
import models.FormObject;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.Router;
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
import com.google.inject.Inject;
import com.google.inject.Singleton;

import ninja.session.FlashScope;
import ninja.session.Session;

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

    public Result index(@Request HttpServletRequest httpServletRequest,
                           @Response HttpServletResponse httpServletResponse,
                           Context context) {
        logger.info("In index ");
        
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

    public Result session(Session session) {
        // Sets the username "kevin" in the session-cookie
        session.put("username", "kevin");

        return Results.html();

    }

    public Result flashSuccess(FlashScope flashScope, Context context) {
        
        Result result = Results.html();
        
        // sets a 18n flash message and adds a timestamp to make sure formatting works
        Optional<String> flashMessage = messages.get("flashSuccess", context, Optional.of(result), "PLACEHOLDER");
        if (flashMessage.isPresent()) {
            flashScope.success(flashMessage.get());
        }

        return result;

    }
    
    public Result flashError(Context context, FlashScope flashScope) {
        Result result = Results.html();
        // sets a 18n flash message and adds a timestamp to make sure formatting works
        Optional<String> flashMessage = messages.get("flashError", context, Optional.of(result), "PLACEHOLDER");
        if (flashMessage.isPresent()) {
            flashScope.error(flashMessage.get());
        }

        return result;

    }
    
    public Result flashAny(Context context, FlashScope flashScope) {
        Result result = Results.html();
        // sets a 18n flash message and adds a timestamp to make sure formatting works
        Optional<String> flashMessage = messages.get("flashAny", context, Optional.of(result), "PLACEHOLDER");
        if (flashMessage.isPresent()) {
            flashScope.put("any", flashMessage.get());
        }

        return result;

    }

    public Result postForm(Context context, FormObject formObject) {
        // formObject is parsed into the method
        // and rendered as json
        return Results.json().render(formObject);
    }

    public Result directObjectTemplateRendering() {
        // Uses Results.html().render(Object) to directly 
        // render an object with a Freemarker template
        FormObject testObject = new FormObject();
        testObject.name = "test_name";
        testObject.primInt = 13579;
        testObject.setObjShort((short)-2954);
        
        return Results.html().render(testObject);
    }

    public Result htmlEscaping(Context context) {

        // just an example of html escaping in action.
        // just visit /htmlEscaping and check out the source
        // all problematic characters will be escaped
        String maliciousJavascript = "<script>alert('Hello! <>&\"'');</script>";

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

    public Result testJsonP() {
        return Results.jsonp().render("object", "value");
    }
}
