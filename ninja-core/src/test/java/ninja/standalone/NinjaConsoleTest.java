/**
 * Copyright (C) 2012-2019 the original author or authors.
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

package ninja.standalone;

import static org.hamcrest.CoreMatchers.containsString;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class NinjaConsoleTest {
    
    @Test
    public void ninjaPropertiesThrowsExceptionUntilConfigured() throws Exception {
        NinjaConsole console = new NinjaConsole();
        
        try {
            console.getNinjaProperties();
            fail("exception expected");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("configure() not called"));
        }
        
        console.configure();
        
        assertThat(console.getNinjaProperties(), is(not(nullValue())));
    }
    
    @Test
    public void start() throws Exception {
        NinjaConsole console = new NinjaConsole();

        try {
            console.getInjector();
            fail("exception expected");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("start() not called"));
        }
        
        console.start();
        try {
            assertThat(console.getInjector(), is(not(nullValue())));
        } finally {
            console.shutdown();
        }
    }
    
}