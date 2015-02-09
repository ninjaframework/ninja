package ninja.template;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ninja.Context;
import ninja.session.Session;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import freemarker.template.TemplateModelException;

@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerAuthenticityMethodTest {

    @Rule 
    public ExpectedException thrown= ExpectedException.none();
    
    @Mock
    private Context context;
    
    @Mock
    private Session session;
    
    private TemplateEngineFreemarkerAuthenticityMethod templateEngineFreemarkerAuthenticityMethod;
    private String TOKEN = "foo";
    
    @Before
    public void init() {
        when(context.getSession()).thenReturn(session);
        when(session.getAuthenticityToken()).thenReturn(TOKEN);
        
        templateEngineFreemarkerAuthenticityMethod = new TemplateEngineFreemarkerAuthenticityMethod(this.context);
    }
    
    @Test
    public void testThatNoKeyYieldsException() throws Exception {
        List args = Collections.EMPTY_LIST;
        thrown.expect(TemplateModelException.class);
        templateEngineFreemarkerAuthenticityMethod.exec(args);
    }
    
    @Test
    public void testForm() throws Exception {
        List args = new ArrayList<String>();
        args.add("form");
        String form = templateEngineFreemarkerAuthenticityMethod.exec(args);
        
        assertEquals("<input type=\"hidden\" value=\"foo\" name=\"authenticityToken\" />", form);
    }
    
    @Test
    public void testToken() throws Exception {
        List args = new ArrayList<String>();
        args.add("token");
        
        String token = templateEngineFreemarkerAuthenticityMethod.exec(args);
        assertEquals(token, TOKEN);
    }
}