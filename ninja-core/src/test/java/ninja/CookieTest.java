/**
 * Copyright (C) 2012-2018 the original author or authors.
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

import org.junit.Test;

public class CookieTest {

    @Test
    public void testThatBuilderRejectsNullKeys() {
        boolean gotException = false;
        try {
            Cookie.builder(null, "");
        } catch (NullPointerException nullPointerException ){
            gotException = true;
        }
        assertTrue(gotException);
    }
    
    @Test
    public void testThatBuilderRejectsNullValues() {
        boolean gotException = false;
        
        try {
            Cookie.builder("", null);
        } catch (NullPointerException nullPointerException ){
            gotException = true;
        }
        
        assertTrue(gotException);
    }
    
    @Test
    public void testThatBuilderWorks() {
        
           Cookie cookie = Cookie.builder("key", "value").build();
           
           assertEquals("key", cookie.getName());
           assertEquals("value", cookie.getValue());
           assertEquals(-1, cookie.getMaxAge());
           assertEquals("/", cookie.getPath());
           
    }

}
