/**
 * Copyright (C) 2012- the original author or authors.
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

package ninja.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests for JSONP render.
 */
public class TemplateEngineJsonPTest {

    Logger logger;
    NinjaProperties properties;
    Context context;
    ResponseStreams responseStreams;
    Result result;
    ObjectMapper objectMapper;
    ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws IOException {
        logger = mock(Logger.class);
        properties = mock(NinjaProperties.class);
        context = mock(Context.class);
        responseStreams = mock(ResponseStreams.class);
        result = Results.jsonp().render(Collections.singletonList(123));
        objectMapper = new ObjectMapper();
        outputStream = new ByteArrayOutputStream();
        when(properties.getWithDefault("ninja.jsonp.callbackParameter",
                TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_NAME)).thenReturn("callback");
        when(context.finalizeHeaders(result)).thenReturn(responseStreams);
        when(responseStreams.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void testCorrectFlow() throws IOException {
        when(context.getParameter("callback", TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_VALUE)).thenReturn("App.callback");

        TemplateEngineJsonP jsonpEngine = new TemplateEngineJsonP(objectMapper, properties);
        jsonpEngine.invoke(context, result);

        String jsonp = new String(outputStream.toByteArray(), "UTF-8");
        assertEquals("App.callback([123])", jsonp);
        verify(context).finalizeHeaders(result);
    }

    @Test
    public void testMissingCallbackVariableFlow() throws IOException {
        TemplateEngineJsonP jsonpEngine = new TemplateEngineJsonP(objectMapper, properties);
        jsonpEngine.invoke(context, result);

        String jsonp = new String(outputStream.toByteArray(), "UTF-8");
        assertEquals(TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_VALUE + "([123])", jsonp);
        verify(context).finalizeHeaders(result);
    }

    @Test
    public void testBadCallbackNameFlow() throws IOException {
        when(context.getParameter("callback", TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_VALUE)).thenReturn(".callback");

        TemplateEngineJsonP jsonpEngine = new TemplateEngineJsonP(objectMapper, properties);
        jsonpEngine.invoke(context, result);

        String jsonp = new String(outputStream.toByteArray(), "UTF-8");
        assertEquals(TemplateEngineJsonP.DEFAULT_CALLBACK_PARAMETER_VALUE + "([123])", jsonp);
        verify(context).finalizeHeaders(result);
    }
    
    @Test
    public void testIsThisASecureCallbackName() {
        assertTrue("simple function", TemplateEngineJsonP.isThisASecureCallbackName("onResponse"));
        assertTrue("object function", TemplateEngineJsonP.isThisASecureCallbackName("MyPath.path"));
        assertTrue("object function", TemplateEngineJsonP.isThisASecureCallbackName("MyApp.Path.myCallback123"));
        assertTrue("object function, path with numbers", 
                TemplateEngineJsonP.isThisASecureCallbackName("MyApp123.Path789.myCallback123"));
        assertTrue("complex path", TemplateEngineJsonP.isThisASecureCallbackName("Ext.data.JsonP.callback4"));
        assertTrue("complex path, $ in identity.", TemplateEngineJsonP.isThisASecureCallbackName("$42.ajaxHandler"));

        assertFalse("wrong first character", TemplateEngineJsonP.isThisASecureCallbackName("42$.q"));
        assertFalse("period in the front, simple", TemplateEngineJsonP.isThisASecureCallbackName(".onResponse"));
        assertFalse("period in the end, simple", TemplateEngineJsonP.isThisASecureCallbackName("onResponse."));
        assertFalse("period in the front, object function", TemplateEngineJsonP.isThisASecureCallbackName(".MyPath.path"));
        assertFalse("period in the end, complex path", TemplateEngineJsonP.isThisASecureCallbackName("MyPath.path.path2."));
        assertFalse("two subsequent periods", TemplateEngineJsonP.isThisASecureCallbackName("MyPath..path.path2"));
        assertFalse("function call", TemplateEngineJsonP.isThisASecureCallbackName("alert(document.cookie)"));
        
        // Cases not supported by the validator.
        assertFalse("simple array", TemplateEngineJsonP.isThisASecureCallbackName("somearray[12345]"));
        assertFalse("unicode characters", TemplateEngineJsonP.isThisASecureCallbackName("\\u0062oo"));
        assertFalse("unicode characters", TemplateEngineJsonP.isThisASecureCallbackName("\\u0020"));
    }
}
