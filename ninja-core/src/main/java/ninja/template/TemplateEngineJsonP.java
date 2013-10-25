/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package ninja.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Pattern;
import ninja.Context;
import ninja.Result;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.slf4j.Logger;

@Singleton
public class TemplateEngineJsonP implements TemplateEngine {

    static final String DEFAULT_CALLBACK_PARAMETER_NAME = "callback";

    static final String DEFAULT_CALLBACK_PARAMETER_VALUE = "onResponse";

    private static final Pattern CALLBACK_VALIDATION_REGEXP =
            Pattern.compile("^[a-zA-Z\\$_]+[a-zA-Z0-9\\$_\\.]?[a-zA-Z0-9\\$_]+$");

    private final Logger logger;

    private final ObjectMapper objectMapper;

    private final String callbackParameterName;

    @Inject
    public TemplateEngineJsonP(Logger logger, ObjectMapper objectMapper, NinjaProperties properties) {
        this.logger = logger;
        this.objectMapper = objectMapper;
        this.callbackParameterName = properties.getWithDefault("ninja.jsonp.callbackParameter",
                DEFAULT_CALLBACK_PARAMETER_NAME);
    }

    @Override
    public void invoke(Context context, Result result) {
        ResponseStreams responseStreams = context.finalizeHeaders(result);
        String callback = getCallbackName(context);
        try (OutputStream outputStream = responseStreams.getOutputStream()) {
            objectMapper.writeValue(outputStream, new JSONPObject(callback, result.getRenderable()));
        } catch (IOException e) {
            logger.error("Error while rendering jsonp.", e);
        }
    }

    @Override
    public String getContentType() {
        return Result.APPLICATON_JSONP;
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        // intentionally returns null...
        return null;
    }

    private String getCallbackName(Context context) {
        String callback = context.getParameter(this.callbackParameterName, DEFAULT_CALLBACK_PARAMETER_VALUE);
        if (callback != null && CALLBACK_VALIDATION_REGEXP.matcher(callback).matches()) {
            return callback;
        }
        return DEFAULT_CALLBACK_PARAMETER_VALUE;
    }
}
