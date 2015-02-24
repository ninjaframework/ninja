package ninja.template;

import com.google.common.collect.Maps;
import java.util.Map;

import ninja.Context;
import ninja.session.Session;
import ninja.template.directives.TemplateEngineFreemarkerAuthenticityFormDirective;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import freemarker.core.Environment;
import freemarker.template.Template;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.io.StringWriter;
import ninja.template.directives.TemplateEngineFreemarkerAuthenticityTokenDirective;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerAuthenticityTokenDirectiveTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Context context;

    @Mock
    Session session;

    StringWriter stringWriter = new StringWriter();

    Environment environment = new Environment(Mockito.mock(Template.class), null, stringWriter);

    TemplateEngineFreemarkerAuthenticityTokenDirective templateEngineFreemarkerAuthenticityTokenDirective;

    Map<String, String> parameters = Maps.newHashMap();

    @Before
    public void before() {
        when(context.getSession()).thenReturn(session);
        when(session.getAuthenticityToken()).thenReturn("12345");

        templateEngineFreemarkerAuthenticityTokenDirective = new TemplateEngineFreemarkerAuthenticityTokenDirective(context);
    }

    @Test
    public void testThatAuthenticityTokenIsNotCalledInConstructor() throws Exception {
        Mockito.verify(session, Mockito.never()).getAuthenticityToken();
    }
    
    @Test
    public void testThatItWorks() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[0];

        templateEngineFreemarkerAuthenticityTokenDirective.execute(environment, parameters, loopVars, null);

        assertThat(
                stringWriter.toString(),
                equalTo("12345"));
        Mockito.verify(session).getAuthenticityToken();
    }
    


    @Test
    public void testThatParamsThrowException() throws Exception {
        thrown.expect(TemplateException.class);
        parameters.put("foo", "bar");

        templateEngineFreemarkerAuthenticityTokenDirective.execute(null, parameters, null, null);
    }

    @Test
    public void testThatLoopVarsThrowException() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[1];
        thrown.expect(TemplateException.class);
        loopVars[0] = new TemplateModel() {};

        templateEngineFreemarkerAuthenticityTokenDirective.execute(null, parameters, loopVars, null);
    }

}
