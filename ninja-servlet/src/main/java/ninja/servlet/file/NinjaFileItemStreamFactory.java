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

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemStream;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;

/**
 * This is a factory class that produces {@link FileItemStream} instances that
 * can be stored and used repeatedly as opposed to instances returned by file
 * item iterator whose data can be read only once.
 *
 */
@Singleton
public class NinjaFileItemStreamFactory {

    @Inject
    private InMemoryFileItemFactory inMemoryFileItemFactory;

    private final boolean inMemory;
    private final File uploadDirectory;

    @Inject
    public NinjaFileItemStreamFactory(NinjaProperties properties) {

        inMemory = properties.getBooleanWithDefault(
                NinjaConstant.FILE_UPLOADS_IN_MEMORY, false);

        // get directory to save uploaded files, use system temp directory by default
        String dir = properties.getWithDefault(
                NinjaConstant.FILE_UPLOADS_DIRECTORY,
                System.getProperty("java.io.tmpdir"));

        uploadDirectory = new File(dir);

        // check if specified target exists. create if does not exist, otherwise check if it is a directory
        if (uploadDirectory.exists()) {
            if (!uploadDirectory.isDirectory()) {
                throw new ProvisionException("Specified target for upload files in not a directory");
            }
        } else {
            uploadDirectory.mkdirs();
        }
    }

    public boolean isInMemory() {
        return inMemory;
    }

    /**
     * Converts given {@link FileItemStream} instance to custom Ninja
     * implemented counterpart instance that can be used repeatedly as opposed
     * to instances that are returned by file item iterator of commons-upload's
     * streaming API.
     *
     * @param fileItemStream {@link FileItemStream} instance returned by file
     * item iterator
     * @return {@link NinjaFileItemStream} instances that can be stored for
     * repeated use
     */
    public NinjaFileItemStream convert(FileItemStream fileItemStream) {
        if (inMemory) {
            return new NinjaInMemoryFileItemStream(fileItemStream,
                    inMemoryFileItemFactory);
        } else {
            return new NinjaDiskFileItemStream(fileItemStream, uploadDirectory);
        }
    }
}
