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
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemHeaders;

/**
 * This interface represents a file or form item that was received within a
 * <code>multipart/form-data</code> POST request.
 * 
 * @author Christian Bourgeois
 */

public interface FileItem {
    
    /**
     * Returns the original filename in the client's filesystem, as provided by
     * the browser (or other client software). In most cases, this will be the
     * base file name, without path information. However, some clients, such as
     * the Opera browser, do include path information.
     */
    String getFileName();
    
    /**
     * Returns an {@link java.io.InputStream InputStream} that can be
     * used to retrieve the contents of the file.
     */
    InputStream getInputStream();
    
    /**
     * Returns an {@link File} that can be used to retrieve the contents of the file.
     */
    File getFile();
    
    /**
     * Returns the content type passed by the browser or <code>null</code> if
     * not defined.
     */
    String getContentType();
    
    /**
     * Returns the file item headers.
     */
    FileItemHeaders getHeaders();
    
    /**
     * Cleanup resources if needed.
     */
    void cleanup();
    
}
