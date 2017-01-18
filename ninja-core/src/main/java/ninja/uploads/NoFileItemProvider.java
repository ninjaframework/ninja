/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import org.apache.commons.fileupload.FileItemStream;

/**
 * {@link FileItemProvider} default's implementation, to indicate to not handle uploaded files, and
 * let the users deal with the request by themselves
 * 
 * @author Christian Bourgeois
 */
public class NoFileItemProvider implements FileItemProvider {

    @Override
    public FileItem create(FileItemStream item) {
        throw new UnsupportedOperationException("Not supported in NoFileItemProvider");
    }
    
}