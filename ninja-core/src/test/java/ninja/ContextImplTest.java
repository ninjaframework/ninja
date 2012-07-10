package ninja;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.template.TemplateEngineManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ContextImplTest {
    
    @Mock
    private TemplateEngineManager templateEngineManager;
    
    @Mock
    private SessionCookie sessionCookie;
    
    @Mock
    private FlashCookie flashCookie;
    
    @Mock
    private BodyParserEngineManager bodyParserEngineManager;    
    
    @Mock
    private HttpServletRequest httpServletRequest;
    
    @Mock
    private HttpServletResponse httpServletResponse;
    
    @Test
    public void testGetRequestUri() {
        
        //make a new context (all mocks)
        ContextImpl context = new ContextImpl(bodyParserEngineManager, flashCookie, sessionCookie, templateEngineManager);
        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRequestURI()).thenReturn("/index");
        
        //init the context from a (mocked) servlet
        context.init(httpServletRequest, httpServletResponse);
        
        //make sure this is correct
        assertEquals("/index", context.getRequestUri());
        
        
    }

}
