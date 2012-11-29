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

package ninja;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ninja.utils.MimeTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This controller serves public resources under /public
 * 
 * @author ra
 * 
 */
@Singleton
public class AssetsController {

    private Logger logger = LoggerFactory.getLogger(AssetsController.class);

    /** Used as seen by http request */
    final String PUBLIC_PREFIX = "/assets/";

    private final MimeTypes mimeTypes;

    @Inject
    public AssetsController(MimeTypes mimeTypes) {
        this.mimeTypes = mimeTypes;

    }

    public Result serve(Context context) {
        Object renderable = new Renderable() {

            @Override
            public void render(Context context, Result result) {

                String finalName = context.getRequestPath().replaceFirst(
                        PUBLIC_PREFIX, "");

                InputStream inputStream = this.getClass().getClassLoader()
                        .getResourceAsStream(NinjaPaths.getAssets() + finalName);

                // check if stream exists. if not print a notfound exception
                if (inputStream == null) {

                    context.finalizeHeaders(Results.status(404));

                } else {
                    try {
                        result.status(200);

                        // try to set the mimetype:
                        String mimeType = mimeTypes.getContentType(context,
                                finalName);

                        if (!mimeType.isEmpty()) {
                            result.contentType(mimeType);
                        }

                        // finalize headers:
                        context.finalizeHeaders(result);

                        ByteStreams.copy(
                                this.getClass()
                                        .getClassLoader()
                                        .getResourceAsStream(
                                                NinjaPaths.getAssets() + finalName),
                                context.getHttpServletResponse()
                                        .getOutputStream());

                    } catch (FileNotFoundException e) {
                        logger.error("error streaming file", e);
                    } catch (IOException e) {
                        logger.error("error streaming file", e);
                    }

                }

            }
        };

        return Results.status(200).render(renderable);

    }

}
