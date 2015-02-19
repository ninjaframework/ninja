package ninja.template;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;
import ninja.session.Session;
import ninja.template.directives.TemplateEngineFreemarkerAuthenticityFormDirective;
import ninja.template.directives.TemplateEngineFreemarkerAuthenticityTokenDirective;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * 
 * @author svenkubiak
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerAuthenticityTest {
    private static final String BAR = "bar";
    private static final String FOO = "foo";

    @Rule 
    public ExpectedException thrown = ExpectedException.none();
    
    @Mock
    private Context context;
    
    @Mock
    private Session session;
    
    private TemplateModel[] loopVars = new TemplateModel [1];
    
    private TemplateEngineFreemarkerAuthenticityFormDirective authenticityFormDirective;
    
    private TemplateEngineFreemarkerAuthenticityTokenDirective authenticityTokenDirective;
    
    private Map<String, String> params = new HashMap<String, String>();
    
    @Before
    public void before() {
        Mockito.when(context.getSession()).thenReturn(session);
        Mockito.when(context.getSession().getAuthenticityToken()).thenReturn(FOO);

        authenticityFormDirective
        = Mockito.spy(new TemplateEngineFreemarkerAuthenticityFormDirective(context));
        
        authenticityTokenDirective
        = Mockito.spy(new TemplateEngineFreemarkerAuthenticityTokenDirective(context));
    }
    
    @Test
    public void testThatParamsExceptionToken() throws Exception {
        thrown.expect(TemplateException.class);
        params.put(FOO, BAR);

        authenticityFormDirective.execute(null, params, null, null);
    }
    
    @Test
    public void testThatParamsExceptionForm() throws Exception {
        thrown.expect(TemplateException.class);
        params.put(FOO, BAR);

        authenticityTokenDirective.execute(null, params, null, null);
    }
    
    @Test
    public void testThatLoopVarsExceptionToken() throws Exception {
        thrown.expect(TemplateException.class);
        loopVars [0] = new TemplateModel() {};

        authenticityFormDirective.execute(null, params, loopVars, null);
    }
    
    @Test
    public void testThatLoopVarsExceptionForm() throws Exception {
        thrown.expect(TemplateException.class);
        loopVars [0] = new TemplateModel() {};

        authenticityTokenDirective.execute(null, params, loopVars, null);
    }
}