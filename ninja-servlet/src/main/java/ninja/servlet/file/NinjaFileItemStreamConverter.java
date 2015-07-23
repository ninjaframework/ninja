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

import ninja.NinjaFileItemStream;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemStream;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * This is a factory class that produces {@link FileItemStream} instances that
 * can be stored and used repeatedly as opposed to instances returned by file
 * item iterator whose data can be read only once.
 *
 */
@Singleton
public class NinjaFileItemStreamConverter {

    private final boolean inMemory;

    @Inject
    Provider<NinjaInMemoryFileItemStream> inMemoryFileItemStreamProvider;

    @Inject
    Provider<NinjaDiskFileItemStream> diskFileItemStreamProvider;

    @Inject
    NinjaFileItemStreamConverter(NinjaProperties properties) {

        Boolean b = properties.getBoolean(NinjaConstant.FILE_UPLOADS_IN_MEMORY);
        inMemory = b != null ? b.booleanValue() : false;

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
        NinjaFileItemStream item;
        if (inMemory) {
            item = inMemoryFileItemStreamProvider.get();
        } else {
            item = diskFileItemStreamProvider.get();
        }
        item.init(fileItemStream);
        return item;
    }
}
