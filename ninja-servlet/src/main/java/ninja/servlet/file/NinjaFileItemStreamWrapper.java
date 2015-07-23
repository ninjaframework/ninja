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

import ninja.Context;
import ninja.NinjaFileItemStream;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

/**
 * {@link FileItemStream} implementation for file items already saved in context
 * instance. Used to simulate file item iterator.
 *
 * <p>
 * This class should be removed when deprecated
 * {@link Context#getFileItemIterator()} method is removed.
 * </p>
 *
 */
@Deprecated
public class NinjaFileItemStreamWrapper implements FileItemStream {

    private final String fieldName;
    private String value;
    private NinjaFileItemStream itemStream;

    public NinjaFileItemStreamWrapper(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setItemStream(NinjaFileItemStream itemStream) {
        this.itemStream = itemStream;
    }

    @Override
    public InputStream openStream() throws IOException {
        if (itemStream != null)
            return itemStream.openStream();
        else
            return new ByteArrayInputStream(value.getBytes());
    }

    @Override
    public String getContentType() {
        return itemStream != null ? itemStream.getContentType() : null;
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
        return itemStream == null;
    }

    @Override
    public FileItemHeaders getHeaders() {
        throw new UnsupportedOperationException("Not supported for Ninja file item wrapper");
    }

    @Override
    public void setHeaders(FileItemHeaders headers) {
        throw new UnsupportedOperationException("Not supported for Ninja file item wrapper");
    }

}
