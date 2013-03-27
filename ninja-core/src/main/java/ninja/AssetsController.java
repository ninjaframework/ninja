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

package ninja;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.apache.commons.io.IOUtils;
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

    private static Logger logger = LoggerFactory
            .getLogger(AssetsController.class);

    /** Used as seen by http request */
    final String PUBLIC_PREFIX = "/assets/";

    /** Used for storing files locally */
    final String ASSETS_PREFIX = "assets/";

    private final MimeTypes mimeTypes;

    private HttpCacheToolkit httpCacheToolkit;

    @Inject
    public AssetsController(HttpCacheToolkit httpCacheToolkit,
                            MimeTypes mimeTypes) {
        
        this.httpCacheToolkit = httpCacheToolkit;
        this.mimeTypes = mimeTypes;

    }

    public Result serve(Context context) {
        Object renderable = new Renderable() {

            @Override
            public void render(Context context, Result result) {

                String finalName = context.getRequestPath().replaceFirst(
                        PUBLIC_PREFIX, "");

                URL url = this.getClass().getClassLoader()
                        .getResource(ASSETS_PREFIX + finalName);

                // check if stream exists. if not print a notfound exception
                if (url == null) {

                    context.finalizeHeaders(Results.notFound());

                } else {

                    try {

                        URLConnection urlConnection = url.openConnection();
                        Long lastModified = urlConnection.getLastModified();
                        httpCacheToolkit.addEtag(context, result, lastModified);

                        if (result.getStatusCode() == Result.SC_304_NOT_MODIFIED) {
                            // Do not stream anything out. Simply return 304
                            context.finalizeHeaders(result);
                            
                        } else {

                            result.status(200);

                            // Try to set the mimetype:
                            String mimeType = mimeTypes.getContentType(context,
                                    finalName);

                            if (!mimeType.isEmpty()) {
                                result.contentType(mimeType);
                            }

                            // finalize headers:
                            ResponseStreams responseStreams = context
                                    .finalizeHeaders(result);

                            InputStream inputStream = urlConnection
                                    .getInputStream();
                            OutputStream outputStream = responseStreams
                                    .getOutputStream();

                            ByteStreams.copy(inputStream, outputStream);

                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly(outputStream);

                        } 

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
