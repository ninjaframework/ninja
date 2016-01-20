/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpHeaderUtilsTest {

    @Test
    public void testGetContentTypeFromContentTypeAndCharacterSetting() {

        assertEquals("application/json", HttpHeaderUtils.getContentTypeFromContentTypeAndCharacterSetting("application/json; charset=utf-8"));
        assertEquals("application/json", HttpHeaderUtils.getContentTypeFromContentTypeAndCharacterSetting("application/json"));

    }
    
    @Test
    public void testGetCharacterSetOfContentType() {
        
        assertEquals("utf-8", HttpHeaderUtils.getCharsetOfContentType("application/json; charset=utf-8", "TEST_ENCODING"));
        assertEquals("utf-8", HttpHeaderUtils.getCharsetOfContentType("application/json;charset=utf-8", "TEST_ENCODING"));
        assertEquals("TEST_ENCODING", HttpHeaderUtils.getCharsetOfContentType("application/json", "TEST_ENCODING"));
 
    }

    
    @Test
    public void testGetCharacterSetOfContentTypeOrUtf8() {
        
        assertEquals("TEST_ENCODING", HttpHeaderUtils.getCharsetOfContentType("application/json; charset=TEST_ENCODING", NinjaConstant.UTF_8));
        assertEquals("TEST_ENCODING", HttpHeaderUtils.getCharsetOfContentType("application/json;charset=TEST_ENCODING", "TEST_ENCODING"));
        assertEquals(NinjaConstant.UTF_8, HttpHeaderUtils.getCharsetOfContentType("application/json", NinjaConstant.UTF_8));

    }

}
