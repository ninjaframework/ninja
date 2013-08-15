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

package ninja.exception;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import ninja.utils.NinjaProperties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NinjaExceptionHandlerTest {

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Logger logger;

    @Test
    public void testHandleException() throws IOException {
        NinjaExceptionHandler handler = new NinjaExceptionHandler(logger,
                ninjaProperties);

        Exception te = new Exception("Test Exception");
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        when(ninjaProperties.isProd()).thenReturn(true);

        handler.handleException(te, "response", new PrintWriter(out));
        assertEquals("response", out.toString());
        
        
        when(ninjaProperties.isProd()).thenReturn(false);
        handler.handleException(te, "response", new PrintWriter(out));

        Assert.assertTrue(out.toString().contains("Rythm Template ERROR MESSAGE STARTS HERE"));
        Assert.assertTrue(out.toString().contains("response"));

    }
}
