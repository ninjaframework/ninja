package ninja;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.template.TemplateEngineManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

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
    
    @Mock 
    private Logger logger;

    private ContextImpl context;

    @Before
    public void setUp() {
        context = new ContextImpl(bodyParserEngineManager, flashCookie, logger, sessionCookie, templateEngineManager);
    }

    @Test
    public void testGetRequestUri() {
        
        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRequestURI()).thenReturn("/index");
        
        //init the context from a (mocked) servlet
        context.init(httpServletRequest, httpServletResponse);
        
        //make sure this is correct
        assertEquals("/index", context.getRequestUri());
    }

    @Test
    public void testAddCookie() {
        Cookie cookie = Cookie.builder("cookie", "yum").setDomain("domain").build();
        context.init(httpServletRequest, httpServletResponse);
        context.addCookie(cookie);

        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(javax.servlet.http.Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        javax.servlet.http.Cookie result = cookieCaptor.getValue();
        assertThat(result.getName(), equalTo("cookie"));
        assertThat(result.getValue(), equalTo("yum"));
        assertThat(result.getPath(), nullValue());
        assertThat(result.getSecure(), equalTo(false));
        assertThat(result.getMaxAge(), equalTo(-1));

    }

}
