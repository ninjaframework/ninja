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

import ninja.Context;
import ninja.Result;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.inject.Inject;

public class TemplateEngineJsonGson implements TemplateEngine {

    private final Logger logger;

    private final Gson gson;

    @Inject
    public TemplateEngineJsonGson(Logger logger) {
        this.logger = logger;
        this.gson = new Gson();
    }

    @Override
    public void invoke(Context context, Result result) {

        ResponseStreams responseStreams = context.finalizeHeaders(result);

        // Gson.toJson() for Strings will give an invalid JSON as per as per RFC
        // 4627. Hence they needs to be bypassed.
        String json = "";
        if (result.getRenderable() instanceof String) {
            json = (String) result.getRenderable();
        } else {
            json = gson.toJson(result.getRenderable());
        }

        try {
            responseStreams.getWriter().write(json);
            responseStreams.getWriter().flush();
            responseStreams.getWriter().close();

        } catch (IOException e) {
            logger.error("Error while writing out Gson Json", e);
        }

    }

    @Override
    public String getContentType() {
        return Result.APPLICATON_JSON;
    }

    @Override
    public String getSuffixOfTemplatingEngine() {
        // intentionally returns null...
        return null;
    }
}
