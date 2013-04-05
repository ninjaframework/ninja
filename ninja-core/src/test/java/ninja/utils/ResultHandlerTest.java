/**
 * Copyright (C) 2013 the original author or authors.
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

import java.io.OutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

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

@RunWith(MockitoJUnitRunner.class)
public class ResultHandlerTest {
    
    @Mock
    private TemplateEngineManager templateEngineManager;
    
    @Mock
    private TemplateEngine templateEngine;
    
    @Mock
    private ResponseStreams responseStreams;

	@Mock
	private OutputStream outputStream;
	
    @Mock
    private Writer writer;

    private ResultHandler resultHandler;
    
    @Mock 
    private Context context;
    
    
    @Before
    public void init() throws Exception {
        
        resultHandler = new ResultHandler(templateEngineManager);
		when(responseStreams.getOutputStream()).thenReturn(outputStream);
        when(responseStreams.getWriter()).thenReturn(writer);
        when(context.finalizeHeaders(any(Result.class))).thenReturn(responseStreams);
        when(templateEngineManager.getTemplateEngineForContentType(
                Result.APPLICATON_JSON)).thenReturn(templateEngine);
        
    }
    
    /**
     * If Cache-Control is not set the no-cache strategy has to be applied.
     * 
     * We expect
     * Cache-Control: ...
     * Date: ...
     * Expires: ...
     */
    @Test
    public void testAddingOfDefaultHeadersWorks() {
        
        Result result = Results.json();
        // just a new object as dummy...
        result.render(new Object());
        
        // make sure the stuff is not set by default json method (just in case...)       
        assertNull(result.getHeaders().get(Result.CACHE_CONTROL));
        assertNull(result.getHeaders().get(Result.DATE));
        assertNull(result.getHeaders().get(Result.EXPIRES));
        
        // handle result
        resultHandler.handleResult(result, context);
        
        // make sure stuff is there:
        assertEquals(Result.CACHE_CONTROL_DEFAULT_NOCACHE_VALUE, result.getHeaders().get(Result.CACHE_CONTROL));
        assertNotNull(result.getHeaders().get(Result.DATE));
        assertEquals(DateUtil.formatForHttpHeader(0L), result.getHeaders().get(Result.EXPIRES));
        
    }
    
    @Test
    public void testCacheControlDoesNotGetTouchedWhenSet() {
        
        Result result = Results.json();
        //just a simple cache control header:
        result.addHeader(Result.CACHE_CONTROL, "must-revalidate");
        // just a new object as dummy...
        result.render(new Object());
        
        // handle result
        resultHandler.handleResult(result, context);
        
        // make sure stuff is there:
        assertEquals("must-revalidate", result.getHeaders().get(Result.CACHE_CONTROL));
        assertNull(result.getHeaders().get(Result.DATE));
        assertNull(result.getHeaders().get(Result.EXPIRES));
    }

    @Test
    public void testRenderPlainStringAndSetDefaultContentType() {
        final String toRender = "this is just a plain string";
        Result result = Results.ok();
        result.render(toRender);
        resultHandler.handleResult(result, context);
        assertEquals(Result.TEXT_PLAIN, result.getContentType());
    }

    @Test
    public void testRenderPlainStringLeavesExplicitlySetContentTypeUntouched() {
        final String toRender = "this is just a plain string";
        final String contentType = "any/contenttype";
        Result result = Results.ok();
        result.contentType(contentType);
        result.render(toRender);
        resultHandler.handleResult(result, context);
        assertEquals(contentType, result.getContentType());
    }

	@Test
	public void testRenderPictureFromBytes() {
		final byte[] toRender = new byte[]{1,2,3};
        final String contentType = "image/png";
        Result result = Results.ok();
        result.contentType(contentType);
        result.render(toRender);
        resultHandler.handleResult(result, context);
        assertEquals(contentType, result.getContentType());
	}
}
