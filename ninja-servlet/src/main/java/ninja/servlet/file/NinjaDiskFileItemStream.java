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
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import ninja.NinjaFileItemStream;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemStream;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;

class NinjaDiskFileItemStream implements NinjaFileItemStream {

    private File file;
    private String fieldName;
    private String contentType;

    @Inject
    public NinjaDiskFileItemStream(NinjaProperties properties) {

        String uploadDir = properties.get(NinjaConstant.FILE_UPLOADS_DIRECTORY);
        if (uploadDir == null) {
            uploadDir = System.getProperty("java.io.tmpdir");
        }

        File directory = new File(uploadDir);

        // check if specified target exists. create if does not exist,
        // otherwise check if it is a directory
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                throw new ProvisionException("Specified target for upload files in not a directory");
            }
        } else {
            directory.mkdirs();
        }

        // create temp file where uploaded file contents will be stored
        try {
            this.file = File.createTempFile("ninja-upload-", null, directory);
        } catch (IOException ex) {
            throw new ProvisionException("Failed to create a temp file", ex);
        }

    }

    @Override
    public void init(FileItemStream fileItemStream) {

        this.fieldName = fileItemStream.getFieldName();
        this.contentType = fileItemStream.getContentType();

        try (InputStream is = fileItemStream.openStream()) {

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
    public void copyTo(Path target) throws IOException {
        Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
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
        file.delete();
    }

}
