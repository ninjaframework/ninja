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

package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import ninja.utils.HttpCacheToolkit;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AssetsControllerTest {

    @Mock
    MimeTypes mimeTypes;

    @Mock
    HttpCacheToolkit httpCacheToolkit;

    @Mock
    Context contextRenderable;

    @Captor
    ArgumentCaptor<Result> resultCaptor;

    @Mock
    ResponseStreams responseStreams;
    
    @Mock 
    NinjaProperties ninjaProperties;
    
    AssetsController assetsController;
    
    @Before
    public void before() {
        assetsController = new AssetsController(
                httpCacheToolkit, mimeTypes, ninjaProperties);
    }
    
    
    
    @Test
    public void testServeStatic404() throws Exception {
        when(contextRenderable.getRequestPath()).thenReturn("notAvailable");
        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        Result result = Results.ok();

        renderable.render(contextRenderable, result);

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());

    }
    
    @Test
    public void testServeStaticSecurityClassesWithoutSlash() throws Exception {
        when(contextRenderable.getRequestPath()).thenReturn("ninja/Ninja.class");
        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        Result result = Results.ok();

        renderable.render(contextRenderable, result);

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());

    }
    
    @Test
    public void testServeStaticSecurityClassesAbsolute() throws Exception {

        when(contextRenderable.getRequestPath()).thenReturn("/ninja/Ninja.class");
        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        Result result = Results.ok();

        renderable.render(contextRenderable, result);

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());

    }
    
    @Test
    public void testServeStaticSecurityNoRelativPathWorks() throws Exception {
        //This theoretically could work as robots.txt is there..
        // But it should
        when(contextRenderable.getRequestPath()).thenReturn("/assets/../../conf/heroku.conf");
        
        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        Result result = Results.ok();

        renderable.render(contextRenderable, result);

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());
        assertEquals(Results.notFound().getStatusCode(), resultCaptor.getValue().getStatusCode());
    }

    @Test
    public void testServeStatic304NotModified() throws Exception {

        when(contextRenderable.getRequestPath()).thenReturn(
                "/assets/testasset.txt");

        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        Result result = Results.ok();
        // manually set to not modified => asset controller should
        // only finalize, but not stream
        result.status(Result.SC_304_NOT_MODIFIED);

        renderable.render(contextRenderable, result);
        // test streaming of resource:
        // => not modified:
        // check etag has been called
        verify(httpCacheToolkit).addEtag(Mockito.eq(contextRenderable),
                Mockito.eq(result), Mockito.anyLong());

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());

        // make sure we get the correct result...
        assertEquals(Result.SC_304_NOT_MODIFIED, resultCaptor.getValue()
                .getStatusCode());

    }
    
    @Test
    public void testServeStaticNormalOperationModifiedNoCaching()
            throws Exception {

        Result result = Results.ok();

        when(contextRenderable.getRequestPath()).thenReturn(
                "/assets/testasset.txt");

        when(mimeTypes.getContentType(Mockito.eq(contextRenderable),
                        Mockito.anyString())).thenReturn("mimetype");

        when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.eq(result))).thenReturn(
                responseStreams);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(responseStreams.getOutputStream()).thenReturn(
                byteArrayOutputStream);
        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        renderable.render(contextRenderable, result);
        // test streaming of resource:
        // => not modified:
        // check etag has been called
        verify(httpCacheToolkit).addEtag(Mockito.eq(contextRenderable),
                Mockito.eq(result), Mockito.anyLong());

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());

        // make sure we get the correct result...
        assertEquals(Result.SC_200_OK, resultCaptor.getValue().getStatusCode());
        // we mocked this one:
        assertEquals("mimetype", result.getContentType());

        // make sure the content is okay...
        assertEquals("testasset", byteArrayOutputStream.toString());

    }
    
    @Test
    public void testServeStaticRobotsTxt()
            throws Exception {

        Result result = Results.ok();

        when(contextRenderable.getRequestPath()).thenReturn(
                "/robots.txt");

        when(mimeTypes.getContentType(Mockito.eq(contextRenderable),
                        Mockito.anyString())).thenReturn("mimetype");

        when(contextRenderable.finalizeHeadersWithoutFlashAndSessionCookie(Mockito.eq(result))).thenReturn(
                responseStreams);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        when(responseStreams.getOutputStream()).thenReturn(
                byteArrayOutputStream);
        Result result2 = assetsController.serveStatic(null);

        Renderable renderable = (Renderable) result2.getRenderable();

        renderable.render(contextRenderable, result);
        // test streaming of resource:
        // => not modified:
        // check etag has been called
        verify(httpCacheToolkit).addEtag(Mockito.eq(contextRenderable),
                Mockito.eq(result), Mockito.anyLong());

        verify(contextRenderable).finalizeHeadersWithoutFlashAndSessionCookie(resultCaptor.capture());

        // make sure we get the correct result...
        assertEquals(Result.SC_200_OK, resultCaptor.getValue().getStatusCode());
        // we mocked this one:
        assertEquals("mimetype", result.getContentType());

        // make sure the content is okay but pay attention to system specific line separator
        String sysLineSeparator = System.lineSeparator();
        assertEquals("User-agent: *" + sysLineSeparator + "Disallow: /", byteArrayOutputStream.toString());

    }

    @Test
    public void testNormalizePathWithoutLeadingSlash() {
        assertEquals("dir1/test.test", assetsController.normalizePathWithoutLeadingSlash("/dir1/test.test", true));
        assertEquals("dir1/test.test", assetsController.normalizePathWithoutLeadingSlash("dir1/test.test", true));
        assertEquals(null, assetsController.normalizePathWithoutLeadingSlash("/../test.test", true));
        assertEquals(null, assetsController.normalizePathWithoutLeadingSlash("../test.test", true));
        assertEquals("dir2/file.test", assetsController.normalizePathWithoutLeadingSlash("/dir1/../dir2/file.test", true));
        assertEquals(null, assetsController.normalizePathWithoutLeadingSlash(null, true));
        assertEquals("", assetsController.normalizePathWithoutLeadingSlash("", true));
    }
}
