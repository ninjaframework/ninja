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

package ninja;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.apache.commons.fileupload.FileItemStream;

/**
 * This interface represents uploaded file of a multipart request. Uploaded
 * files injected into controller methods are of this type.
 *
 */
public interface NinjaFileItemStream {

    /**
     * Initializes this Ninja file item stream with given file item stream.
     *
     * @param fileItemStream file item stream to use values of
     */
    void init(FileItemStream fileItemStream);

    /**
     * Gets an {@link InputStream} that allows to read file contents.
     *
     * @return input stream from which file contents can be read
     * @throws IOException
     */
    InputStream openStream() throws IOException;

    /**
     * Copies contents of this uploaded file to specified target location.
     *
     * @param target file to copy contents to
     * @throws IOException
     */
    void copyTo(Path target) throws IOException;

    /**
     * Gets the name of the field in the multipart form corresponding to this
     * file item.
     *
     * @return The name of the form field.
     */
    String getFieldName();

    /**
     * Returns the content type passed by the browser.
     *
     * @return The content type passed by the browser or {@code null} if not
     * defined.
     */
    String getContentType();

    /**
     * Purges underlying resources of this file item. For file system backed
     * file items this method deletes temporary file where uploaded file
     * contents were saved.
     */
    void purge();

}
