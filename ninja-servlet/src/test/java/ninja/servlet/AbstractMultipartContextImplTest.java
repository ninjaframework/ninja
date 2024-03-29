/**
 * Copyright (C) the original author or authors.
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

package ninja.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.Route;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.params.ParamParsers;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.uploads.FileItem;
import ninja.uploads.FileItemProvider;
import ninja.uploads.FileProvider;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
abstract class AbstractMultipartContextImplTest {

    @Mock
    private Session sessionCookie;

    @Mock
    private FlashScope flashCookie;

    @Mock
    private BodyParserEngineManager bodyParserEngineManager;

    @Mock
    private ServletContext servletContext;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private ResultHandler resultHandler;

    @Mock
    private Validation validation;

    private NinjaProperties ninjaProperties;

    private NinjaServletContext context;

    private final String paramA = "paramA";
    private final String paramB = "paramB";
    private final String valueA = "valueA";
    private final String valueB1 = "valueB1";
    private final String valueB2 = "valueB2";

    private final String file1 = "file1";
    private final String file2 = "file2";
    private final String file1Data = "abcdefghijklmnopqrstuvwxyz";

    protected final void init(final Class<? extends FileItemProvider> fileItemProviderClass) throws IOException, ServletException {
        //default setup for httpServlet request.
        //According to servlet spec the following will be returned:
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(httpServletRequest.getRequestURI()).thenReturn("/");
        when(httpServletRequest.getContentType()).thenReturn("multipart/form-data");
        when(httpServletRequest.getMethod()).thenReturn("POST");

        ninjaProperties = NinjaPropertiesImpl.builder().withMode(NinjaMode.test).build();
        
        FileItemIterator fileItemIterator = makeFileItemsIterator();
        
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NinjaProperties.class).toInstance(ninjaProperties);
                bind(FileItemProvider.class).to(fileItemProviderClass);
            }
        });

        context = new NinjaServletContext(
                bodyParserEngineManager,
                flashCookie,
                ninjaProperties,
                resultHandler,
                sessionCookie,
                validation,
                injector,
                new ParamParsers(new HashSet<>())
         )
        {
            public FileItemIterator getFileItemIterator() {
                return fileItemIterator;
            }
        };
    }
    
    @After
    public final void tearDown() {
        if (context != null) {
            context.cleanup();
        }
    }

    private FileItemIterator makeFileItemsIterator() {
        // ===== uploaded file items =====
        List<FileItemStream> fileItems = asList(
            new FileItemStreamImpl(file1, "text/plain", "my-file1.txt", file1Data.getBytes()),
            new FileItemStreamImpl(file2, "text/plain", "my-file2.txt", file1Data.getBytes()),
            new FileItemStreamImpl(file2, "text/plain", "my-file2.txt", "1234567890".getBytes())
        );

        // ===== simple key-value form fields =====
        Map<String, List<String>> params = new HashMap<>();
        params.put(paramA, asList(valueA));
        params.put(paramB, asList(valueB1, valueB2));

        return new FileItemIteratorImpl(fileItems, params);
    }
    
    @Test
    public void testGetParameter() {
        
        context.init(servletContext, httpServletRequest, httpServletResponse);

        Assert.assertEquals(valueA, context.getParameter(paramA));
        Assert.assertEquals(valueB1, context.getParameter(paramB));
        Assert.assertNull(context.getParameter("paramX"));
    }

    @Test
    public void testGetParameterValues() {
        
        context.init(servletContext, httpServletRequest, httpServletResponse);

        List<String> params = context.getParameterValues(paramA);
        Assert.assertEquals(1, params.size());
        Assert.assertTrue(params.contains(valueA));

        params = context.getParameterValues(paramB);
        Assert.assertEquals(2, params.size());
        Assert.assertTrue(params.contains(valueB1));
        Assert.assertTrue(params.contains(valueB2));
    }

    @Test
    public void testGetParameters() {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        Map<String, String[]> params = context.getParameters();

        Assert.assertEquals(2, params.size());

        String[] arr = params.get(paramA);
        Assert.assertNotNull(arr);
        Assert.assertEquals(1, arr.length);
        Assert.assertEquals(valueA, arr[0]);

        arr = params.get(paramB);
        Assert.assertNotNull(arr);
        Assert.assertEquals(2, arr.length);
        Assert.assertEquals(valueB1, arr[0]);
        Assert.assertEquals(valueB2, arr[1]);

    }

    @Test
    public void testIsMultipart() {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        Assert.assertTrue(context.isMultipart());
    }

    @Test
    public void testGetUploadedFileStream() throws IOException {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        InputStream is = context.getParameterAsFileItem(file1).getInputStream();
        Assert.assertNotNull(is);
        Assert.assertEquals(file1Data, IOUtils.toString(is, UTF_8));

        Assert.assertNotNull(context.getParameterAsFileItem(file2));
        Assert.assertNull(context.getParameterAsFileItem("fileX"));
    }

    @Test
    public void testGetUploadedFileStreams() {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        List<FileItem> files = context.getParameterAsFileItems(file1);
        Assert.assertEquals(1, files.size());

        // empty collection for nonexisting file
        files = context.getParameterAsFileItems("fileX");
        Assert.assertNotNull(files);
        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void testGetFileItems() {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        Assert.assertEquals(2, context.getParameterFileItems().size());
        int count = 0;
        for (List<FileItem> values : context.getParameterFileItems().values()) {
            count += values.size();
        }
        Assert.assertEquals(3, count);
    }
    
    @Test
    public void testControllerWithFileProviderAtClass() throws Exception {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        Method controllerMethod = ControllerImpl.class.getMethod("method1");
        Route route = new Route("GET", "/", controllerMethod, null);
        context.setRoute(route);

        Assert.assertEquals("test#1", context.getParameterAsFileItem(file1).getContentType());
        
    }

    @Test
    public void testControllerWithFileProviderAtMethod() throws Exception {

        Method controllerMethod = ControllerImpl.class.getMethod("method2");
        Route route = new Route("GET", "/", controllerMethod, null);
        context.setRoute(route);
        
        context.init(servletContext, httpServletRequest, httpServletResponse);

        Assert.assertEquals("test#2", context.getParameterAsFileItem(file1).getContentType());
        
    }


    /**
     * Simple file item stream impl to mock uploaded file items.
     */
    private static class FileItemStreamImpl implements FileItemStream {

        String fieldName;
        String contentType;
        String name;
        byte[] data;
        boolean isFormField;

        public FileItemStreamImpl(String fieldName, byte[] data) {
            this.fieldName = fieldName;
            this.contentType = null;
            this.name = null;
            this.data = data;
            this.isFormField = true;
        }

        public FileItemStreamImpl(String fieldName, String contentType, String name, byte[] data) {
            this.fieldName = fieldName;
            this.contentType = contentType;
            this.name = name;
            this.data = data;
            this.isFormField = false;
        }

        @Override
        public InputStream openStream() {
            return new ByteArrayInputStream(data);
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getFieldName() {
            return fieldName;
        }

        @Override
        public boolean isFormField() {
            return isFormField;
        }

        @Override
        public FileItemHeaders getHeaders() {
            return null;
        }

        @Override
        public void setHeaders(FileItemHeaders headers) {
        }

    }
    
    private static class FileItemIteratorImpl implements FileItemIterator {

        private final List<FileItemStream> items;
        private int current = -1;

        public FileItemIteratorImpl(List<FileItemStream> fileItems, Map<String, List<String>> params) {

            // create list with uploaded file item streams
            this.items = new ArrayList<>(fileItems);

            // add form field params to the list
            for (Map.Entry<String, List<String>> e : params.entrySet()) {
                for (String value : e.getValue()) {
                    this.items.add(new FileItemStreamImpl(e.getKey(), value.getBytes()));
                }
            }
        }

        @Override
        public boolean hasNext() {
            return current < items.size() - 1;
        }

        @Override
        public FileItemStream next() {
            return items.get(++current);
        }
        
    }
    
    @FileProvider(FileItemProviderImpl1.class)
    private static class ControllerImpl {
        public void method1() {
        }
        @FileProvider(FileItemProviderImpl2.class)
        public void method2() {
            method1();
        }
    }
    
}

class FileItemProviderImpl1 implements FileItemProvider {
    
    @Override
    public FileItem create(FileItemStream item) {
        return new FileItem() {
            @Override
            public String getFileName() {
                return null;
            }
            @Override
            public InputStream getInputStream() {
                return null;
            }
            @Override
            public File getFile() {
                return null;
            }
            @Override
            public String getContentType() {
                return "test#1";
            }
            @Override
            public FileItemHeaders getHeaders() {
                return null;
            }
            @Override
            public void cleanup() {
            }
        };
    }
    
}

class FileItemProviderImpl2 implements FileItemProvider {
    
    @Override
    public FileItem create(FileItemStream item) {
        return new FileItem() {
            @Override
            public String getFileName() {
                return null;
            }
            @Override
            public InputStream getInputStream() {
                return null;
            }
            @Override
            public File getFile() {
                return null;
            }
            @Override
            public String getContentType() {
                return "test#2";
            }
            @Override
            public FileItemHeaders getHeaders() {
                return null;
            }
            @Override
            public void cleanup() {
            }
        };
    }
    
}