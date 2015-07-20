/**
 * Copyright (C) 2012-2015 the original author or authors.
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

import java.io.IOException;
import java.io.InputStream;

import ninja.Context;
import ninja.NinjaFileItemStream;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.utils.MimeTypes;
import ninja.utils.ResponseStreams;

import org.slf4j.Logger;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UploadController {

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

    private final MimeTypes mimeTypes;

    @Inject
    public UploadController(MimeTypes mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public Result upload() {
        // simply renders the default view for this controller
        return Results.html();
    }

    /**
     * 
     * This upload method expects a file and simply displays the file in the
     * multipart upload again to the user (in the correct mime encoding).
     * 
     * @param context
     * @return
     * @throws Exception
     */
    public Result uploadFinish(Context context) throws Exception {

        // we are using a renderable inner class to stream the input again to
        // the user
        Renderable renderable = new Renderable() {

            @Override
            public void render(Context context, Result result) {

                NinjaFileItemStream item = context.getUploadedFileStream("file");
                if (item != null) {
                    ResponseStreams responseStreams = context.finalizeHeaders(result);
                    try (InputStream stream = item.openStream()) {
                        ByteStreams.copy(stream, responseStreams.getOutputStream());
                    } catch (IOException ex) {
                        logger.error("Failed to read/write uploaded file", ex);
                    }
                } else {
                    logger.info("No uploaded file found");
                }
            }
        };

        return new Result(200).render(renderable);

    }

}
