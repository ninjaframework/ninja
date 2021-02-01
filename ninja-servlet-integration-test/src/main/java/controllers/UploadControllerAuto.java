/**
 * Copyright (C) 2012-2020 the original author or authors.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ninja.Context;
import ninja.Renderable;
import ninja.Result;
import ninja.Results;
import ninja.exceptions.InternalServerErrorException;
import ninja.i18n.Lang;
import ninja.params.Param;
import ninja.params.Params;
import ninja.uploads.DiskFileItemProvider;
import ninja.uploads.FileItem;
import ninja.uploads.FileProvider;
import ninja.utils.MimeTypes;
import ninja.utils.ResponseStreams;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.LinkedHashMap;
import java.util.Map;

import models.FormObject;
import models.FormWithFile;

@Singleton
public class UploadControllerAuto {

    /**
     * This is the system wide logger. You can still use any config you like. Or
     * create your own custom logger.
     * 
     * But often this is just a simple solution:
     */
    @Inject
    public Logger logger;

    /**
     * 
     * This upload method expects a file and simply displays the file in the
     * multipart upload again to the user (in the correct mime encoding).
     * 
     * @param context
     * @return
     * @throws Exception
     */
    @FileProvider(DiskFileItemProvider.class)
    public Result uploadFinishAuto(Context context,
            @Param("file") File file, @Params("file") File[] files,
            @Param("file") InputStream inputStream, @Params("file") InputStream[] inputStreams,
            @Param("file") FileItem fileItem, @Params("file") FileItem[] fileItems,
            @Param("file2") File file2
            ) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        
        // file
        sb.append("file\n").append(IOUtils.toString(new FileInputStream(file))).append("\n");
        // files
        for (File f : files) {
            sb.append("files\n").append(IOUtils.toString(new FileInputStream(f))).append("\n");
        }
        // file inputstream
        sb.append("inputstream\n").append(IOUtils.toString(inputStream)).append("\n");
        // files inputstream
        for (InputStream is : inputStreams) {
            sb.append("inputstreams\n").append(IOUtils.toString(is)).append("\n");
        }
        // file fileItem
        sb.append("fileitem\n").append(IOUtils.toString(fileItem.getInputStream())).append("\n");
        // file fileItems
        for (FileItem fi : fileItems) {
            sb.append("fileitems\n").append(IOUtils.toString(fi.getInputStream())).append("\n");
        }
        // context.getParameterAsFileItem
        sb.append("getParameterAsFileItem\n").append(IOUtils.toString(context.getParameterAsFileItem("file").getInputStream())).append("\n");
        // file2
        sb.append("file2\n").append(IOUtils.toString(new FileInputStream(file2))).append("\n");

        return Results.ok().renderRaw(sb.toString().getBytes());

    }
    
    /**
     * This upload method expects a form with one file and simply sends back the form as a json object,
     * with the fact that a file have been received too.
     */
    @FileProvider(DiskFileItemProvider.class)
    public Result postFormWithFile(
            Context context,
            FormWithFile formObject,
            @Param("a") Long valueA,
            @Param("b") String valueB,
            @Param("file") File file) {
        
        // use context to build a map of all request parameters
        Map<String,String> parameters = new LinkedHashMap<>();
        Map<String,String[]> allParams = context.getParameters();
        if (allParams != null) {
            allParams.forEach((k,v) -> {
                parameters.put(k, v[0]);
            });
        }
        
        formObject.fileReceived = file != null ? file.exists() : Boolean.FALSE;
        
        if (!parameters.isEmpty()) {
            formObject.parameters = parameters;
        }
        
        formObject.a = valueA;
        formObject.b = valueB;
        
        return Results.json().render(formObject);
    }

}
