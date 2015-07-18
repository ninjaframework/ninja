/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.uploads;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link FileItemProvider} that save uploaded files on disk, in a temporary folder.
 * The default folder location is specified by the system property "java.io.tmpdir",
 * and can be changed using "uploads.temp_folder" ninja property.
 * <br><br>
 * Temporary files are automatically deleted at the end or the request.
 * 
 * @author Christian Bourgeois
 *
 */
@Singleton
public class DiskFileItemProvider implements FileItemProvider {
    
    private File tmpFolder;

    @Inject
    public DiskFileItemProvider(NinjaProperties ninjaProperties) {
        String tempName = ninjaProperties.get(NinjaConstant.UPLOADS_TEMP_FOLDER);
        if (tempName == null) {
            tempName = System.getProperty("java.io.tmpdir");
        }
        this.tmpFolder = new File(tempName);
        if (!tmpFolder.exists()) {
            tmpFolder.mkdirs();
        }
    }
    
    @Override
    public FileItem create(FileItemStream item) {
        
        File tmpFile = null;
        
        // do copy
        try (InputStream is = item.openStream()) {
            tmpFile = File.createTempFile("nju", null, tmpFolder);
            Files.copy(is, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary uploaded file on disk", e);
        }

        // return
        final String name = item.getName();
        final File file = tmpFile;
        final String contentType = item.getContentType();
        final FileItemHeaders headers = item.getHeaders();
        
        return new FileItem() {
            @Override
            public String getFileName() {
                return name;
            }
            @Override
            public InputStream getInputStream() {
                try {
                    return new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException("Failed to read temporary uploaded file from disk", e);
                }
            }
            @Override
            public File getFile() {
                return file;
            }
            @Override
            public String getContentType() {
                return contentType;
            }
            @Override
            public FileItemHeaders getHeaders() {
                return headers;
            }
            @Override
            public void cleanup() {
                // try to delete temporary file, silently fail on error
                try {
                    file.delete();
                } catch (Exception e) {
                }
            }
        };

    }

}
