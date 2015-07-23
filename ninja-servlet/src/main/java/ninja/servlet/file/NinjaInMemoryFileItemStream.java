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

import static ninja.servlet.file.InMemoryFileItemFactory.DEFAULT_MAX_FILE_SIZE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import ninja.NinjaFileItemStream;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

import com.google.inject.Inject;

class NinjaInMemoryFileItemStream implements NinjaFileItemStream {

    private final InMemoryFileOutputStream outputStream;
    private String fieldName;
    private String contentType;

    @Inject
    public NinjaInMemoryFileItemStream(NinjaProperties properties) {

        Integer limit = properties.getInteger(NinjaConstant.FILE_UPLOADS_MAX_FILE_SIZE);

        if (limit == null) {
            limit = DEFAULT_MAX_FILE_SIZE;
        }

        this.outputStream = new InMemoryFileOutputStream(limit);
    }

    @Override
    public void init(FileItemStream fileItemStream) {

        this.fieldName = fileItemStream.getFieldName();
        this.contentType = fileItemStream.getContentType();

        try (InputStream is = fileItemStream.openStream()) {

            Streams.copy(is, outputStream, true);

        } catch (IOException ex) {
            throw new RuntimeException("Failed to read uploaded file", ex);
        }
    }

    @Override
    public InputStream openStream() throws IOException {
        byte[] data = outputStream.getData();
        byte[] copy = Arrays.copyOf(data, data.length);
        return new ByteArrayInputStream(copy);
    }

    @Override
    public void copyTo(Path target) throws IOException {
        try (InputStream is = new ByteArrayInputStream(outputStream.getData())) {
            Files.copy(is, target);
        }
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void purge() {
        // do nothing for in-memory file item
    }

}
