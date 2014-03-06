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

package ninja.params;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import ninja.Context;
import ninja.Result;
import ninja.RoutingException;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.validation.JSR303Validation;
import ninja.validation.NumberValue;
import ninja.validation.Required;
import ninja.validation.Validation;
import ninja.validation.ValidationImpl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;


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

    private Validation validation;

    @Before
    public void setUp() throws Exception {
        validation = new ValidationImpl();
        when(context.getSessionCookie()).thenReturn(session);
        when(context.getFlashCookie()).thenReturn(flash);
        when(context.getValidation()).thenReturn(validation);
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
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
        assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void validationShouldPassWhenGoodRequest() {
        when(context.getParameter("param1")).thenReturn("value");
        create("required").invoke(mockController, context);
        verify(mockController).required("value");
        assertFalse(validation.hasViolations());
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPreFail() {
        create("requiredInt").invoke(mockController, context);
        verify(mockController).requiredInt(0);
        assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPostFail() {
        when(context.getParameter("param1")).thenReturn("5");
        create("requiredInt").invoke(mockController, context);
        verify(mockController).requiredInt(5);
        assertTrue(validation.hasFieldViolation("param1"));
    }

    @Test
    public void validationShouldBeAppliedInCorrectOrderPass() {
        when(context.getParameter("param1")).thenReturn("20");
        create("requiredInt").invoke(mockController, context);
        verify(mockController).requiredInt(20);
        assertFalse(validation.hasViolations());
    }

    @Test(expected = RoutingException.class)
    public void invalidValidatorShouldBeFlagged() {
        create("badValidator").invoke(mockController, context);
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

    // JSR303Validation(@Pattern(regexp = "[a-z]*") String param1,
    // @Length(min = 5, max = 10) String param2, @Min(3) @Max(10) int param3);
    @Test
    public void validationPassed() {
        validateJSR303(buildDto("regex", "length", 5));
        assertFalse(context.getValidation().hasViolations());
        assertFalse("Expected not to have regex violation.",
                context.getValidation().hasBeanViolation("regex"));
    }

    @Test
    public void validationFailedRegex() {
        validateJSR303(buildDto("regex!!!", "length", 5));
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getBeanViolations().size(), 1);
        assertTrue("Expected to have regex violation.",
                context.getValidation().hasBeanViolation("regex"));
        assertTrue(context.getValidation().getBeanViolations().get(0).field
                .contentEquals("regex"));
    }

    @Test
    public void validationFailedLength() {
        validateJSR303(buildDto("regex", "length - too long", 5));
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getBeanViolations().size(), 1);
        assertTrue("Expected to have length violation.",
                context.getValidation().hasBeanViolation("length"));
        assertTrue(context.getValidation().getBeanViolations().get(0).field
                .contentEquals("length"));
    }

    @Test
    public void validationFailedRange() {
        validateJSR303(buildDto("regex", "length", 25));
        assertTrue(context.getValidation().hasViolations());
        assertEquals(context.getValidation().getBeanViolations().size(), 1);
        assertTrue("Expected to have range violation.",
                context.getValidation().hasBeanViolation("range"));
        assertTrue(context.getValidation().getBeanViolations().get(0).field
                .contentEquals("range"));
    }

    @Test
    public void validationFailedWithThreeFields() {
        validateJSR303(buildDto("regex!!!", "length is now tooooo loooong", 25));
        assertTrue(context.getValidation().hasViolations());
        assertTrue(context.getValidation().hasBeanViolations());
        assertTrue("Expected to have regex violation.",
                context.getValidation().hasBeanViolation("regex"));
        assertEquals(context.getValidation().getBeanViolations().size(), 3);

        for (int i = 0; i < context.getValidation().getBeanViolations().size(); i++) {
            String fieldName = context.getValidation().getBeanViolations().get(i).field;
            assertTrue(fieldName.contentEquals("regex") || fieldName.contentEquals("length")
                    || fieldName.contentEquals("range"));
        }

    }

    @Test
    public void validationFailedWithTwoAnnotations() {
        validateJSR303(buildDto("regex!!! which is also too long", "length", 5));
        assertTrue(context.getValidation().hasViolations());
        assertTrue(context.getValidation().hasBeanViolations());
        assertEquals(context.getValidation().getBeanViolations().size(), 2);

        for (int i = 0; i < context.getValidation().getBeanViolations().size(); i++) {
            String fieldName = context.getValidation().getBeanViolations().get(i).field;
            assertTrue(fieldName.contentEquals("regex"));
        }

        String message0 =
                context.getValidation().getBeanViolations().get(0).constraintViolation
                        .getMessageKey();
        String message1 =
                context.getValidation().getBeanViolations().get(1).constraintViolation
                        .getMessageKey();
        assertFalse(message0.contentEquals(message1));
    }

    @Test
    public void validationWithNullObject() {
        validateJSR303(null);
        assertFalse(context.getValidation().hasViolations());
        validateJSR303WithRequired(null);
        assertTrue(context.getValidation().hasViolations());
    }

    private void validateJSR303(Dto dto) {
        when(context.parseBody(Dto.class)).thenReturn(dto);
        create("JSR303Validation").invoke(mockController, context);
    }

    private void validateJSR303WithRequired(Dto dto) {
        when(context.parseBody(Dto.class)).thenReturn(dto);
        create("JSR303ValidationWithRequired").invoke(mockController, context);
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
        return ControllerMethodInvoker.build(method, Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                for (Object o : toBind) {
                    bind((Class<Object>) o.getClass()).toInstance(o);
                }
            }
        }));
    }

    public interface MockController {
        public Result noParameter();
        public Result context(Context context);
        public Result session(Session session);
        public Result flash(FlashScope flash);
        public Result param(@Param("param1") String param1);
        public Result pathParam(@PathParam("param1") String param1);
        public Result sessionParam(@SessionParam("param1") String param1);
        public Result attribute(@Attribute("param1") Dep param1);
        public Result integerParam(@Param("param1") Integer param1);
        public Result intParam(@Param("param1") int param1);
        public Result booleanParam(@Param("param1") Boolean param1);
        public Result primBooleanParam(@Param("param1") boolean param1);
        public Result longParam(@Param("param1") Long param1);
        public Result primLongParam(@Param("param1") long param1);
        public Result floatParam(@Param("param1") Float param1);
        public Result primFloatParam(@Param("param1") float param1);
        public Result doubleParam(@Param("param1") Double param1);
        public Result primDoubleParam(@Param("param1") double param1);
        public Result noArgArgumentExtractor(@NoArg String param1);
        public Result classArgArgumentExtractor(@ClassArg String param1);
        public Result guiceArgumentExtractor(@GuiceAnnotation(foo = "bar") String param1);
        public Result multiple(@Param("param1") String param1, @PathParam("param2") int param2,
                Context context, Session session);
        public Result required(@Param("param1") @Required String param1);
        public Result requiredInt(@Param("param1") @Required @NumberValue(min = 10) int param1);
        public Result badValidator(@Param("param1") @NumberValue(min = 10) String param1);
        public Result body(Object body);
        public Result tooManyBodies(Object body1, Object body2);

        public Result JSR303Validation(@JSR303Validation Dto dto, Validation validation);

        public Result JSR303ValidationWithRequired(@Required @JSR303Validation Dto dto,
                Validation validation);
    }

    // Custom argument extractors for testing different instantiation paths

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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(NoArgArgumentExtractor.class)
    public @interface NoArg {}

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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(ClassArgArgumentExtractor.class)
    public @interface ClassArg {}

    public class Dep {
        private final String value;

        public Dep(String value) {
            this.value = value;
        }

        public String value() {
            return value;
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

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @WithArgumentExtractor(GuiceArgumentExtractor.class)
    public @interface GuiceAnnotation {
        String foo();
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
