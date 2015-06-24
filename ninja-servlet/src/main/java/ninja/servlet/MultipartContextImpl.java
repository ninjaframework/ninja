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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineManager;
import ninja.servlet.file.FormFieldItemStream;
import ninja.servlet.file.NinjaFileItemStream;
import ninja.servlet.file.NinjaFileItemStreamFactory;
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

    @Inject
    NinjaFileItemStreamFactory fileItemStreamFactory;

    private final Map<String, List<NinjaFileItemStream>> fileItems = new HashMap<>();
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
    public void init(ServletContext servletContext,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {

        super.init(servletContext, httpServletRequest, httpServletResponse);

        // parse multipart request payload
        parseParts();
    }

    @Override
    public void cleanup() {
        for (List<NinjaFileItemStream> items : fileItems.values()) {
            for (NinjaFileItemStream item : items) {
                item.purge();
            }
        }
        super.cleanup();
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
    public FileItemIterator getFileItemIterator() {

        // streaming API of commons-upload allows us to iterate items of a
        // multipart request only once. Item iterator is used in init() method,
        // so we simulate iterator here by custom iterator implementation
        return new FileItemIteratorImpl(getFileItems(), multipartParams);
    }

    static class FileItemIteratorImpl implements FileItemIterator {

        private final List<FileItemStream> items;
        private int current = -1;

        public FileItemIteratorImpl(List<FileItemStream> fileItems, Map<String, List<String>> params) {

            // create list with uploaded file item streams
            this.items = new ArrayList<>(fileItems);

            // add form field params to the list
            for (Map.Entry<String, List<String>> e : params.entrySet()) {
                for (String value : e.getValue()) {
                    this.items.add(new FormFieldItemStream(e.getKey(), value));
                }
            }
        }

        @Override
        public boolean hasNext() throws FileUploadException, IOException {
            return current < items.size() - 1;
        }

        @Override
        public FileItemStream next() throws FileUploadException, IOException {
            return items.get(++current);
        }

    }

    @Override
    public InputStream getUploadedFileStream(String name) {
        List<NinjaFileItemStream> ls = fileItems.get(name);
        if (ls != null && ls.size() > 0) {
            try {
                return ls.get(0).openStream();
            } catch (IOException ex) {
                LOGGER.debug("Failed to open file stream", ex);
            }
        }
        return null;
    }

    @Override
    public List<InputStream> getUploadedFileStreams(String name) {
        List<NinjaFileItemStream> ls = fileItems.get(name);
        if (ls != null) {
            try {
                List<InputStream> result = new ArrayList<>();
                for (FileItemStream fis : ls) {
                    result.add(fis.openStream());
                }
                return result;
            } catch (IOException ex) {
                LOGGER.debug("Failed to open file stream", ex);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<FileItemStream> getFileItems() {
        List<FileItemStream> all = new LinkedList<>();
        for (List<NinjaFileItemStream> items : fileItems.values()) {
            all.addAll(items);
        }
        return all;
    }

    /**
     * Parses multipart contents of the request associated with this context.
     * Note that file item iterator of this context is passed to
     * {@link MultipartContextImpl#parseParts(org.apache.commons.fileupload.FileItemIterator)}
     * method.
     */
    void parseParts() {
        // note that we call getFileItemIterator() of super class, i.e. the real
        // file item iterator from common-upload
        FileItemIterator fileItemIterator = super.getFileItemIterator();
        if (fileItemIterator != null) {
            parseParts(fileItemIterator);
        }
    }

    /**
     * Multipart request payload parser. This method accepts file item iterator
     * and is not private to make testing easier by passing custom mock
     * iterator.
     *
     * @param fileItemIterator
     * @see MultipartContextImpl#parseParts()
     */
    void parseParts(FileItemIterator fileItemIterator) {
        try {
            while (fileItemIterator.hasNext()) {
                FileItemStream fileItemStream = fileItemIterator.next();
                String fieldName = fileItemStream.getFieldName();

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
                    List<String> ls = multipartParams.get(fieldName);
                    if (ls == null) {
                        ls = new ArrayList<>();
                        multipartParams.put(fieldName, ls);
                    }
                    ls.add(sb.toString());

                } else {
                    // an attached file
                    List<NinjaFileItemStream> items = fileItems.get(fieldName);
                    if (items == null) {
                        items = new ArrayList<>();
                        fileItems.put(fieldName, items);
                    }
                    items.add(fileItemStreamFactory.convert(fileItemStream));
                }
            }
        } catch (FileUploadException | IOException ex) {
            throw new RuntimeException("Failed to parse multipart request data", ex);
        }
    }

}
