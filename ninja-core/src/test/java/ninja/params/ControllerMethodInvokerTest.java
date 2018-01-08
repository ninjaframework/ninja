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

package ninja.params;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.multibindings.Multibinder;

import ninja.Context;
import ninja.Result;
import ninja.RoutingException;
import ninja.i18n.Lang;
import ninja.i18n.LangImpl;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import ninja.validation.*;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import ninja.exceptions.BadRequestException;
import ninja.utils.NinjaConstant;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Matchers;

import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ControllerMethodInvokerTest.
 */
@RunWith(MockitoJUnitRunner.class)
public class ControllerMethodInvokerTest {

    @Mock
    private MockController mockController;

    @Mock
    private Context context;

    @Mock
    private Session session;

    @Mock
    private FlashScope flash;

    private NinjaProperties ninjaProperties;

    private Lang lang;

    private Validation validation;

    @Before
    public void setUp() throws Exception {
        this.ninjaProperties = Mockito.spy(new NinjaPropertiesImpl(NinjaMode.test));
        this.lang = new LangImpl(this.ninjaProperties);
        this.validation = new ValidationImpl();

        when(this.context.getSession()).thenReturn(this.session);
        when(this.context.getFlashScope()).thenReturn(this.flash);
        when(this.context.getValidation()).thenReturn(this.validation);
    }

    @Test
    public void noParameterMethodShouldBeInvoked() throws Exception {
        create("noParameter").invoke(mockController, context);
        verify(mockController).noParameter();
    }

    @Test
    public void contextShouldBePassed() throws Exception {
        create("context").invoke(mockController, context);
        verify(mockController).context(context);
    }

    @Test
    public void sessionShouldBePassed() throws Exception {
        create("session").invoke(mockController, context);
        verify(mockController).session(session);
    }

    @Test
    public void flashArgumentShouldBePassed() throws Exception {
        create("flash").invoke(mockController, context);
        verify(mockController).flash(flash);
    }


    @Test
    public void paramAnnotatedArgumentShouldBePassed() throws Exception {
        when(context.getParameter("param1")).thenReturn("value");
        create("param").invoke(mockController, context);
        verify(mockController).param("value");
    }

    @Test
    public void pathParamAnnotatedArgumentShouldBePassed() throws Exception {
        when(context.getPathParameter("param1")).thenReturn("value");
        create("pathParam").invoke(mockController, context);
        verify(mockController).pathParam("value");
    }

    @Test
    public void sessionParamAnnotatedArgumentShouldBePassed() throws Exception {
        when(session.get("param1")).thenReturn("value");
        create("sessionParam").invoke(mockController, context);
        verify(mockController).sessionParam("value");
    }

    @Test
    public void attributeAnnotatedArgumentShouldBePassed() throws Exception {
        Dep dep = new Dep("dep");
        when(context.getAttribute("param1", Dep.class)).thenReturn(dep);
        create("attribute").invoke(mockController, context);
        verify(mockController).attribute(dep);
    }

    @Test
    public void headerAnnotatedArgumentShouldBePassed() throws Exception {
        when(context.getHeader("param1")).thenReturn("value");
        create("header").invoke(mockController, context);
        verify(mockController).header("value");
    }

    @Test
    public void headerAnnotatedArgumentShouldHandleNull() throws Exception {
        when(context.getHeader("param1")).thenReturn(null);
        create("header").invoke(mockController, context);
        verify(mockController).header(null);
    }

    @Test
    public void headersAnnotatedArgumentShouldReturnNull() throws Exception {
        when(context.getHeaders("param1")).thenReturn(new ArrayList<String>());
        create("headers").invoke(mockController, context);
        verify(mockController).headers(null);
    }

    @Test
    public void headersAnnotatedArgumentShouldBePassed() throws Exception {
        when(context.getHeaders("param1")).thenReturn(Arrays.asList("a", "b", "c"));
        create("headers").invoke(mockController, context);
        verify(mockController).headers(new String[]{"a", "b", "c"});
    }

    @Test
    public void headersAnnotatedArgumentShouldHandleNull() throws Exception {
        when(context.getHeader("param1")).thenReturn(null);
        create("headers").invoke(mockController, context);
        verify(mockController).headers(null);
    }

    @Test
    public void integerParamShouldBeParsedToInteger() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("integerParam").invoke(mockController, context);
        verify(mockController).integerParam(20);
    }

    @Test
    public void integerParamShouldHandleNull() throws Exception {
        create("integerParam").invoke(mockController, context);
        verify(mockController).integerParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void integerValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("integerParam").invoke(mockController, context);
        verify(mockController).integerParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void intParamShouldBeParsedToInteger() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("intParam").invoke(mockController, context);
        verify(mockController).intParam(20);
    }

    @Test
    public void intParamShouldHandleNull() throws Exception {
        create("intParam").invoke(mockController, context);
        verify(mockController).intParam(0);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void intValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("intParam").invoke(mockController, context);
        verify(mockController).intParam(0);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void shortParamShouldBeParsedToShort() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("shortParam").invoke(mockController, context);
        verify(mockController).shortParam((short) 20);
    }

    @Test
    public void shortParamShouldHandleNull() throws Exception {
        create("shortParam").invoke(mockController, context);
        verify(mockController).shortParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void shortValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("shortParam").invoke(mockController, context);
        verify(mockController).shortParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void primShortParamShouldBeParsedToShort() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("primShortParam").invoke(mockController, context);
        verify(mockController).primShortParam((short) 20);
    }

    @Test
    public void primShortParamShouldHandleNull() throws Exception {
        create("primShortParam").invoke(mockController, context);
        verify(mockController).primShortParam((short) 0);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void primShortValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primShortParam").invoke(mockController, context);
        verify(mockController).primShortParam((short) 0);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void characterParamShouldBeParsedToCharacter() throws Exception {
        when(context.getParameter("param1")).thenReturn("ABC");
        create("characterParam").invoke(mockController, context);
        verify(mockController).characterParam('A');
    }

    @Test
    public void characterParamShouldHandleNull() throws Exception {
        create("characterParam").invoke(mockController, context);
        verify(mockController).characterParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void charParamShouldBeParsedToCharacter() throws Exception {
        when(context.getParameter("param1")).thenReturn("ABC");
        create("charParam").invoke(mockController, context);
        verify(mockController).charParam('A');
    }

    @Test
    public void charParamShouldHandleNull() throws Exception {
        create("charParam").invoke(mockController, context);
        verify(mockController).charParam('\0');
        assertFalse(validation.hasViolations());
    }

    @Test
    public void byteParamShouldBeParsedToByte() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("byteParam").invoke(mockController, context);
        verify(mockController).byteParam((byte) 20);
    }

    @Test
    public void byteParamShouldHandleNull() throws Exception {
        create("byteParam").invoke(mockController, context);
        verify(mockController).byteParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void byteValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("byteParam").invoke(mockController, context);
        verify(mockController).byteParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void primByteParamShouldBeParsedToByte() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("primByteParam").invoke(mockController, context);
        verify(mockController).primByteParam((byte) 20);
    }

    @Test
    public void primByteParamShouldHandleNull() throws Exception {
        create("primByteParam").invoke(mockController, context);
        verify(mockController).primByteParam((byte) 0);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void primByteValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primByteParam").invoke(mockController, context);
        verify(mockController).primByteParam((byte) 0);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void booleanParamShouldBeParsedToBoolean() throws Exception {
        when(context.getParameter("param1")).thenReturn("true");
        create("booleanParam").invoke(mockController, context);
        verify(mockController).booleanParam(true);
    }

    @Test
    public void booleanParamShouldHandleNull() throws Exception {
        create("booleanParam").invoke(mockController, context);
        verify(mockController).booleanParam(null);
        assertFalse(validation.hasViolations());
    }
    
    
    @Test(expected = BadRequestException.class)
    public void booleanParamShouldHandleNullInStrictMode() throws Exception {
        when(context.getParameter("param1")).thenReturn(null);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);

        create("booleanParam").invoke(mockController, context);
    }
    
    @Test(expected = BadRequestException.class)
    public void booleanParamShouldHandleWrongInputForBooleanInStrictMode() throws Exception {
        when(context.getParameter("param1")).thenReturn("test");
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);

        create("booleanParam").invoke(mockController, context);
    }

    @Test
    public void primBooleanParamShouldBeParsedToBoolean() throws Exception {
        when(context.getParameter("param1")).thenReturn("true");
        create("primBooleanParam").invoke(mockController, context);
        verify(mockController).primBooleanParam(true);
    }

    @Test
    public void primBooleanParamShouldHandleNull() throws Exception {
        create("primBooleanParam").invoke(mockController, context);
        verify(mockController).primBooleanParam(false);
        assertFalse(validation.hasViolations());
    }
        
    @Test
    public void booleanParamWithOptionalShouldHandleWrongInputForBooleanInStrictMode() throws Exception {
        when(context.getParameter("param1")).thenReturn("test");
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);

        create("booleanParamWithOptional").invoke(mockController, context);
        
        verify(mockController).booleanParamWithOptional(Optional.empty());
    }
        
    @Test
    public void booleanParamWithOptionalShouldHandleTrueInStrictMode() throws Exception {
        when(context.getParameter("param1")).thenReturn("true");
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);

        create("booleanParamWithOptional").invoke(mockController, context);
        
        verify(mockController).booleanParamWithOptional(Optional.of(Boolean.TRUE));
    }
    
    @Test
    public void booleanParamWithOptionalShouldHandleFalseInStrictMode() throws Exception {
        when(context.getParameter("param1")).thenReturn("false");
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);

        create("booleanParamWithOptional").invoke(mockController, context);
        
        verify(mockController).booleanParamWithOptional(Optional.of(Boolean.FALSE));
    }

    @Test
    public void longParamShouldBeParsedToLong() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("longParam").invoke(mockController, context);
        verify(mockController).longParam(20l);
    }

    @Test
    public void longParamShouldHandleNull() throws Exception {
        create("longParam").invoke(mockController, context);
        verify(mockController).longParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void longValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("longParam").invoke(mockController, context);
        verify(mockController).longParam(null);
        assertTrue(validation.hasViolation("param1"));
    }


    @Test
    public void primLongParamShouldBeParsedToLong() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("primLongParam").invoke(mockController, context);
        verify(mockController).primLongParam(20);
    }

    @Test
    public void primLongParamShouldHandleNull() throws Exception {
        create("primLongParam").invoke(mockController, context);
        verify(mockController).primLongParam(0);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void primLongValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primLongParam").invoke(mockController, context);
        verify(mockController).primLongParam(0L);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void floatParamShouldBeParsedToFloat() throws Exception {
        when(context.getParameter("param1")).thenReturn("3.14");
        create("floatParam").invoke(mockController, context);
        verify(mockController).floatParam(3.14f);
    }

    @Test
    public void floatParamShouldHandleNull() throws Exception {
        create("floatParam").invoke(mockController, context);
        verify(mockController).floatParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void floatValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("floatParam").invoke(mockController, context);
        verify(mockController).floatParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void primFloatParamShouldBeParsedToFloat() throws Exception {
        when(context.getParameter("param1")).thenReturn("3.14");
        create("primFloatParam").invoke(mockController, context);
        verify(mockController).primFloatParam(3.14f);
    }

    @Test
    public void primFloatParamShouldHandleNull() throws Exception {
        create("primFloatParam").invoke(mockController, context);
        verify(mockController).primFloatParam(0);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void primFloatValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primFloatParam").invoke(mockController, context);
        verify(mockController).primFloatParam(0);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void doubleParamShouldBeParsedToDouble() throws Exception {
        when(context.getParameter("param1")).thenReturn("3.14");
        create("doubleParam").invoke(mockController, context);
        verify(mockController).doubleParam(3.14);
    }

    @Test
    public void doubleParamShouldHandleNull() throws Exception {
        create("doubleParam").invoke(mockController, context);
        verify(mockController).doubleParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void doubleValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("doubleParam").invoke(mockController, context);
        verify(mockController).doubleParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void primDoubleParamShouldBeParsedToDouble() throws Exception {
        when(context.getParameter("param1")).thenReturn("3.14");
        create("primDoubleParam").invoke(mockController, context);
        verify(mockController).primDoubleParam(3.14);
    }

    @Test
    public void primDoubleParamShouldHandleNull() throws Exception {
        create("primDoubleParam").invoke(mockController, context);
        verify(mockController).primDoubleParam(0);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void primDoubleValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primDoubleParam").invoke(mockController, context);
        verify(mockController).primDoubleParam(0);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void enumParamShouldBeParsedToEnumCaseSensitive() throws Exception {
        when(context.getParameter("param1")).thenReturn("Red");
        create("enumParam").invoke(mockController, context);
        verify(mockController).enumParam(Rainbow.Red);
    }

    @Test
    public void enumParamShouldBeParsedToEnumCaseInsensitive() throws Exception {
        when(context.getParameter("param1")).thenReturn("red");
        create("enumParam").invoke(mockController, context);
        verify(mockController).enumParam(Rainbow.Red);
    }

    @Test
    public void enumParamShouldHandleNull() throws Exception {
        create("enumParam").invoke(mockController, context);
        verify(mockController).enumParam(null);
        assertFalse(validation.hasViolations());
    }

    @Test
    public void enumParamValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("enumParam").invoke(mockController, context);
        verify(mockController).enumParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void enumCsvParamSingleShouldBeParsed() throws Exception {
        when(context.getParameter("param1")).thenReturn("Red");
        create("enumCsvParam").invoke(mockController, context);
        verify(mockController).enumCsvParam(new Rainbow[]{Rainbow.Red});
    }

    @Test
    public void enumCsvParamMultipleShouldBeParsed() throws Exception {
        when(context.getParameter("param1")).thenReturn("Red,Orange,Yellow");
        create("enumCsvParam").invoke(mockController, context);
        verify(mockController).enumCsvParam(new Rainbow[]{Rainbow.Red, Rainbow.Orange, Rainbow.Yellow});
    }

    @Test
    public void enumCsvParamShouldReturnNull() throws Exception {
        when(context.getParameter("param1")).thenReturn("");
        create("enumCsvParam").invoke(mockController, context);
        verify(mockController).enumCsvParam(null);
        assertFalse(validation.hasViolation("param1"));
    }

    @Test
    public void enumCsvParamValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("White,Black");
        create("enumCsvParam").invoke(mockController, context);
        verify(mockController).enumCsvParam(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void enumArrayParamSingleShouldBeParsed() throws Exception {
        when(context.getParameterValues("param1")).thenReturn(Arrays.asList("Blue"));
        create("enumArrayParam").invoke(mockController, context);
        verify(mockController).enumArrayParam(new Rainbow[]{Rainbow.Blue});
    }

    @Test
    public void enumArrayParamMultipleShouldBeParsed() throws Exception {
        when(context.getParameterValues("param1")).thenReturn(Arrays.asList("Blue", "Indigo", "Violet"));
        create("enumArrayParam").invoke(mockController, context);
        verify(mockController).enumArrayParam(new Rainbow[]{Rainbow.Blue, Rainbow.Indigo, Rainbow.Violet});
    }

    @Test
    public void enumArrayParamShouldReturnNull() throws Exception {
        when(context.getParameterValues("param1")).thenReturn(new ArrayList<String>());
        create("enumArrayParam").invoke(mockController, context);
        verify(mockController).enumArrayParam(null);
        assertFalse(validation.hasViolation("param1"));
    }

    @Test
    public void enumArrayParamValidationShouldWork() throws Exception {
        when(context.getParameterValues("param1")).thenReturn(Arrays.asList("White", "Black"));
        create("enumArrayParam").invoke(mockController, context);
        verify(mockController).enumArrayParam(null);
        assertTrue(validation.hasViolation("param1"));
    }
    
    
    @Test
    public void customDateFormatParamShouldBeParsedToDate() throws Exception {
        when(context.getParameter("param1")).thenReturn("15/01/2015");
        create("dateParam", DateParamParser.class).invoke(mockController, context);
        verify(mockController).dateParam(new LocalDateTime(2015, 1, 15, 0, 0).toDate());
    }
    
    @Test
    public void customDateFormatParamWithOptionalShouldBeParsedToDate() throws Exception {
        when(context.getParameter("param1")).thenReturn("15/01/2015");
        create("dateParamWithOptional", DateParamParser.class).invoke(mockController, context);
        verify(mockController).dateParamWithOptional(Optional.of(new LocalDateTime(2015, 1, 15, 0, 0).toDate()));
    }

    @Test
    public void customDateFormatParamShouldHandleNull() throws Exception {
        create("dateParam", DateParamParser.class).invoke(mockController, context);
        verify(mockController).dateParam(null);
        assertFalse(validation.hasViolations());
    }
    
    @Test
    public void customDateFormatParamWithOptionalShouldHandleEmpty() throws Exception {
        create("dateParamWithOptional", DateParamParser.class).invoke(mockController, context);
        verify(mockController).dateParamWithOptional(Optional.empty());
        assertFalse(validation.hasViolations());
    }

    @Test
    public void customDateFormatValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("dateParam", DateParamParser.class).invoke(mockController, context);
        verify(mockController).dateParam(null);
        assertTrue(validation.hasViolation("param1"));
    }
    
    @Test(expected = RoutingException.class)
    public void needingInjectionParamParserNotBinded() throws Exception {
        when(context.getParameter("param1")).thenReturn("hello");
        create("needingInjectionParamParser").invoke(mockController, context);
        verify(mockController).needingInjectionParamParser(new Dep("hello_hello"));
    }
    
    @Test
    public void needingInjectionParamParser() throws Exception {
        when(context.getParameter("param1")).thenReturn("hello");
        create("needingInjectionParamParser", NeedingInjectionParamParser.class).invoke(mockController, context);
        verify(mockController).needingInjectionParamParser(new Dep("hello_hello"));
    }
    
    @Test
    public void needingInjectionParamParserArray() throws Exception {
        when(context.getParameterValues("param1")).thenReturn(Arrays.asList("hello1", "hello2"));
        create("needingInjectionParamParserArray", NeedingInjectionParamParser.class).invoke(mockController, context);
        verify(mockController).needingInjectionParamParserArray(new Dep[] { new Dep("hello_hello1"), new Dep("hello_hello2") });
    }

    @Test
    public void customArgumentExtractorWithNoArgsShouldBeInstantiated() {
        create("noArgArgumentExtractor").invoke(mockController, context);
        verify(mockController).noArgArgumentExtractor("noargs");
    }

    @Test
    public void customArgumentExtractorWithClassArgShouldBeInstantiated() {
        create("classArgArgumentExtractor").invoke(mockController, context);
        verify(mockController).classArgArgumentExtractor("java.lang.String");
    }

    @Test
    public void customArgumentExtractorWithGuiceShouldBeInstantiated() {
        create("guiceArgumentExtractor", new Dep("dep")).invoke(mockController, context);
        verify(mockController).guiceArgumentExtractor("dep:bar:java.lang.String");
    }
    
    @Test
    public void customArgumentExtractorWithOptionalAndGuiceShouldBeInstantiated() {
        create("guiceArgumentExtractorWithOptional", new Dep("dep")).invoke(mockController, context);
        verify(mockController).guiceArgumentExtractorWithOptional(Optional.of("dep:bar:java.lang.String"));
    }

    @Test
    public void multipleDifferentExtractorsShouldWorkFine() {
        when(context.getParameter("param1")).thenReturn("value");
        when(context.getPathParameter("param2")).thenReturn("20");
        create("multiple").invoke(mockController, context);
        verify(mockController).multiple("value", 20, context, session);
    }

    @Test
    public void validationShouldFailWhenBadRequest() {
        create("required").invoke(mockController, context);
        verify(mockController).required(null);
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void validationShouldPassWhenGoodRequest() {
        when(context.getParameter("param1")).thenReturn("value");
        create("required").invoke(mockController, context);
        verify(mockController).required("value");
        assertFalse(validation.hasViolations());
    }
    
    @Test
    public void optionalSessionParam() {
        when(session.get("param1")).thenReturn("value");
        create("optionalSessionParam").invoke(mockController, context);
        verify(mockController).optionalSessionParam(Optional.of("value"));
    }
    
    @Test
    public void optionalSessionParamEmpty() {
        when(session.get("param1")).thenReturn(null);
        create("optionalSessionParam").invoke(mockController, context);
        verify(mockController).optionalSessionParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void sessionParamStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(session.get("param1")).thenReturn(null);
        create("sessionParam").invoke(mockController, context);
    }

    @Test
    public void optionalAttribute() {
        Dep dep = new Dep("dep");
        when(context.getAttribute("param1", Dep.class)).thenReturn(dep);
        create("optionalAttribute").invoke(mockController, context);
        verify(mockController).optionalAttribute(Optional.of(dep));
    }
    
    @Test
    public void optionalAttributeEmpty() {
        when(context.getAttribute("param1", Dep.class)).thenReturn(null);
        create("optionalAttribute").invoke(mockController, context);
        verify(mockController).optionalAttribute(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void attributeStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getAttribute("param1", Dep.class)).thenReturn(null);
        create("attribute").invoke(mockController, context);
    }

    @Test
    public void optionalHeader() {
        when(context.getHeader("param1")).thenReturn("value");
        create("optionalHeader").invoke(mockController, context);
        verify(mockController).optionalHeader(Optional.of("value"));
    }
    
    @Test
    public void optionalHeaderEmpty() {
        when(context.getHeader("param1")).thenReturn(null);
        create("optionalHeader").invoke(mockController, context);
        verify(mockController).optionalHeader(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void headerStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getHeader("param1")).thenReturn(null);
        create("header").invoke(mockController, context);
    }

    @Test
    public void optionalParam() {
        when(context.getParameter("param1")).thenReturn("value");
        create("optionalParam").invoke(mockController, context);
        verify(mockController).optionalParam(Optional.of("value"));
    }
    
    @Test
    public void optionalParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalParam").invoke(mockController, context);
        verify(mockController).optionalParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void paramStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("param").invoke(mockController, context);
    }
    
    @Test
    public void optionalIntegerParam() {
        when(context.getParameter("param1")).thenReturn("1");
        create("optionalIntegerParam").invoke(mockController, context);
        verify(mockController).optionalIntegerParam(Optional.of(1));
    }
    
    @Test
    public void optionalIntegerParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalIntegerParam").invoke(mockController, context);
        verify(mockController).optionalIntegerParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void integerParamStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("integerParam").invoke(mockController, context);
    }
    
    @Test
    public void optionalLongParam() {
        when(context.getParameter("param1")).thenReturn("1");
        create("optionalLongParam").invoke(mockController, context);
        verify(mockController).optionalLongParam(Optional.of(1L));
    }
    
    @Test
    public void optionalLongParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalLongParam").invoke(mockController, context);
        verify(mockController).optionalLongParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void longParamEmptyStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("longParam").invoke(mockController, context);
    }
    
    @Test
    public void optionalShortParam() {
        when(context.getParameter("param1")).thenReturn("1");
        create("optionalShortParam").invoke(mockController, context);
        verify(mockController).optionalShortParam(Optional.of(new Short("1")));
    }
    
    @Test
    public void optionalShortParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalShortParam").invoke(mockController, context);
        verify(mockController).optionalShortParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void shortParamEmptyStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("shortParam").invoke(mockController, context);
    }
    
    @Test
    public void optionalEnumParam() {
        when(context.getParameter("param1")).thenReturn("red");
        create("optionalEnumParam").invoke(mockController, context);
        verify(mockController).optionalEnumParam(Optional.of(Rainbow.Red));
    }
    
    @Test
    public void optionalEnumParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalEnumParam").invoke(mockController, context);
        verify(mockController).optionalEnumParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void rainbowParamStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("enumParam").invoke(mockController, context);
    }
    
    @Captor
    ArgumentCaptor<Optional<Rainbow[]>> argumentCaptor;
    
    @Test
    public void optionalEnumArrayParam() {
        when(context.getParameter("param1")).thenReturn("Red,Orange,Yellow");
        create("optionalEnumArrayParam").invoke(mockController, context);
        verify(mockController).optionalEnumArrayParam(argumentCaptor.capture());
        Rainbow [] rainbows = argumentCaptor.getValue().get();
        assertThat(rainbows, Matchers.arrayContaining(Rainbow.Red, Rainbow.Orange, Rainbow.Yellow));
    }
    
    @Test
    public void optionalEnumArrayParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalEnumArrayParam").invoke(mockController, context);
        verify(mockController).optionalEnumArrayParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void rainbowArrayParamEmptyStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("enumArrayParam").invoke(mockController, context);
    }
    
    @Test
    public void optionalDateParam() {
        when(context.getParameter("param1")).thenReturn("15/01/2015");
        create("optionalDateParam", DateParamParser.class).invoke(mockController, context);
        verify(mockController).optionalDateParam(Optional.of(new LocalDateTime(2015, 1, 15, 0, 0).toDate()));
    }
    
    @Test
    public void optionalDateParamEmpty() {
        when(context.getParameter("param1")).thenReturn(null);
        create("optionalDateParam", DateParamParser.class).invoke(mockController, context);
        verify(mockController).optionalDateParam(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void dateParamStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.getParameter("param1")).thenReturn(null);
        create("dateParam", DateParamParser.class).invoke(mockController, context);
    }
    
    @Test
    public void optionalBody() {
        Object body = new Object();
        when(context.parseBody(Object.class)).thenReturn(body);
        create("optionalBody").invoke(mockController, context);
        verify(mockController).optionalBody(Optional.of(body));
    }
    
    @Test
    public void optionalBodyEmpty() {
        when(context.parseBody(Object.class)).thenReturn(null);
        create("optionalBody").invoke(mockController, context);
        verify(mockController).optionalBody(Optional.empty());
    }
    
    @Test(expected = BadRequestException.class)
    public void bodyEmptyStrictModeWorks() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.NINJA_STRICT_ARGUMENT_EXTRACTORS, false)).thenReturn(true);
        when(context.parseBody(Object.class)).thenReturn(null);
        create("body").invoke(mockController, context);
    }
    
    @Test
    public void validationShouldBeAppliedInCorrectOrderPreFail() {
        create("requiredInt").invoke(mockController, context);
        verify(mockController).requiredInt(0);
        assertTrue(validation.hasViolation("param1"));
        assertEquals(1, validation.getViolations("param1").size());
        assertEquals("validation.required.violation", validation.getViolations("param1").get(0).getMessageKey());
    }
    
    @Test
    public void validationWithOptionalShouldBeAppliedInCorrectOrderPreFail() {
        create("requiredIntWithOptional").invoke(mockController, context);
        verify(mockController).requiredIntWithOptional(Optional.empty());
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPostFail() {
        when(context.getParameter("param1")).thenReturn("5");
        create("requiredInt").invoke(mockController, context);
        verify(mockController).requiredInt(5);
        assertTrue(validation.hasViolation("param1"));
        assertEquals(1, validation.getViolations("param1").size());
        assertEquals("validation.number.min.violation", validation.getViolations("param1").get(0).getMessageKey());
    }
    
    @Test
    public void validationWithOptionalShouldBeAppliedInCorrectOrderPostFail() {
        when(context.getParameter("param1")).thenReturn("5");
        create("requiredIntWithOptional").invoke(mockController, context);
        verify(mockController).requiredIntWithOptional(Optional.of(5));
        assertTrue(validation.hasViolation("param1"));
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPass() {
        when(context.getParameter("param1")).thenReturn("20");
        create("requiredInt").invoke(mockController, context);
        verify(mockController).requiredInt(20);
        assertFalse(validation.hasViolations());
    }
    
    @Test
    public void validationWithOptionalShouldBeAppliedInCorrectOrderPass() {
        when(context.getParameter("param1")).thenReturn("20");
        create("requiredIntWithOptional").invoke(mockController, context);
        verify(mockController).requiredIntWithOptional(Optional.of(20));
        assertFalse(validation.hasViolations());
    }

    @Test(expected = RoutingException.class)
    public void invalidValidatorShouldBeFlagged() {
        create("badValidator").invoke(mockController, context);
    }
    
    @Test(expected = RoutingException.class)
    public void invalidValidatorWithOptionalShouldBeFlagged() {
        create("badValidatorWithOptional").invoke(mockController, context);
    }

    @Test(expected = RoutingException.class)
    public void tooManyBodiesShouldBeFlagged() {
        create("tooManyBodies").invoke(mockController, context);
    }

    @Test
    public void bodyShouldBeParsedIntoLeftOverParameter() {
        Object body = new Object();
        when(context.parseBody(Object.class)).thenReturn(body);
        create("body").invoke(mockController, context);
        verify(mockController).body(body);
    }
    
    @Test
    public void bodyWithOptionalShouldBeParsedIntoLeftOverParameter() {
        Object body = new Object();
        when(context.parseBody(Object.class)).thenReturn(body);
        create("bodyWithOptional").invoke(mockController, context);
        verify(mockController).bodyWithOptional(Optional.of(body));
    }
    
    @Test
    public void bodyWithOptionalShouldBeEmptyIfNoBodyPresent() {
        when(context.parseBody(Object.class)).thenReturn(null);
        create("bodyWithOptional").invoke(mockController, context);
        verify(mockController).bodyWithOptional(Optional.empty());
    }

    // JSR303Validation(@Pattern(regexp = "[a-z]*") String param1,
    // @Length(min = 5, max = 10) String param2, @Min(3) @Max(10) int param3);
    @Test
    public void validationPassed() {
        validateJSR303(buildDto("regex", "length", 5));
        doCheckValidationPassed(context);
    }
    
    @Test
    public void validationWithOptionalPassed() {
        validateJSR303WithOptional(buildDto("regex", "length", 5));
        doCheckValidationPassed(context);
    }
    
    private void doCheckValidationPassed(Context context) {
        assertFalse(context.getValidation().hasViolations());
        assertFalse("Expected not to have regex violation.", context.getValidation().hasViolation("regex"));
    }
    
    @Test
    public void validationFailedRegex() {
        validateJSR303(buildDto("regex!!!", "length", 5));
        docheckValidationFailedRegex(context);
    }
        
    @Test
    public void validationWithOptionalFailedRegex() {
        validateJSR303WithOptional(buildDto("regex!!!", "length", 5));
        docheckValidationFailedRegex(context);
    }

    private void docheckValidationFailedRegex(Context context) {
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getViolations().size(), 1);
        assertTrue("Expected to have regex violation.",
                context.getValidation().hasViolation("regex"));
        assertTrue(context.getValidation().getViolations().get(0).getFieldKey()
                .contentEquals("regex"));
    }
    
    @Test
    public void validationFailedLength() {
        validateJSR303(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedLength(context);
    }
    
    @Test
    public void validationWithOptionalFailedLength() {
        validateJSR303WithOptional(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedLength(context);
    }

    private void doCheckValidationFailedLength(Context context) {
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getViolations().size(), 1);
        assertTrue("Expected to have length violation.",
                context.getValidation().hasViolation("length"));
        assertTrue(context.getValidation().getViolations().get(0).getFieldKey()
                .contentEquals("length"));
    }

    @Test
    public void validationFailedRange() {
        validateJSR303(buildDto("regex", "length", 25));
        doCheckValidationFailedRange(context);
    }
    
    @Test
    public void validationWithOptionalFailedRange() {
        validateJSR303WithOptional(buildDto("regex", "length", 25));
        doCheckValidationFailedRange(context);
    }
    
    private void doCheckValidationFailedRange(Context context) {
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getViolations().size(), 1);
        assertTrue("Expected to have range violation.",
                context.getValidation().hasViolation("range"));
        assertTrue(context.getValidation().getViolations().get(0).getFieldKey()
                .contentEquals("range"));
    }

    @Test
    public void validationFailedTranslationFr() {
        when(this.context.getAcceptLanguage()).thenReturn("fr");
        validateJSR303(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationFr(context);
    }
    
    @Test
    public void validationWithOptionalFailedTranslationFr() {
        when(this.context.getAcceptLanguage()).thenReturn("fr");
        validateJSR303WithOptional(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationFr(context);
    }
    
    private void doCheckValidationFailedTranslationFr(Context context) {
        assertTrue(this.context.getValidation().hasViolations());
        assertEquals(this.context.getValidation().getViolations().size(), 1);
        assertEquals(this.context.getValidation().getViolations().get(0).getDefaultMessage(), "la taille doit être entre 5 et 10");
    }
    
    @Test
    public void validationFailedTranslationEn() {
        when(this.context.getAcceptLanguage()).thenReturn("en");
        validateJSR303(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationEn(context);
    }
    
    @Test
    public void validationWithOptionalFailedTranslationEn() {
        when(this.context.getAcceptLanguage()).thenReturn("en");
        validateJSR303WithOptional(buildDto("regex", "length - too long", 5));
        doCheckValidationFailedTranslationEn(context);
    }

    private void doCheckValidationFailedTranslationEn(Context context) {
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getViolations().size(), 1);
        assertEquals(context.getValidation().getViolations().get(0).getDefaultMessage(), "size must be between 5 and 10");
    }

    @Test
    public void validationFailedWithThreeFields() {
        validateJSR303(buildDto("regex!!!", "length is now tooooo loooong", 25));
        doCheckValidationFailedWithThreeFields(context);
    }

    @Test
    public void validationWithOptionalFailedWithThreeFields() {
        validateJSR303WithOptional(buildDto("regex!!!", "length is now tooooo loooong", 25));
        doCheckValidationFailedWithThreeFields(context);
    }
    
    private void doCheckValidationFailedWithThreeFields(Context context) {
        assertTrue(context.getValidation().hasViolations());
        assertTrue(context.getValidation().hasViolations());
        assertTrue("Expected to have regex violation.",
                context.getValidation().hasViolation("regex"));
        assertEquals(context.getValidation().getViolations().size(), 3);

        for (int i = 0; i < context.getValidation().getViolations().size(); i++) {
            String fieldName = context.getValidation().getViolations().get(i).getFieldKey();
            assertTrue(fieldName.contentEquals("regex") || fieldName.contentEquals("length")
                    || fieldName.contentEquals("range"));
        }

    }

    @Test
    public void validationFailedWithTwoAnnotations() {
        validateJSR303(buildDto("regex!!! which is also too long", "length", 5));
        doValidationFailedWithTwoAnnotations(context);
    }
    
    @Test
    public void validationWithOptionalFailedWithTwoAnnotations() {
        validateJSR303WithOptional(buildDto("regex!!! which is also too long", "length", 5));
        doValidationFailedWithTwoAnnotations(context);
    }
    
    private void doValidationFailedWithTwoAnnotations(Context context) {
        assertTrue(context.getValidation().hasViolations());
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getViolations().size(), 2);

        for (int i = 0; i < context.getValidation().getViolations().size(); i++) {
            String fieldName = context.getValidation().getViolations().get(i).getFieldKey();
            assertTrue(fieldName.contentEquals("regex"));
        }

        String message0 =
                context.getValidation().getViolations().get(0).getMessageKey();
        String message1 =
                context.getValidation().getViolations().get(1).getMessageKey();
        assertFalse(message0.contentEquals(message1));
    }

    @Test
    public void validationWithNullObject() {
        validateJSR303(null);
        assertFalse(context.getValidation().hasViolations());
        validateJSR303WithOptional(null);
        assertFalse(context.getValidation().hasViolations());
        validateJSR303WithRequired(null);
        assertTrue(context.getValidation().hasViolations());
    }

    private void validateJSR303(Dto dto) {
        when(context.parseBody(Dto.class)).thenReturn(dto);
        create("JSR303Validation", this.lang).invoke(mockController, context);
    }
    
    private void validateJSR303WithOptional(Dto dto) {
        when(context.parseBody(Dto.class)).thenReturn(dto);
        create("JSR303ValidationWithOptional", this.lang).invoke(mockController, context);
    }

    private void validateJSR303WithRequired(Dto dto) {
        when(context.parseBody(Dto.class)).thenReturn(dto);
        create("JSR303ValidationWithRequired", this.lang).invoke(mockController, context);
    }

    private Dto buildDto(String regex, String length, int range) {
        Dto dto = new Dto();
        dto.regex = regex;
        dto.length = length;
        dto.range = range;
        return dto;
    }

    private ControllerMethodInvoker create(String methodName, final Object... toBind) {
        Method method = null;
        for (Method m : MockController.class.getMethods()) {
            if (m.getName().equals(methodName)) {
                method = m;
                break;
            }
        }
        return ControllerMethodInvoker.build(method, method, Guice.createInjector(new AbstractModule() {
            @SuppressWarnings({
                    "rawtypes", "unchecked"
            })
            @Override
            protected void configure() {
                Multibinder<ParamParser> parsersBinder = Multibinder.newSetBinder(binder(), ParamParser.class);
                
                bind(NinjaProperties.class).toInstance(ninjaProperties);
                
                for (Object o : toBind) {
                    if(o instanceof Class && ParamParser.class.isAssignableFrom((Class) o)) {
                        parsersBinder.addBinding().to((Class<? extends ParamParser>) o);
                    } else {
                        bind((Class<Object>) o.getClass()).toInstance(o);
                    }
                }
            }
        }), ninjaProperties);
    }

    public enum Rainbow {
        Red, Orange, Yellow, Green, Blue, Indigo, Violet
    }

    // Custom argument extractors for testing different instantiation paths

    public interface MockController {
        public Result noParameter();

        public Result context(Context context);

        public Result session(Session session);

        public Result flash(FlashScope flash);

        public Result param(@Param("param1") String param1);

        public Result pathParam(@PathParam("param1") String param1);

        public Result sessionParam(@SessionParam("param1") String param1);

        public Result attribute(@Attribute("param1") Dep param1);

        public Result header(@Header("param1") String param1);

        public Result headers(@Headers("param1") String[] param1);

        public Result integerParam(@Param("param1") Integer param1);

        public Result intParam(@Param("param1") int param1);

        public Result shortParam(@Param("param1") Short param1);

        public Result primShortParam(@Param("param1") short param1);

        public Result characterParam(@Param("param1") Character param1);

        public Result charParam(@Param("param1") char param1);

        public Result byteParam(@Param("param1") Byte param1);

        public Result primByteParam(@Param("param1") byte param1);

        public Result booleanParam(@Param("param1") Boolean param1);
        
        public Result booleanParamWithOptional(@Param("param1") Optional<Boolean> param1);

        public Result primBooleanParam(@Param("param1") boolean param1);

        public Result longParam(@Param("param1") Long param1);

        public Result primLongParam(@Param("param1") long param1);

        public Result floatParam(@Param("param1") Float param1);

        public Result primFloatParam(@Param("param1") float param1);

        public Result doubleParam(@Param("param1") Double param1);

        public Result primDoubleParam(@Param("param1") double param1);

        public Result enumParam(@Param("param1") Rainbow param1);

        public Result enumCsvParam(@Param("param1") Rainbow[] param1);

        public Result enumArrayParam(@Params("param1") Rainbow[] param1);

        public Result noArgArgumentExtractor(@NoArg String param1);

        public Result classArgArgumentExtractor(@ClassArg String param1);

        public Result guiceArgumentExtractor(@GuiceAnnotation(foo = "bar") String param1);
        
        public Result guiceArgumentExtractorWithOptional(@GuiceAnnotation(foo = "bar") Optional<String> param1);

        public Result multiple(@Param("param1") String param1, @PathParam("param2") int param2,
                               Context context, Session session);

        public Result required(@Param("param1") @Required String param1);
        
        public Result optionalSessionParam(@SessionParam("param1") Optional<String> param1);

        public Result optionalAttribute(@Attribute("param1") Optional<Dep> param1);

        public Result optionalHeader(@Header("param1") Optional<String> param1);

        public Result optionalHeaders(@Headers("param1") Optional<String[]> param1);
        
        public Result optionalParam(@Param("param1") Optional<String> param1);
        
        public Result optionalIntegerParam(@Param("param1") Optional<Integer> param1);
        
        public Result optionalLongParam(@Param("param1") Optional<Long> param1);
        
        public Result optionalShortParam(@Param("param1") Optional<Short> param1);
        
        public Result optionalEnumParam(@Param("param1") Optional<Rainbow> param1);
        
        public Result optionalEnumArrayParam(@Param("param1") Optional<Rainbow[]> param1);
        
        public Result optionalDateParam(@Param("param1") Optional<Date> param1);
        
        public Result optionalBody(Optional<Object> body);

        public Result requiredInt(@Param("param1") @Required @NumberValue(min = 10) int param1);
        
        public Result requiredIntWithOptional(@Param("param1") @Required @NumberValue(min = 10) Optional<Integer> param1);

        public Result badValidator(@Param("param1") @NumberValue(min = 10) String param1);
        
        public Result badValidatorWithOptional(@Param("param1") @NumberValue(min = 10) Optional<String> param1);

        public Result body(Object body);
        
        public Result bodyWithOptional(Optional<Object> body);

        public Result tooManyBodies(Object body1, Object body2);

        public Result JSR303Validation(@JSR303Validation Dto dto, Validation validation);
        
        public Result JSR303ValidationWithOptional(@JSR303Validation Optional<Dto> dto, Validation validation);

        public Result JSR303ValidationWithRequired(@Required @JSR303Validation Dto dto, Validation validation);
        
        public Result dateParam(@Param("param1") Date param1);
        
        public Result dateParamWithOptional(@Param("param1") Optional<Date> param1);
        
        public Result needingInjectionParamParser(@Param("param1") Dep param1);
        
        public Result needingInjectionParamParserArray(@Params("param1") Dep[] paramsArray);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(NoArgArgumentExtractor.class)
    public @interface NoArg {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(ClassArgArgumentExtractor.class)
    public @interface ClassArg {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(GuiceArgumentExtractor.class)
    public @interface GuiceAnnotation {
        String foo();
    }

    public static class NoArgArgumentExtractor implements ArgumentExtractor<String> {
        @Override
        public String extract(Context context) {
            return "noargs";
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class ClassArgArgumentExtractor implements ArgumentExtractor<String> {
        private final Class<?> clazz;

        public ClassArgArgumentExtractor(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public String extract(Context context) {
            return clazz.getName();
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }
    

    public static class DateParamParser implements ParamParser<Date> {

        public static final String DATE_FORMAT = "dd/MM/yyyy";
        
        public static final String KEY = "validation.is.date.violation";
        
        public static final String MESSAGE = "{0} must be a valid date";
        
        @Override
        public Date parseParameter(String field, String parameterValue, Validation validation) {
            try {
                return parameterValue == null ? null : new SimpleDateFormat(DATE_FORMAT).parse(parameterValue);
            } catch(ParseException e) {
                validation.addViolation(new ConstraintViolation(KEY, field, MESSAGE, parameterValue));
                return null;
            }
        }

        @Override
        public Class<Date> getParsedType() {
            return Date.class;
        }

    }
    
    public static class NeedingInjectionParamParser implements ParamParser<Dep> {

        // In a real application, you can also use @Named as each properties is binded by its name
        @Inject
        NinjaProperties properties;
        
        @Override
        public Dep parseParameter(String field, String parameterValue, Validation validation) {
            return new Dep(properties.get("needingInjectionParamParser.value") + "_" + parameterValue);
        }

        @Override
        public Class<Dep> getParsedType() {
            return Dep.class;
        }
        
    }

    /**
     * Argument extractor that has a complex constructor for Guice. It depends on some
     * other dependency (dep), plus the annotation that was on the parameter, and the
     * class of the parameter.
     */
    public static class GuiceArgumentExtractor implements ArgumentExtractor<String> {
        private final Dep dep;
        private final GuiceAnnotation annot;
        private final Class clazz;

        @Inject
        public GuiceArgumentExtractor(Dep dep, GuiceAnnotation annot, ArgumentClassHolder holder) {
            this.dep = dep;
            this.annot = annot;
            this.clazz = holder.getArgumentClass();
        }

        @Override
        public String extract(Context context) {
            return dep.value() + ":" + annot.foo() + ":" + clazz.getName();
        }

        @Override
        public Class<String> getExtractedType() {
            return String.class;
        }

        @Override
        public String getFieldName() {
            return null;
        }
    }

    public static class Dep {

        private final String value;

        public Dep(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Dep other = (Dep) obj;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }
    }

    public class Dto {

        @Size(min = 1, max = 10)
        @Pattern(regexp = "[a-z]*")
        public String regex;

        @Size(min = 5, max = 10)
        public String length;

        @Min(value = 3)
        @Max(value = 10)
        public int range;
    }
}
