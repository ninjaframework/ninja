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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import ninja.NinjaFileItemStream;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.inject.Provider;

@RunWith(Parameterized.class)
public class NinjaFileItemStreamConverterTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Mock
    private NinjaProperties properties;

    @Mock
    private Provider<NinjaInMemoryFileItemStream> inMemoryFileItemStreamProvider;

    @Mock
    private Provider<NinjaDiskFileItemStream> diskFileItemStreamProvider;

    private NinjaFileItemStreamConverter factory;

    @Parameterized.Parameter
    public boolean inMemory;

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{{false}, {true}});
    }

    @Before
    public void setUp() throws IOException {

        MockitoAnnotations.initMocks(this);

        Mockito.when(properties.isTest()).thenReturn(true);
        Mockito.when(properties.getBoolean(NinjaConstant.FILE_UPLOADS_IN_MEMORY))
                .thenReturn(inMemory);
        Mockito.when(properties.getInteger(NinjaConstant.FILE_UPLOADS_MAX_FILE_SIZE))
                .thenReturn(null);
        Mockito.when(properties.get(NinjaConstant.FILE_UPLOADS_DIRECTORY))
                .thenReturn(temp.newFolder().getPath());

        Mockito.when(inMemoryFileItemStreamProvider.get()).thenAnswer(
                new Answer<NinjaInMemoryFileItemStream>() {

                    @Override
                    public NinjaInMemoryFileItemStream answer(InvocationOnMock invocation) throws Throwable {
                        return new NinjaInMemoryFileItemStream(properties);
                    }
                });

        Mockito.when(diskFileItemStreamProvider.get()).thenAnswer(
                new Answer<NinjaDiskFileItemStream>() {

                    @Override
                    public NinjaDiskFileItemStream answer(InvocationOnMock invocation) throws Throwable {
                        return new NinjaDiskFileItemStream(properties);
                    }
                });

        factory = new NinjaFileItemStreamConverter(properties);
        factory.diskFileItemStreamProvider = diskFileItemStreamProvider;
        factory.inMemoryFileItemStreamProvider = inMemoryFileItemStreamProvider;
    }

    @Test
    public void testIsInMemory() {
        Assert.assertEquals(inMemory, factory.isInMemory());
    }

    @Test
    public void testConvert() {

        FileItemStream item = new FileItemStream() {

            String data = "abcdefghijklmnopqrstuvwxyz";

            @Override
            public InputStream openStream() throws IOException {
                return new ByteArrayInputStream(data.getBytes());
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public String getName() {
                return "file.txt";
            }

            @Override
            public String getFieldName() {
                return "name";
            }

            @Override
            public boolean isFormField() {
                return false;
            }

            @Override
            public FileItemHeaders getHeaders() {
                throw new UnsupportedOperationException("Not supported in tests");
            }

            @Override
            public void setHeaders(FileItemHeaders headers) {
                throw new UnsupportedOperationException("Not supported in tests");
            }
        };

        NinjaFileItemStream converted = factory.convert(item);

        if (inMemory) {
            Assert.assertTrue(converted instanceof NinjaInMemoryFileItemStream);
        } else {
            Assert.assertTrue(converted instanceof NinjaDiskFileItemStream);
        }
    }

}
