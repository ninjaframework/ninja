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
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemStream;

/**
 * For testing purposes. Having this class helps to keep
 * {@link NinjaInMemoryFileItemStream} and {@link NinjaDiskFileItemStream}
 * classes package private.
 *
 */
public class NinjaFileItemStreamConverterMock extends NinjaFileItemStreamConverter {

    private NinjaProperties properties;

    public NinjaFileItemStreamConverterMock(NinjaProperties properties) {
        super(properties);
        this.properties = properties;
    }

    @Override
    public NinjaFileItemStream convert(FileItemStream fileItemStream) {
        NinjaFileItemStream item;
        if (isInMemory()) {
            item = new NinjaInMemoryFileItemStream(properties);
        } else {
            item = new NinjaDiskFileItemStream(properties);
        }
        item.init(fileItemStream);
        return item;
    }

}
