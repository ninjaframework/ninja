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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

class NinjaDiskFileItemStream implements NinjaFileItemStream {

    private final FileItemStream fileItemStream;
    private File file;


     public NinjaDiskFileItemStream(FileItemStream fileItemStream, File directory) {

        this.fileItemStream = fileItemStream;

        try (InputStream is = fileItemStream.openStream()) {

            this.file = File.createTempFile("ninja-upload-", null, directory);
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            throw new RuntimeException("Failed to read uploaded file", ex);
        }
    }

    @Override
    public InputStream openStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public String getContentType() {
        return fileItemStream.getContentType();
    }

    @Override
    public String getName() {
        return fileItemStream.getName();
    }

    @Override
    public String getFieldName() {
        return fileItemStream.getFieldName();
    }

    @Override
    public boolean isFormField() {
        return fileItemStream.isFormField();
    }

    @Override
    public FileItemHeaders getHeaders() {
        return fileItemStream.getHeaders();
    }

    @Override
    public void setHeaders(FileItemHeaders headers) {
        throw new UnsupportedOperationException(
                "You can not set headers for internally used file item stream");
    }

    @Override
    public void purge() {
        file.delete();
    }

}
