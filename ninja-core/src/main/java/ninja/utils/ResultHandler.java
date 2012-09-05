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

package ninja.utils;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ninja.AsyncResult;
import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;

/**
 * Handles the result
 * 
 * @author James Roper
 */
@Singleton
public class ResultHandler {

    private final TemplateEngineManager templateEngineManager;

    @Inject
    public ResultHandler(TemplateEngineManager templateEngineManager) {
        this.templateEngineManager = templateEngineManager;
    }

    public void handleResult(Result result, Context context) {

        if (result == null || result instanceof AsyncResult) {
            // Do nothing, assuming the controller manually handled it
            return;
        }

        Object object = result.getRenderable();
        String contentType = result.getContentType();

        if (object == null && contentType == null) {
            // Just finalize the headers, nothing else
            context.finalizeHeaders(result);
        } else if (object instanceof Renderable) {
            // if the object is a renderable it should do everything itself...:
            // make sure to call context.finalizeHeaders(result) with the
            // results
            // you want to set...
            handleRenderable((Renderable) object, context, result);
        } else {
            // if content type is not yet set in result we copy it over from the
            // request accept header
            if (result.getContentType() == null) {
                result.contentType(context.getAcceptContentType());
            }
            
            // If result does not contain a Cache-control: ... header
            // we disable caching of this response by calling doNotCacheContent().
            if (!result.getHeaders().containsKey(Result.CACHE_CONTROL)) {
                result.doNotCacheContent();
            }

            renderWithTemplateEngine(context, result);
        }
    }

    private void handleRenderable(Renderable renderable,
                                  Context context,
                                  Result result) {
        try {
            renderable.render(context, result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void renderWithTemplateEngine(Context context, Result result) {
        // try to get a suitable rendering engine...
        TemplateEngine templateEngine = templateEngineManager
                .getTemplateEngineForContentType(result.getContentType());

        if (templateEngine != null) {

            templateEngine.invoke(context, result);

        } else {

            if (result.getRenderable() instanceof String) {
                // Simply write it out
                try {
                    result.contentType(Result.TEXT_PLAIN);
                    ResponseStreams responseStreams = context
                            .finalizeHeaders(result);
                    responseStreams.getWriter().write(
                            (String) result.getRenderable());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else if (result.getRenderable() instanceof byte[]) {
                // Simply write it out
                try {
                    result.contentType(Result.APPLICATION_OCTET_STREAM);
                    ResponseStreams responseStreams = context
                            .finalizeHeaders(result);
                    responseStreams.getOutputStream().write(
                            (byte[]) result.getRenderable());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalArgumentException(
                        "No template engine found for result content type "
                                + result.getContentType());
            }
        }
    }

}
