/**
 * Copyright (C) 2012-2014 the original author or authors.
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

public class SwissKnifeTest {

    @Test
    public void testGetRealClassNameLowerCamelCase() {

        MySuperTestObject mySuperTestObject = new MySuperTestObject();

        assertEquals("mySuperTestObject",
                SwissKnife.getRealClassNameLowerCamelCase(mySuperTestObject));

    }

    @Test
    public void testConvert() {
        
        String aString = "aString";
        String str = "1024";
        String doubleStr = "1024.24";
        String boolStr = "true";
        String byteStr = "89";
        char charStr = 'x';
        String emptyString = "";

        assertEquals(Integer.valueOf(str), SwissKnife.convert(str, int.class));
        assertEquals(Integer.valueOf(str), SwissKnife.convert(str, Integer.class));
        assertEquals(Long.valueOf(str), SwissKnife.convert(str, long.class));
        assertEquals(Float.valueOf(doubleStr), SwissKnife.convert(doubleStr, float.class));
        assertEquals(Float.valueOf(doubleStr), SwissKnife.convert(doubleStr, Float.class));
        assertEquals(Double.valueOf(doubleStr), SwissKnife.convert(doubleStr, double.class));
        assertEquals(Double.valueOf(doubleStr), SwissKnife.convert(doubleStr, Double.class));
        assertEquals(Boolean.valueOf(boolStr), SwissKnife.convert(boolStr, boolean.class));
        assertEquals(Boolean.valueOf(boolStr), SwissKnife.convert(boolStr, Boolean.class));
        assertEquals(Byte.valueOf(byteStr), SwissKnife.convert(byteStr, byte.class));
        assertEquals(Byte.valueOf(byteStr), SwissKnife.convert(byteStr, Byte.class));
        assertEquals(Character.valueOf(charStr), SwissKnife.convert(String.valueOf(charStr), char.class));
        assertEquals(Character.valueOf(charStr), SwissKnife.convert(String.valueOf(charStr), Character.class));
        assertEquals(null, SwissKnife.convert(String.valueOf(emptyString), char.class));
        assertEquals(null, SwissKnife.convert(String.valueOf(emptyString), Character.class));
        
        assertEquals(null, SwissKnife.convert(String.valueOf(aString), StringBuffer.class));
        assertEquals(null, SwissKnife.convert(String.valueOf(aString), Integer.class));
        
        assertEquals(aString, SwissKnife.convert(String.valueOf(aString), String.class));
    }

    // just for testing that camel case conversion stuff works
    public class MySuperTestObject {
    }

}
