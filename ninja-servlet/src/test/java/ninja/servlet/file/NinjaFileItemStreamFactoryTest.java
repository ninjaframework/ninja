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

import java.util.Arrays;
import java.util.Collection;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import org.apache.commons.fileupload.FileItemStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class NinjaFileItemStreamFactoryTest {

    private NinjaFileItemStreamFactory factory;

    @Parameterized.Parameter
    public boolean inMemory;

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{{false}, {true}});
    }

    @Before
    public void setUp() {
        NinjaPropertiesImpl properties = new NinjaPropertiesImpl(NinjaMode.test);
        properties.setProperty(NinjaConstant.FILE_UPLOADS_IN_MEMORY, Boolean.toString(inMemory));
        factory = new NinjaFileItemStreamFactory(properties);
        factory.inMemoryFileItemFactory = new InMemoryFileItemFactory(properties);
    }

    @Test
    public void testIsInMemory() {
        Assert.assertEquals(inMemory, factory.isInMemory());
    }

    @Test
    public void testConvert() {
        FileItemStream item = new FormFieldItemStream("name", "value");
        NinjaFileItemStream converted = factory.convert(item);

        if (inMemory) {
            Assert.assertTrue(converted instanceof NinjaInMemoryFileItemStream);
        } else {
            Assert.assertTrue(converted instanceof NinjaDiskFileItemStream);
        }
    }

}
