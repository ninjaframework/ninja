/*
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * This is a {@link Context} implementation for multipart requests. Multipart
 * requests are most commonly used to send big files along with simple key-value
 * form parameters.
 *
 */
public class MultipartContextImpl extends ContextImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultipartContextImpl.class);

    private final Map<String, List<File>> files = new HashMap<>();
    private final Map<String, List<String>> multipartParams = new HashMap<>();

    @Inject
    public MultipartContextImpl(
            BodyParserEngineManager bodyParserEngineManager,
            FlashScope flashCookie,
            NinjaProperties ninjaProperties,
            ResultHandler resultHandler,
            Session sessionCookie,
            Validation validation) {
        super(bodyParserEngineManager, flashCookie, ninjaProperties, resultHandler, sessionCookie, validation);
    }

    @Override
    public void purgeFiles() {
        Collection<List<File>> filesCollection = files.values();
        for (List<File> ls : filesCollection) {
            for (File f : ls) {
                f.delete();
            }
        }
    }

    @Override
    public void init(ServletContext servletContext,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        super.init(servletContext, httpServletRequest, httpServletResponse);

        // parse multipart request payload
        parseParts();
    }

    @Override
    public String getParameter(String key) {
        List<String> params = multipartParams.get(key);
        if (params != null && !params.isEmpty()) {
            return params.get(0);
        }
        return super.getParameter(key);
    }

    @Override
    public List<String> getParameterValues(String name) {
        List<String> result = new ArrayList<>();

        List<String> params = super.getParameterValues(name);
        result.addAll(params);

        List<String> ls = multipartParams.get(name);
        if (ls != null) {
            result.addAll(ls);
        }

        return result;
    }

    @Override
    public Map<String, String[]> getParameters() {

        // create map with query params
        Map<String, String[]> map = new HashMap<>(super.getParameters());

        // add params of multipart request
        for (Entry<String, List<String>> e : multipartParams.entrySet()) {

            List<String> ls = new ArrayList<>(e.getValue());

            String[] values = map.get(e.getKey());
            if (values != null) {
                ls.addAll(Arrays.asList(values));
            }

            map.put(e.getKey(), ls.toArray(new String[ls.size()]));
        }
        return map;
    }

    @Override
    public boolean isMultipart() {
        return true;
    }

    @Override
    public File getUploadedFile(String name) {
        List<File> ls = files.get(name);
        return ls != null ? ls.get(0) : null;
    }

    @Override
    public List<File> getUploadedFiles(String name) {
        List<File> ls = files.get(name);
        if (ls == null) {
            return Collections.emptyList();
        }
        return ls;
    }

    private void parseParts() {
        FileItemIterator fileItemIterator = getFileItemIterator();
        if (fileItemIterator == null) {
            return;
        }
        try {
            while (fileItemIterator.hasNext()) {
                FileItemStream fileItemStream = fileItemIterator.next();
                String name = fileItemStream.getFieldName();

                if (fileItemStream.isFormField()) {

                    // simple key/value item
                    StringBuilder sb = new StringBuilder();
                    try (InputStreamReader isr = new InputStreamReader(fileItemStream.openStream())) {
                        int n;
                        char[] buf = new char[128];
                        while ((n = isr.read(buf)) > 0) {
                            sb.append(buf, 0, n);
                        }
                    }
                    List<String> ls = multipartParams.get(name);
                    if (ls == null) {
                        ls = new ArrayList<>();
                        multipartParams.put(name, ls);
                    }
                    ls.add(sb.toString());

                } else {
                    // an attached file
                    Path target = Files.createTempFile("ninja-upload", null);
                    try (InputStream is = fileItemStream.openStream()) {
                        Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
                    }

                    List<File> ls = files.get(name);
                    if (ls == null) {
                        ls = new ArrayList<>();
                        files.put(name, ls);
                    }
                    ls.add(target.toFile());
                }
            }
        } catch (FileUploadException | IOException ex) {
            LOGGER.debug("Failed to parse multipart request data", ex);
            throw new RuntimeException(ex);
        }
    }

}
