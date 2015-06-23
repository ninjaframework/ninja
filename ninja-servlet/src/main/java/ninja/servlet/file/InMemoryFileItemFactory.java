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

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link FileItemFactory} implementation that produces in-memory stored file
 * item instances.
 */
@Singleton
public class InMemoryFileItemFactory implements FileItemFactory {

    /**
     * Default max file size of 10 MB.
     */
    public static final int DEFAULT_MAX_FILE_SIZE = (1 << 20) * 10;

    private final int limit;

    @Inject
    public InMemoryFileItemFactory(NinjaProperties properties) {
        limit = properties.getIntegerWithDefault(
                NinjaConstant.FILE_UPLOADS_MAX_FILE_SIZE,
                DEFAULT_MAX_FILE_SIZE);
    }

    public int getMaxFileSize() {
        return limit;
    }

    @Override
    public FileItem createItem(String fieldName,
            String contentType,
            boolean isFormField,
            String fileName) {

        return new InMemoryFileItem(fieldName, contentType, isFormField, fileName, limit);

    }

}
