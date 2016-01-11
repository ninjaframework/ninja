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

package ninja.bodyparser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.Context;

import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class BodyParserEnginePostTest {
    
    @Mock
    Context context; 

    @Test
    public void testBodyParser() {

        // some setup for this method:
        String dateString = "2014-10-10";
        String dateTimeString = "2014-10-10T20:09:10";

        Map<String, String[]> map = new HashMap<>();
        map.put("integer", new String [] {"1000"});
        map.put("string", new String [] {"aString"});
        map.put("date", new String[]{dateString});
        map.put("timestamp", new String[]{dateTimeString});
        map.put("somethingElseWhatShouldBeSkipped", new String [] {"somethingElseWhatShouldBeSkipped"});

        Mockito.when(context.getParameters()).thenReturn(map);

        // do
        BodyParserEnginePost bodyParserEnginePost = new BodyParserEnginePost();       
        TestObject testObject = bodyParserEnginePost.invoke(context, TestObject.class);
        
        // and test:
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.integer, CoreMatchers.equalTo(1000));
        assertThat(testObject.date, CoreMatchers.equalTo(new LocalDateTime(dateString).toDate()));
        assertThat(testObject.timestamp, CoreMatchers.equalTo(new LocalDateTime(dateTimeString).toDate()));
        
    }
    
    @Test
    public void testBodyParserWhenThereIsAnUnsupportedFieldType() {
        
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("string", new String [] {"aString"});
        map.put("iAmNotSupportedField", new String [] {"iAmNotSupportedField"});
        map.put("longs", new String[] {"1", "2"});
        
        Mockito.when(context.getParameters()).thenReturn(map);

        // do
        BodyParserEnginePost bodyParserEnginePost = new BodyParserEnginePost();       
        TestObjectWithUnsupportedFields testObject = bodyParserEnginePost.invoke(context, TestObjectWithUnsupportedFields.class);
        
        // and test:
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.iAmNotSupportedField, CoreMatchers.equalTo(null));
        assertThat(testObject.longs, CoreMatchers.equalTo(null));
        
    }

    @Test
    public void testBodyParserWithCollectionAndArray() {

        Map<String, String[]> map = new HashMap<>();
        map.put("integers", new String[] {"1", "2"});
        map.put("strings", new String[] {"hello", "world"});

        Mockito.when(context.getParameters()).thenReturn(map);

        // do
        BodyParserEnginePost bodyParserEnginePost = new BodyParserEnginePost();
        TestObjectWithArraysAndCollections testObject = bodyParserEnginePost.invoke(context, TestObjectWithArraysAndCollections.class);

        // and test:
        assertThat(testObject.integers.length, equalTo(2));
        assertThat(testObject.integers[0], equalTo(1));
        assertThat(testObject.integers[1], equalTo(2));

        assertThat(testObject.strings.size(), equalTo(2));
        assertThat(testObject.strings.get(0), equalTo("hello"));
        assertThat(testObject.strings.get(1), equalTo("world"));
    }

    
    
    
    public static class TestObject {
    
        public int integer;
        public String string;
        public java.util.Date date;
        public java.util.Date timestamp;

    }
    
    public static class TestObjectWithUnsupportedFields {

        public StringBuffer iAmNotSupportedField;
        public String string;
        public long[] longs;

    }

    public static class TestObjectWithArraysAndCollections {

        public Integer[] integers;
        public List<String> strings;

    }
    
}
