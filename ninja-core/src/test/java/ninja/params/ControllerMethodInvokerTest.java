package ninja.params;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import ninja.Context;
import ninja.Result;

import ninja.Validation;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ControllerMethodInvokerTest {

    @Mock
	private MockController mockController;
    @Mock
    private Context context;
    @Mock
    private SessionCookie session;
    @Mock
    private FlashCookie flash;
    @Mock
    private Validation validation;

    @Before
    public void setUp() throws Exception {
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
    public void integerParamShouldBeParsedToInteger() throws Exception {
        when(context.getParameter("param1")).thenReturn("20");
        create("integerParam").invoke(mockController, context);
        verify(mockController).integerParam(20);
    }

    @Test
    public void integerParamShouldHandleNull() throws Exception {
        create("integerParam").invoke(mockController, context);
        verify(mockController).integerParam(null);
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void integerValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("integerParam").invoke(mockController, context);
        verify(mockController).integerParam(null);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void intValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("intParam").invoke(mockController, context);
        verify(mockController).intParam(0);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void longValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("longParam").invoke(mockController, context);
        verify(mockController).longParam(null);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void primLongValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primLongParam").invoke(mockController, context);
        verify(mockController).primLongParam(0L);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void floatValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("floatParam").invoke(mockController, context);
        verify(mockController).floatParam(null);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void primFloatValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primFloatParam").invoke(mockController, context);
        verify(mockController).primFloatParam(0);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void doubleValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("doubleParam").invoke(mockController, context);
        verify(mockController).doubleParam(null);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        verify(validation, never()).addFieldError(anyString(), anyString(), any(Object[].class));
    }

    @Test
    public void primDoubleValidationShouldWork() throws Exception {
        when(context.getParameter("param1")).thenReturn("blah");
        create("primDoubleParam").invoke(mockController, context);
        verify(mockController).primDoubleParam(0);
        verify(validation).addFieldError(eq("param1"), anyString(), eq("blah"));
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
        public Result session(SessionCookie session);
        public Result flash(FlashCookie flash);
        public Result param(@Param("param1") String param1);
        public Result pathParam(@PathParam("param1") String param1);
        public Result sessionParam(@SessionParam("param1") String param1);
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
                Context context, SessionCookie session);
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

}
