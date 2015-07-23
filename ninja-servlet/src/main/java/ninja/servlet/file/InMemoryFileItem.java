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

package ninja.servlet.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.ParameterParser;
import org.apache.commons.fileupload.disk.DiskFileItem;

/**
 * In-memory implementation of {@link FileItem}. This is a custom implementation
 * to be used by "commons-upload" to make uploaded files be stored fully
 * in-memory instead of being partly stored in-memoery up to some threshold
 * value and then in file system.
 *
 */
public class InMemoryFileItem implements FileItem {

    private String fieldName;
    private String contentType;
    private boolean formField;
    private String fileName;

    private int limit;
    private InMemoryFileOutputStream out;

    private FileItemHeaders headers;


    public InMemoryFileItem(String fieldName,
            String contentType,
            boolean formField,
            String fileName,
            int limit) {
        this.fieldName = fieldName;
        this.contentType = contentType;
        this.formField = formField;
        this.fileName = fileName;
        this.limit = limit;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(out.getData());
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public long getSize() {
        return out.size();
    }

    @Override
    public byte[] get() {
        return out.getData();
    }

    @Override
    public String getString(String encoding) throws UnsupportedEncodingException {
        return new String(get(), encoding);
    }

    @Override
    public String getString() {

        // parse content charset passed by the agent; use default if not specified
        ParameterParser parser = new ParameterParser();
        parser.setLowerCaseNames(true);
        Map<String, String> params = parser.parse(getContentType(), ';');
        String charset = params.get("charset");
        if (charset == null) {
            charset = DiskFileItem.DEFAULT_CHARSET;
        }

        byte[] rawdata = get();
        try {
            return new String(rawdata, charset);
        } catch (UnsupportedEncodingException e) {
            return new String(rawdata);
        }
    }

    @Override
    public void write(File file) throws Exception {
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(get());
        }
    }

    @Override
    public void delete() {
        // no op for in-memory file item
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public void setFieldName(String name) {
        this.fieldName = name;
    }

    @Override
    public boolean isFormField() {
        return formField;
    }

    @Override
    public void setFormField(boolean state) {
        this.formField = state;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (out == null) {
            out = new InMemoryFileOutputStream(limit);
        }
        return out;
    }

    @Override
    public FileItemHeaders getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(FileItemHeaders headers) {
        this.headers = headers;
    }

}
