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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import ninja.Context;
import ninja.params.ParamParsers;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import ninja.params.ControllerMethodInvokerTest.Dep;
import ninja.params.ControllerMethodInvokerTest.NeedingInjectionParamParser;
import ninja.params.ParamParser;
import ninja.validation.FieldViolation;
import ninja.validation.IsDate;
import ninja.validation.IsFloat;
import ninja.validation.IsInteger;
import ninja.validation.Validation;
import ninja.validation.ValidationImpl;

import org.hamcrest.CoreMatchers;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;

/**
 *
 * @author ra
 */
@RunWith(MockitoJUnitRunner.class)
public class BodyParserEnginePostTest {
    
    @Mock
    Context context;
    
    Validation validation;
    
    BodyParserEnginePost bodyParserEnginePost; 
    
    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NinjaProperties.class).toInstance(new NinjaPropertiesImpl(NinjaMode.test));
                
                Multibinder<ParamParser> parsersBinder = Multibinder.newSetBinder(binder(), ParamParser.class);
                parsersBinder.addBinding().to(NeedingInjectionParamParser.class);
            }
        });
        
        validation = new ValidationImpl();
        Mockito.when(this.context.getValidation()).thenReturn(this.validation);
        
        bodyParserEnginePost = injector.getInstance(BodyParserEnginePost.class);
    }

    @Test
    public void testBodyParser() {

        // some setup for this method:
        String dateString = "2014-10-10";
        String dateTimeString = "2014-10-10T20:09:10";

        Map<String, String[]> map = new HashMap<>();
        map.put("integerPrimitive", new String [] {"1000"});
        map.put("integerObject", new String [] {"2000"});
        map.put("longPrimitive", new String [] {"3000"});
        map.put("longObject", new String [] {"4000"});
        map.put("floatPrimitive", new String [] {"1.234"});
        map.put("floatObject", new String [] {"2.345"});
        map.put("doublePrimitive", new String [] {"3.456"});
        map.put("doubleObject", new String [] {"4.567"});
        map.put("string", new String [] {"aString"});
        map.put("characterPrimitive", new String [] {"a"});
        map.put("characterObject", new String [] {"b"});
        map.put("date", new String[]{dateString});
        map.put("timestamp", new String[]{dateTimeString});
        map.put("somethingElseWhatShouldBeSkipped", new String [] {"somethingElseWhatShouldBeSkipped"});

        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);

        // do
        TestObject testObject = bodyParserEnginePost.invoke(context, TestObject.class);
        
        // and test:
        assertThat(testObject.integerPrimitive, CoreMatchers.equalTo(1000));
        assertThat(testObject.integerObject, CoreMatchers.equalTo(2000));
        assertThat(testObject.longPrimitive, CoreMatchers.equalTo(3000L));
        assertThat(testObject.longObject, CoreMatchers.equalTo(4000L));
        assertThat(testObject.floatPrimitive, CoreMatchers.equalTo(1.234F));
        assertThat(testObject.floatObject, CoreMatchers.equalTo(2.345F));
        assertThat(testObject.doublePrimitive, CoreMatchers.equalTo(3.456D));
        assertThat(testObject.doubleObject, CoreMatchers.equalTo(4.567D));
        assertThat(testObject.string, equalTo("aString"));
        assertThat(testObject.characterPrimitive, equalTo('a'));
        assertThat(testObject.characterObject, equalTo('b'));
        assertThat(testObject.date, CoreMatchers.equalTo(new LocalDateTime(dateString).toDate()));
        assertThat(testObject.timestamp, CoreMatchers.equalTo(new LocalDateTime(dateTimeString).toDate()));
        
        assertFalse(validation.hasViolations());
        
    }
    
    @Test
    public void testBodyParserWithValidationErrors() {

        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("integerPrimitive", new String [] {"a"});
        map.put("integerObject", new String [] {"b"});
        map.put("longPrimitive", new String [] {"c"});
        map.put("longObject", new String [] {"d"});
        map.put("floatPrimitive", new String [] {"e"});
        map.put("floatObject", new String [] {"f"});
        map.put("doublePrimitive", new String [] {"g"});
        map.put("doubleObject", new String [] {"h"});
        map.put("characterPrimitive", new String [] {null});
        map.put("characterObject", new String [] {null});
        map.put("date", new String[]{"cc"});
        map.put("timestamp", new String[]{"dd"});
        map.put("somethingElseWhatShouldBeSkipped", new String [] {"somethingElseWhatShouldBeSkipped"});

        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);

        // do
        TestObject testObject = bodyParserEnginePost.invoke(context, TestObject.class);
        
        // and test:
        assertTrue(validation.hasViolations());
        assertViolation("integerPrimitive", IsInteger.KEY);
        assertThat(testObject.integerPrimitive, equalTo(0));
        assertViolation("integerObject", IsInteger.KEY);
        assertNull(testObject.integerObject);
        assertViolation("longPrimitive", IsInteger.KEY);
        assertThat(testObject.longPrimitive, equalTo(0L));
        assertViolation("longObject", IsInteger.KEY);
        assertNull(testObject.longObject);
        assertViolation("floatPrimitive", IsFloat.KEY);
        assertThat(testObject.floatPrimitive, equalTo(0F));
        assertViolation("floatObject", IsFloat.KEY);
        assertNull(testObject.floatObject);
        assertViolation("doublePrimitive", IsFloat.KEY);
        assertThat(testObject.doublePrimitive, equalTo(0D));
        assertViolation("doubleObject", IsFloat.KEY);
        assertNull(testObject.doubleObject);
        
        assertViolation("date", IsDate.KEY);
        assertNull(testObject.date);
        assertViolation("timestamp", IsDate.KEY);
        assertNull(testObject.timestamp);
        
        assertNull(testObject.string);
        assertThat(testObject.characterPrimitive, equalTo('\0'));
        assertNull(testObject.characterObject);
        
        assertNull(testObject.string);
    }
    
    @Test
    public void testBodyParserWhenThereIsAnUnsupportedFieldType() {
        
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("string", new String [] {"aString"});
        map.put("iAmNotSupportedField", new String [] {"iAmNotSupportedField"});
        map.put("longs", new String[] {"1", "2"});
        
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);

        // do
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
        Mockito.when(context.getValidation()).thenReturn(validation);

        // do
        TestObjectWithArraysAndCollections testObject = bodyParserEnginePost.invoke(context, TestObjectWithArraysAndCollections.class);

        // and test:
        assertThat(testObject.integers.length, equalTo(2));
        assertThat(testObject.integers[0], equalTo(1));
        assertThat(testObject.integers[1], equalTo(2));

        assertThat(testObject.strings.size(), equalTo(2));
        assertThat(testObject.strings.get(0), equalTo("hello"));
        assertThat(testObject.strings.get(1), equalTo("world"));
        
        assertFalse(validation.hasViolations());
        
    }

    @Test
    public void testBodyParserWithEnumerations() {

        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("enum1", new String[] {MyEnum.VALUE_A.name()});
        map.put("enum2", new String[] {new String("value_b")});

        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);

        // do
        TestObjectWithEnum testObject = bodyParserEnginePost.invoke(context, TestObjectWithEnum.class);
        
        // and test:
        assertThat(testObject.enum1, equalTo(MyEnum.VALUE_A));
        assertThat(testObject.enum2, equalTo(MyEnum.VALUE_B));
        assertFalse(validation.hasViolations());
        
    }
    
    @Test
    public void testBodyParserWithCustomNeedingInjectionParamParser() {
        // some setup for this method:
        Map<String, String[]> map = new HashMap<>();
        map.put("dep", new String[] {"dep1"});
        map.put("depArray", new String[] {"depArray1", "depArray2"});
        map.put("depList", new String[] {"depList1", "depList2"});
        
        Mockito.when(context.getParameters()).thenReturn(map);
        Mockito.when(context.getValidation()).thenReturn(validation);

        // do
        TestObjectWithCustomType testObject = bodyParserEnginePost.invoke(context, TestObjectWithCustomType.class);
        
        // and test:
        assertThat(testObject.dep, equalTo(new Dep("hello_dep1")));
        
        assertNotNull(testObject.depArray);
        assertThat(testObject.depArray.length, equalTo(2));
        assertThat(testObject.depArray[0], equalTo(new Dep("hello_depArray1")));
        assertThat(testObject.depArray[1], equalTo(new Dep("hello_depArray2")));
        
        assertNotNull(testObject.depList);
        assertThat(testObject.depList.size(), equalTo(2));
        assertThat(testObject.depList.get(0), equalTo(new Dep("hello_depList1")));
        assertThat(testObject.depList.get(1), equalTo(new Dep("hello_depList2")));
        
        assertFalse(validation.hasViolations());
    }
    
    private <T> void assertViolation(String fieldName, String violationMessage) {
        assertTrue(validation.hasFieldViolation(fieldName));
        assertFalse(validation.getFieldViolations().isEmpty());
        assertThat(validation.getFieldViolations(fieldName).size(), equalTo(1));
        FieldViolation violation = validation.getFieldViolations(fieldName).get(0);
        assertThat(violation.field, equalTo(fieldName));
        assertNotNull(violation.constraintViolation);
        assertThat(violation.constraintViolation.getFieldKey(), equalTo(fieldName));
        assertThat(violation.constraintViolation.getMessageKey(), equalTo(violationMessage));
    }
    
    public static class TestObject {
    
        public int integerPrimitive;
        public Integer integerObject;
        public long longPrimitive;
        public Long longObject;
        public float floatPrimitive;
        public Float floatObject;
        public double doublePrimitive;
        public Double doubleObject;
        public String string;
        public char characterPrimitive;
        public Character characterObject;
        public Date date;
        public Date timestamp;
        @NotNull
        public Object requiredObject;

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
    
    public static enum MyEnum {
        VALUE_A, VALUE_B, VALUE_C
    }
    
    public static class TestObjectWithEnum {
        
        public MyEnum enum1;
        public MyEnum enum2;

    }
    
    public static class TestObjectWithCustomType {
        
        public Dep dep;
        public Dep[] depArray;
        public List<Dep> depList;

    }
    
}
