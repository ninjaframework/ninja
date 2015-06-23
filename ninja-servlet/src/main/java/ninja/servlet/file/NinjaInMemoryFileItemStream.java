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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

class NinjaInMemoryFileItemStream implements NinjaFileItemStream {

    private FileItem fileItem;

    public NinjaInMemoryFileItemStream(FileItemStream fileItemStream,
            InMemoryFileItemFactory inMemoryFileItemFactory) {

        this.fileItem = inMemoryFileItemFactory.createItem(
                fileItemStream.getFieldName(),
                fileItemStream.getContentType(),
                fileItemStream.isFormField(),
                fileItemStream.getName());

        try (InputStream is = fileItemStream.openStream()) {

            Streams.copy(is, fileItem.getOutputStream(), true);

        } catch (IOException ex) {
            throw new RuntimeException("Failed to read uploaded file", ex);
        }
    }

    @Override
    public InputStream openStream() throws IOException {
        return fileItem.getInputStream();
    }

    @Override
    public String getContentType() {
        return fileItem.getContentType();
    }

    @Override
    public String getName() {
        return fileItem.getName();
    }

    @Override
    public String getFieldName() {
        return fileItem.getFieldName();
    }

    @Override
    public boolean isFormField() {
        return fileItem.isFormField();
    }

    @Override
    public FileItemHeaders getHeaders() {
        return fileItem.getHeaders();
    }

    @Override
    public void setHeaders(FileItemHeaders headers) {
        throw new UnsupportedOperationException(
                "You can not set headers for internally used file item stream");
    }

    @Override
    public void purge() {
        fileItem.delete();
    }

}
