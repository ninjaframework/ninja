/*
 * Copyright 2014 ra.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.bodyparser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import ninja.Context;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;
import static org.junit.Assert.*;
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
        Map<String, String[]> map = new HashMap<>();
        map.put("integer", new String [] {"1000"});
        map.put("string", new String [] {"aString"});
        map.put("somethingElseWhatShouldBeSkipped", new String [] {"somethingElseWhatShouldBeSkipped"});
        
        Mockito.when(context.getParameters()).thenReturn(map);

        // do
        BodyParserEnginePost bodyParserEnginePost = new BodyParserEnginePost();       
        TestObject testObject = bodyParserEnginePost.invoke(context, TestObject.class);
        
        // and test:
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.integer, CoreMatchers.equalTo(1000));
        
    }
    
    @Test
    public void testBodyParserWhenThereIsAnUnsupportedFieldType() {
        
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("string", new String [] {"aString"});
        map.put("iAmNotSupportedField", new String [] {"iAmNotSupportedField"});
        
        Mockito.when(context.getParameters()).thenReturn(map);

        // do
        BodyParserEnginePost bodyParserEnginePost = new BodyParserEnginePost();       
        TestObjectWithUnsupportedFields testObject = bodyParserEnginePost.invoke(context, TestObjectWithUnsupportedFields.class);
        
        // and test:
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.iAmNotSupportedField, CoreMatchers.equalTo(null));
        
    }

    
    
    
    public static class TestObject {
    
        public int integer;
        public String string;
    
    }
    
    public static class TestObjectWithUnsupportedFields {

        public StringBuffer iAmNotSupportedField;
        public String string;

    }
    
}
