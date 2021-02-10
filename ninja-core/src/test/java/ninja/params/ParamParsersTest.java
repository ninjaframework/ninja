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

package ninja.params;

import java.util.UUID;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ninja.validation.Validation;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@RunWith(MockitoJUnitRunner.class)
public class ParamParsersTest {

    @Mock
    Validation validation;

    @Test
    public void testBooleanParamParser() {
        ParamParsers.BooleanParamParser booleanParamParser = new ParamParsers.BooleanParamParser();

        assertThat(booleanParamParser.getParsedType(), Matchers.is(Boolean.class));

        assertThat(booleanParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(booleanParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());
        assertThat(booleanParamParser.parseParameter("param1", "123123", validation), Matchers.nullValue());
        assertThat(booleanParamParser.parseParameter("param1", "-", validation), Matchers.nullValue());
        assertThat(booleanParamParser.parseParameter("param1", "+", validation), Matchers.nullValue());

        assertThat(booleanParamParser.parseParameter("param1", "true", validation), Matchers.is(Boolean.TRUE));
        assertThat(booleanParamParser.parseParameter("param1", "TRUE", validation), Matchers.is(Boolean.TRUE));

        assertThat(booleanParamParser.parseParameter("param1", "false", validation), Matchers.is(Boolean.FALSE));
        assertThat(booleanParamParser.parseParameter("param1", "FALSE", validation), Matchers.is(Boolean.FALSE));
    }

    @Test
    public void testPrimitiveBooleanParamParser() {
        ParamParsers.PrimitiveBooleanParamParser booleanParamParser = new ParamParsers.PrimitiveBooleanParamParser();

        assertThat(booleanParamParser.getParsedType(), Matchers.is(Boolean.class));

        // No null for primitives
        assertThat(booleanParamParser.parseParameter("param1", null, validation), Matchers.is(Boolean.FALSE));
        assertThat(booleanParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(Boolean.FALSE));
        assertThat(booleanParamParser.parseParameter("param1", "123123", validation), Matchers.is(Boolean.FALSE));
        assertThat(booleanParamParser.parseParameter("param1", "-", validation), Matchers.is(Boolean.FALSE));
        assertThat(booleanParamParser.parseParameter("param1", "+", validation), Matchers.is(Boolean.FALSE));

        assertThat(booleanParamParser.parseParameter("param1", "true", validation), Matchers.is(Boolean.TRUE));
        assertThat(booleanParamParser.parseParameter("param1", "TRUE", validation), Matchers.is(Boolean.TRUE));

        assertThat(booleanParamParser.parseParameter("param1", "false", validation), Matchers.is(Boolean.FALSE));
        assertThat(booleanParamParser.parseParameter("param1", "FALSE", validation), Matchers.is(Boolean.FALSE));
    }

    @Test
    public void testStringParamParser() {
        ParamParsers.StringParamParser stringParamParser = new ParamParsers.StringParamParser();

        assertThat(stringParamParser.getParsedType(), Matchers.is(String.class));

        assertThat(stringParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(stringParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        assertThat(stringParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new String("asdfasdf")));
    }

    @Test
    public void testEmptyStringParamParser() {
        ParamParsers.EmptyStringParamParser stringParamParser = new ParamParsers.EmptyStringParamParser();

        assertThat(stringParamParser.getParsedType(), Matchers.is(String.class));

        assertThat(stringParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(stringParamParser.parseParameter("param1", "", validation), Matchers.emptyString());
        assertThat(stringParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new String("asdfasdf")));
    }
    
    @Test
    public void testIntegerParamParser() {
        ParamParsers.IntegerParamParser integerParamParser = new ParamParsers.IntegerParamParser();

        assertThat(integerParamParser.getParsedType(), Matchers.is(Integer.class));

        assertThat(integerParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(integerParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        assertThat(integerParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());

        assertThat(integerParamParser.parseParameter("param1", "0", validation), Matchers.is(new Integer(0)));
        assertThat(integerParamParser.parseParameter("param1", "000", validation), Matchers.is(new Integer(0)));
        assertThat(integerParamParser.parseParameter("param1", "123", validation), Matchers.is(new Integer(123)));
        assertThat(integerParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Integer(-123)));
    }

    @Test
    public void testPrimitiveIntegerParamParser() {
        ParamParsers.PrimitiveIntegerParamParser integerParamParser = new ParamParsers.PrimitiveIntegerParamParser();

        assertThat(integerParamParser.getParsedType(), Matchers.is(Integer.class));

        // No null form primitives
        assertThat(integerParamParser.parseParameter("param1", null, validation), Matchers.is((int) 0));
        assertThat(integerParamParser.parseParameter("param1", "", validation), Matchers.is((int) 0));
        assertThat(integerParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is((int) 0));

        assertThat(integerParamParser.parseParameter("param1", "0", validation), Matchers.is((int) 0));
        assertThat(integerParamParser.parseParameter("param1", "000", validation), Matchers.is((int) 0));
        assertThat(integerParamParser.parseParameter("param1", "123", validation), Matchers.is((int) 123));
        assertThat(integerParamParser.parseParameter("param1", "-123", validation), Matchers.is((int) -123));
    }

    @Test
    public void testShortParamParser() {
        ParamParsers.ShortParamParser shortParamParser = new ParamParsers.ShortParamParser();

        assertThat(shortParamParser.getParsedType(), Matchers.is(Short.class));

        assertThat(shortParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(shortParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        assertThat(shortParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());

        assertThat(shortParamParser.parseParameter("param1", "0", validation), Matchers.is(new Short((short) 0)));
        assertThat(shortParamParser.parseParameter("param1", "000", validation), Matchers.is(new Short((short) 0)));
        assertThat(shortParamParser.parseParameter("param1", "123", validation), Matchers.is(new Short((short) 123)));
        assertThat(shortParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Short((short) -123)));
    }

    @Test
    public void testPrimitiveShortParamParser() {
        ParamParsers.PrimitiveShortParamParser shortParamParser = new ParamParsers.PrimitiveShortParamParser();

        assertThat(shortParamParser.getParsedType(), Matchers.is(Short.class));

        // No null form primitives
        assertThat(shortParamParser.parseParameter("param1", null, validation), Matchers.is((short) 0));
        assertThat(shortParamParser.parseParameter("param1", "", validation), Matchers.is((short) 0));
        assertThat(shortParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is((short) 0));

        assertThat(shortParamParser.parseParameter("param1", "0", validation), Matchers.is((short) 0));
        assertThat(shortParamParser.parseParameter("param1", "000", validation), Matchers.is((short) 0));
        assertThat(shortParamParser.parseParameter("param1", "123", validation), Matchers.is((short) 123));
        assertThat(shortParamParser.parseParameter("param1", "-123", validation), Matchers.is((short) -123));
    }
    
    @Test
    public void testLongParamParser() {
        ParamParsers.LongParamParser longParamParser = new ParamParsers.LongParamParser();

        assertThat(longParamParser.getParsedType(), Matchers.is(Long.class));

        assertThat(longParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(longParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        assertThat(longParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());

        assertThat(longParamParser.parseParameter("param1", "0", validation), Matchers.is(new Long(0)));
        assertThat(longParamParser.parseParameter("param1", "000", validation), Matchers.is(new Long(0)));
        assertThat(longParamParser.parseParameter("param1", "123", validation), Matchers.is(new Long(123)));
        assertThat(longParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Long(-123)));
    }

    @Test
    public void testPrimitiveLongParamParser() {
        ParamParsers.PrimitiveLongParamParser longParamParser = new ParamParsers.PrimitiveLongParamParser();

        assertThat(longParamParser.getParsedType(), Matchers.is(Long.class));

        // No null form primitives
        assertThat(longParamParser.parseParameter("param1", null, validation), Matchers.is(new Long(0)));
        assertThat(longParamParser.parseParameter("param1", "", validation), Matchers.is(new Long(0)));
        assertThat(longParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new Long(0)));

        assertThat(longParamParser.parseParameter("param1", "0", validation), Matchers.is(new Long(0)));
        assertThat(longParamParser.parseParameter("param1", "000", validation), Matchers.is(new Long(0)));
        assertThat(longParamParser.parseParameter("param1", "123", validation), Matchers.is(new Long(123)));
        assertThat(longParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Long(-123)));
    }

    @Test
    public void testFloatParamParser() {
        ParamParsers.FloatParamParser floatParamParser = new ParamParsers.FloatParamParser();

        assertThat(floatParamParser.getParsedType(), Matchers.is(Float.class));

        assertThat(floatParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(floatParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        assertThat(floatParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());

        assertThat(floatParamParser.parseParameter("param1", "0", validation), Matchers.is(new Float(0)));
        assertThat(floatParamParser.parseParameter("param1", "000", validation), Matchers.is(new Float(0)));
        assertThat(floatParamParser.parseParameter("param1", "123", validation), Matchers.is(new Float(123)));
        assertThat(floatParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Float(-123)));

        assertThat(floatParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Float(0.1)));
        assertThat(floatParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Float(123.1)));
        assertThat(floatParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Float(-123.1)));
    }

    @Test
    public void testPrimitiveFloatParamParser() {
        ParamParsers.PrimitiveFloatParamParser floatParamParser = new ParamParsers.PrimitiveFloatParamParser();

        assertThat(floatParamParser.getParsedType(), Matchers.is(Float.class));

        // No null form primitives
        assertThat(floatParamParser.parseParameter("param1", null, validation), Matchers.is(new Float(0)));
        assertThat(floatParamParser.parseParameter("param1", "", validation), Matchers.is(new Float(0)));
        assertThat(floatParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new Float(0)));

        assertThat(floatParamParser.parseParameter("param1", "0", validation), Matchers.is(new Float(0)));
        assertThat(floatParamParser.parseParameter("param1", "000", validation), Matchers.is(new Float(0)));
        assertThat(floatParamParser.parseParameter("param1", "123", validation), Matchers.is(new Float(123)));
        assertThat(floatParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Float(-123)));

        assertThat(floatParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Float(0.1)));
        assertThat(floatParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Float(123.1)));
        assertThat(floatParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Float(-123.1)));
    }

    @Test
    public void testDoubleParamParser() {
        ParamParsers.DoubleParamParser doubleParamParser = new ParamParsers.DoubleParamParser();

        assertThat(doubleParamParser.getParsedType(), Matchers.is(Double.class));

        assertThat(doubleParamParser.parseParameter("param1", null, validation), Matchers.nullValue());
        assertThat(doubleParamParser.parseParameter("param1", "", validation), Matchers.nullValue());
        assertThat(doubleParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.nullValue());

        assertThat(doubleParamParser.parseParameter("param1", "0", validation), Matchers.is(new Double(0)));
        assertThat(doubleParamParser.parseParameter("param1", "000", validation), Matchers.is(new Double(0)));
        assertThat(doubleParamParser.parseParameter("param1", "123", validation), Matchers.is(new Double(123)));
        assertThat(doubleParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Double(-123)));

        assertThat(doubleParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Double(0.1)));
        assertThat(doubleParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Double(123.1)));
        assertThat(doubleParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Double(-123.1)));
    }

    @Test
    public void testPrimitiveDoubleParamParser() {
        ParamParsers.PrimitiveDoubleParamParser doubleParamParser = new ParamParsers.PrimitiveDoubleParamParser();

        assertThat(doubleParamParser.getParsedType(), Matchers.is(Double.class));

        // No null form primitives
        assertThat(doubleParamParser.parseParameter("param1", null, validation), Matchers.is(new Double(0)));
        assertThat(doubleParamParser.parseParameter("param1", "", validation), Matchers.is(new Double(0)));
        assertThat(doubleParamParser.parseParameter("param1", "asdfasdf", validation), Matchers.is(new Double(0)));

        assertThat(doubleParamParser.parseParameter("param1", "0", validation), Matchers.is(new Double(0)));
        assertThat(doubleParamParser.parseParameter("param1", "000", validation), Matchers.is(new Double(0)));
        assertThat(doubleParamParser.parseParameter("param1", "123", validation), Matchers.is(new Double(123)));
        assertThat(doubleParamParser.parseParameter("param1", "-123", validation), Matchers.is(new Double(-123)));

        assertThat(doubleParamParser.parseParameter("param1", "0.1", validation), Matchers.is(new Double(0.1)));
        assertThat(doubleParamParser.parseParameter("param1", "123.1", validation), Matchers.is(new Double(123.1)));
        assertThat(doubleParamParser.parseParameter("param1", "-123.1", validation), Matchers.is(new Double(-123.1)));
    }
    
    @Test
    public void testUUIDParamParser() {
        ParamParsers.UUIDParamParser uuidParamParser = new ParamParsers.UUIDParamParser();

        assertThat(uuidParamParser.getParsedType(), Matchers.is(UUID.class));
        assertThat(uuidParamParser.parseParameter("param1", null, validation), is(nullValue()));
        assertThat(uuidParamParser.parseParameter("param1", "", validation), is(nullValue()));
        assertThat(uuidParamParser.parseParameter("param1", "asdfasdf", validation), is(nullValue()));
        assertThat(uuidParamParser.parseParameter("param1", "fe45481f-ed31-40e4-9bca-9cec383302c2", validation),
            is(UUID.fromString("fe45481f-ed31-40e4-9bca-9cec383302c2")));
    }
    
}