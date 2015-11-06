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

package ninja.template;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import ninja.Context;
import ninja.Result;
import ninja.utils.ResponseStreams;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for application/json render.
 */
public class TemplateEngineJsonTest {

    Context context;
    ResponseStreams responseStreams;
    Result result;
    ObjectMapper objectMapper;
    ByteArrayOutputStream outputStream;

    @Before
    public void setUp() throws IOException {
        context = mock(Context.class);
        responseStreams = mock(ResponseStreams.class);
        result = mock(Result.class);

        objectMapper = new ObjectMapper();
        outputStream = new ByteArrayOutputStream();

        TestObject testObj = new TestObject();
        testObj.field1 = "field_one";
        testObj.field2 = "field_two";

        when(result.getRenderable()).thenReturn(testObj);
        when(context.finalizeHeaders(result)).thenReturn(responseStreams);
        when(responseStreams.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void testJsonViewWorks() throws IOException {
        Mockito.<Class<?>>when(result.getJsonView()).thenReturn(View.Public.class);

        TemplateEngineJson jsonEngine = new TemplateEngineJson(objectMapper);
        jsonEngine.invoke(context, result);

        String json = new String(outputStream.toByteArray(), "UTF-8");
        assertTrue(json.contains("field_one"));
        assertFalse(json.contains("field_two"));

        verify(context).finalizeHeaders(result);
    }


    private static class TestObject {

        @JsonView(View.Public.class)
        public String field1;

        @JsonView(View.Private.class)
        public String field2;
    }


    private static class View {
        public static class Public {}
        public static class Private {}
    }

}
