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

package ninja.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.io.Writer;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ResultHandlerTest {

    @Mock
    private TemplateEngineManager templateEngineManager;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private TemplateEngine templateEngineHtml;

    @Mock
    private ResponseStreams responseStreams;

    @Mock
    private OutputStream outputStream;

    @Mock
    private Writer writer;

    private ResultHandler resultHandler;

    @Mock
    private Context context;

    @Mock
    Logger logger;

    @Before
    public void init() throws Exception {

        resultHandler = new ResultHandler(logger, templateEngineManager);
        when(responseStreams.getOutputStream()).thenReturn(outputStream);
        when(responseStreams.getWriter()).thenReturn(writer);
        when(context.finalizeHeaders(any(Result.class)))
            .thenReturn(responseStreams);
        when(templateEngineManager
            .getTemplateEngineForContentType(Result.APPLICATION_JSON))
            .thenReturn(templateEngine);
        when(templateEngineManager.getTemplateEngineForContentType(Result.TEXT_HTML))
            .thenReturn(templateEngineHtml);

    }

    /**
     * If Cache-Control is not set the no-cache strategy has to be applied.
     * 
     * We expect Cache-Control: ... Date: ... Expires: ...
     */
    @Test
    public void testAddingOfDefaultHeadersWorks() {

        Result result = Results.json();
        // just a new object as dummy...
        result.render(new Object());

        // make sure the stuff is not set by default json method (just in
        // case...)
        assertNull(result.getHeaders().get(Result.CACHE_CONTROL));
        assertNull(result.getHeaders().get(Result.DATE));
        assertNull(result.getHeaders().get(Result.EXPIRES));

        // handle result
        resultHandler.handleResult(result, context);

        // make sure stuff is there:
        assertEquals(Result.CACHE_CONTROL_DEFAULT_NOCACHE_VALUE, result
                .getHeaders().get(Result.CACHE_CONTROL));
        assertNotNull(result.getHeaders().get(Result.DATE));
        assertEquals(DateUtil.formatForHttpHeader(0L),
                result.getHeaders().get(Result.EXPIRES));

    }

    @Test
    public void testCacheControlDoesNotGetTouchedWhenSet() {

        Result result = Results.json();
        // just a simple cache control header:
        result.addHeader(Result.CACHE_CONTROL, "must-revalidate");
        // just a new object as dummy...
        result.render(new Object());

        // handle result
        resultHandler.handleResult(result, context);

        // make sure stuff is there:
        assertEquals("must-revalidate", 
            result.getHeaders().get(Result.CACHE_CONTROL));
        assertNull(result.getHeaders().get(Result.DATE));
        assertNull(result.getHeaders().get(Result.EXPIRES));
    }

    @Test
    public void testRenderPlainStringAndSetDefaultContentType() {
        final String toRender = "this is just a plain string";
        Result result = Results.ok();
        result.renderRaw(toRender);
        resultHandler.handleResult(result, context);
        assertEquals(Result.TEXT_PLAIN, result.getContentType());
    }

    @Test
    public void testContentNegotiation() {
        when(context.getAcceptContentType()).thenReturn("text/html");
        Result result = Results.ok();
        resultHandler.handleResult(result, context);
        assertEquals("text/html", result.getContentType());
        verify(templateEngineHtml).invoke(context, result);
    }

    @Test
    public void testRenderPlainStringLeavesExplicitlySetContentTypeUntouched() {
        final String toRender = "this is just a plain string";
        final String contentType = "any/contenttype";
        Result result = Results.ok();
        result.contentType(contentType);
        result.renderRaw(toRender);
        resultHandler.handleResult(result, context);
        assertEquals(contentType, result.getContentType());
    }

    @Test
    public void testRenderPictureFromBytes() {
        final byte[] toRender = new byte[] { 1, 2, 3 };
        final String contentType = "image/png";
        Result result = Results.ok();
        result.contentType(contentType);
        result.renderRaw(toRender);
        resultHandler.handleResult(result, context);
        assertEquals(contentType, result.getContentType());
    }
    
    @Test
    public void testThatNoHttpBodyWorks() {
        
        // make sure that NoHttpBody causes the resulthandler to finalize
        // the context and does not call a tempate render engine.
        Result result = new Result(200);
        result.render(new NoHttpBody());
        
        resultHandler.handleResult(result, context);
        verify(context).finalizeHeaders(result);
        
    }
    
    @Test
    public void testThatFallbackContentTypeWorks() {
        Result result 
            = new Result(200)
                .fallbackContentType(Result.TEXT_HTML)
                .contentType(null);
        
        resultHandler.handleResult(result, context);
        
        assertThat(result.getContentType(), equalTo(Result.TEXT_HTML));
    }
}
