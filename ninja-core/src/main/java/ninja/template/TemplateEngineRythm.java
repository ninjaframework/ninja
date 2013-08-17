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

package ninja.template;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.exception.NinjaExceptionHandler;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.rythmengine.RythmEngine;
import org.rythmengine.template.ITemplate;
import org.slf4j.Logger;

import com.google.common.base.CaseFormat;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * 
 * Render Ninja with Rythm template engine (http://rythmengine.org).
 * 
 * @author sojin
 * 
 */

@Singleton
public class TemplateEngineRythm implements TemplateEngine {

    private final String FILE_SUFFIX = ".html";

    private final Messages messages;

    private final Lang lang;

    private final TemplateEngineHelper templateEngineHelper;

    private final NinjaExceptionHandler exceptionHandler;

    private final Logger logger;

    private final RythmEngine engine;

    @Inject
    public TemplateEngineRythm(Messages messages,
                               Lang lang,
                               Logger ninjaLogger,
                               NinjaExceptionHandler exceptionHandler,
                               TemplateEngineHelper templateEngineHelper,
                               TemplateEngineManager templateEngineManager,
                               NinjaProperties ninjaProperties,
                               RythmEngine engine) throws Exception {

        this.messages = messages;
        this.lang = lang;
        this.logger = ninjaLogger;
        this.exceptionHandler = exceptionHandler;
        this.templateEngineHelper = templateEngineHelper;
        this.engine = engine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Context context, Result result) {

        Object object = result.getRenderable();

        ResponseStreams responseStreams = context.finalizeHeaders(result);

        Map map;
        // if the object is null we simply render an empty map...
        if (object == null) {
            map = Maps.newHashMap();

        } else if (object instanceof Map) {
            map = (Map) object;

        } else {
            // We are getting an arbitrary Object and put that into
            // the root of rythm

            // If you are rendering something like Results.ok().render(new
            // MyObject())
            // Assume MyObject has a public String name field and
            // a getter getField()
            // You can then access the fields in the template like that:
            // @myObject.getField()
            // You will need to declare the object in the Template file, for eg:
            // @args package.MyObject myObject

            String realClassNameLowerCamelCase = CaseFormat.UPPER_CAMEL.to(
                    CaseFormat.LOWER_CAMEL, object.getClass().getSimpleName());

            map = Maps.newHashMap();
            map.put(realClassNameLowerCamelCase, object);

        }

        // set language from framework. You can access
        // it in the templates as @lang
        Optional<String> language = lang.getLanguage(context,
                Optional.of(result));
        Map<String, Object> renderArgs = new HashMap<String, Object>();
        if (language.isPresent()) {

            renderArgs.put("lang", language.get());
        }

        // put all entries of the session cookie to the map.
        // You can access the values by their key in the cookie
        // For eg: @session.get("key")
        if (!context.getSessionCookie().isEmpty()) {
            renderArgs.put("session", context.getSessionCookie().getData());
        }

        renderArgs.put("contextPath", context.getContextPath());

        // /////////////////////////////////////////////////////////////////////
        // Convenience method to translate possible flash scope keys.
        // !!! If you want to set messages with placeholders please do that
        // !!! in your controller. We only can set simple messages.
        // Eg. A message like "errorMessage=my name is: {0}" => translate in
        // controller and pass directly.
        // A message like " errorMessage=An error occurred" => use that as
        // errorMessage.
        //
        // get flash values like @flash.get("key")
        // ////////////////////////////////////////////////////////////////////

        Map<String, String> flash = new HashMap<String, String>();

        for (Entry<String, String> entry : context.getFlashCookie()
                .getCurrentFlashCookieData().entrySet()) {

            String messageValue = null;

            Optional<String> messageValueOptional = messages.get(
                    entry.getValue(), context, Optional.of(result));

            if (!messageValueOptional.isPresent()) {
                messageValue = entry.getValue();
            } else {
                messageValue = messageValueOptional.get();
            }

            flash.put(entry.getKey(), messageValue);
        }
        renderArgs.put("flash", flash);

        String templateName = templateEngineHelper.getRythmTemplateForResult(
                context.getRoute(), result, FILE_SUFFIX);

        // Specify the data source where the template files come from.
        // Here I set a file directory for it:

        try {
            PrintWriter writer = new PrintWriter(responseStreams.getWriter());

            if (language.isPresent()) {
                // RythmEngine uses ThreadLocal to set the locale, so this
                // setting per request is Thread safe.
                engine.prepare(new Locale(language.get()));
            }
            ITemplate t = engine.getTemplate(templateName, map);
            t.__setRenderArgs(renderArgs);
            String response = t.render();

            writer.println(response);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            ResponseStreams outStream = context.finalizeHeaders(Results
                    .internalServerError());
            String response = engine.getTemplate(
                    NinjaConstant.LOCATION_VIEW_HTML_INTERNAL_SERVER_ERROR)
                    .render();
            try {
                exceptionHandler.handleException(e, response,
                        outStream.getWriter());
            } catch (IOException ie) {
                logger.error(
                        "Error while getting response writer from Response Stream. ",
                        ie);
            }
        }
    }

    @Override
    public String getContentType() {
        return "text/html";
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        return FILE_SUFFIX;
    }
}
