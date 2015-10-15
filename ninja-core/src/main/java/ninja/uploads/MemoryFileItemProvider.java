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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

import com.google.common.io.ByteStreams;
import com.google.inject.Singleton;

/**
 * {@link FileItemProvider} that save uploaded files in memory.
 * 
 * @author Christian Bourgeois
 *
 */
@Singleton
public class MemoryFileItemProvider implements FileItemProvider {
    
    @Override
    public FileItem create(FileItemStream item) {
        
        // build output stream to get bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // do copy
        try {
            ByteStreams.copy(item.openStream(), outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temporary uploaded file in memory", e);
        }
        
        // return
        final String name = item.getName();
        final byte[] bytes = outputStream.toByteArray();
        final String contentType = item.getContentType();
        final FileItemHeaders headers = item.getHeaders();
        
        return new FileItem() {
            @Override
            public String getFileName() {
                return name;
            }
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(bytes);
            }
            @Override
            public File getFile() {
                throw new UnsupportedOperationException("Not supported in MemoryFileProvider");
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
            }
        };

    }

}
