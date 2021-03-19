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

package ninja.template;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import ninja.Context;
import ninja.Result;
import ninja.utils.ResponseStreams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for application/json render.
 */
public class TemplateEngineJsonTest {

    private final Context context = mock(Context.class);
    private final ResponseStreams responseStreams = mock(ResponseStreams.class);
    private final Result result = mock(Result.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Before
    public final void setUp() throws IOException {
        TestObject testObj = new TestObject();
        testObj.field1 = "field_one";
        testObj.field2 = "field_two";

        when(result.getRenderable()).thenReturn(testObj);
        when(context.finalizeHeaders(result)).thenReturn(responseStreams);
        when(responseStreams.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void testJsonViewWorks() {
        Mockito.<Class<?>>when(result.getJsonView()).thenReturn(View.Public.class);

        TemplateEngineJson jsonEngine = new TemplateEngineJson(objectMapper);
        jsonEngine.invoke(context, result);

        String json = new String(outputStream.toByteArray(), UTF_8);
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
