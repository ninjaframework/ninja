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
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

/**
 * {@link FileItemStream} implementation for form field parameters of a
 * multipart request. Used to simulate file item iterator - form fields are
 * represented by instances of this class.
 *
 */
public class FormFieldItemStream implements FileItemStream {

    private final String fieldName;
    private final String value;

    public FormFieldItemStream(String fieldName, String value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public InputStream openStream() throws IOException {
        return new ByteArrayInputStream(value.getBytes());
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public boolean isFormField() {
        return true;
    }

    @Override
    public FileItemHeaders getHeaders() {
        throw new UnsupportedOperationException("Not supported for custom form field items");
    }

    @Override
    public void setHeaders(FileItemHeaders headers) {
        throw new UnsupportedOperationException("Not supported for custom form field items");
    }

}
