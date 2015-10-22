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

import org.apache.commons.fileupload.FileItemStream;

import com.google.inject.ImplementedBy;

/**
 * This interface represents a file item provider, to create {@link FileItem} for each uploaded file.
 * 
 * The provider to use can be defined using the {@link FileProvider} on the controller's classes or methods.
 * 
 * @author Christian Bourgeois
 */
@ImplementedBy(NoFileItemProvider.class)
public interface FileItemProvider {
    
    FileItem create(FileItemStream item);

}
