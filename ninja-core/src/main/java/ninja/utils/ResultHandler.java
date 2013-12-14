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

package ninja.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final Logger logger;

    @Inject
    public ResultHandler(Logger logger, TemplateEngineManager templateEngineManager) {
        this.logger = logger;
        this.templateEngineManager = templateEngineManager;
    }

    public void handleResult(Result result, Context context) {

        if (result == null || result instanceof AsyncResult) {
            // Do nothing, assuming the controller manually handled it
            return;
        }
        
        Object objectToBeRendered = result.getRenderable();

        if (objectToBeRendered instanceof Renderable) {
            // if the object is a renderable it should do everything itself...:
            // make sure to call context.finalizeHeaders(result) with the
            // results you want to set...
            handleRenderable((Renderable) objectToBeRendered, context, result);
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
            
            if (objectToBeRendered instanceof NoHttpBody) {
                // This indicates that we do not want to render anything in the body.
                // Can be used e.g. for a 204 No Content response.
                // and bypasses the rendering engines.
                context.finalizeHeaders(result);
                
            } else {
                // normal mode of operation: we render the stuff via the
                // template renderer:
                renderWithTemplateEngineOrRaw(context, result);
            }
        }
    }

    private void handleRenderable(Renderable renderable,
                                  Context context,
                                  Result result) {
        try {
            renderable.render(context, result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error while handling renderable", e);
        }
    }

    private void renderWithTemplateEngineOrRaw(Context context, Result result) {
        // try to get a suitable rendering engine...
        TemplateEngine templateEngine = templateEngineManager
                .getTemplateEngineForContentType(result.getContentType());

        if (templateEngine != null) {

            templateEngine.invoke(context, result);

        } else {

            if (result.getRenderable() instanceof String) {
                
                // Simply write it out
                
                if (result.getContentType() == null) {
                    // if content type not explicitly set, text/plain is a good default value:
                    result.contentType(Result.TEXT_PLAIN);
                }
                
                ResponseStreams responseStreams = context
                    .finalizeHeaders(result);

                try (Writer writer = responseStreams.getWriter()) {
                    
                    writer.append((String) result.getRenderable());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else if (result.getRenderable() instanceof byte[]) {
                
                // If content type not explicitly set, application/octet-stream 
                // is a good default value:
                if (result.getContentType() == null) {
                    result.contentType(Result.APPLICATION_OCTET_STREAM);
                }
                
                ResponseStreams responseStreams = context
                        .finalizeHeaders(result);
                
                try (OutputStream outputStream = responseStreams.getOutputStream()) {

                    outputStream.write((byte[]) result.getRenderable());
                    
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                
            } else {
                
                context.finalizeHeaders(result);
                
                throw new IllegalArgumentException(
                        "No template engine found for result content type "
                                + result.getContentType());
            }
        }
    }

}
