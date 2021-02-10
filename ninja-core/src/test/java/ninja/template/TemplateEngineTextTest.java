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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

import ninja.Context;
import ninja.Result;
import ninja.utils.ResponseStreams;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for text/plain render.
 */
public class TemplateEngineTextTest {

    Context context;
    ResponseStreams responseStreams;
    Result result;
    StringWriter writer;

    @Before
    public void setUp() throws IOException {
        context = mock(Context.class);
        responseStreams = mock(ResponseStreams.class);
        result = mock(Result.class);

        Map<String, String> map = new TreeMap<String, String>() {
            {
                put("apples", "oranges");
                put("cars", "trucks");
            }
        };

        when(result.getRenderable()).thenReturn(map);
        writer = new StringWriter();
        when(context.finalizeHeaders(result)).thenReturn(responseStreams);
        when(responseStreams.getWriter()).thenReturn(writer);
    }

    @Test
    public void testTextRendering() throws IOException {

        TemplateEngineText textEngine = new TemplateEngineText();
        textEngine.invoke(context, result);

        String text = writer.toString();
        assertEquals("{apples=oranges, cars=trucks}", text);
        verify(context).finalizeHeaders(result);
    }

}
