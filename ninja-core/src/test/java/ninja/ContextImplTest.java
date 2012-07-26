package ninja;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;

import ninja.utils.NinjaConstant;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;

@RunWith(MockitoJUnitRunner.class)
public class ContextImplTest {
    
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
    private Route route;

    @Mock
    private ResultHandler resultHandler;

    @Mock
    private Validation validation;

    private ContextImpl context;

    @Before
    public void setUp() {
        //default setup for httpServlet request.
        //According to servlet spec the following will be returned:
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(httpServletRequest.getRequestURI()).thenReturn("/");
        
        
        context = new ContextImpl(bodyParserEngineManager, flashCookie, sessionCookie,
                resultHandler, validation);
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
        //context.addCookie(cookie);

        //generate an arbitrary result:
        Result result = Results.html();
        result.addCookie(cookie);
        
        //finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(result);

        //and verify the stuff:
        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(javax.servlet.http.Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        javax.servlet.http.Cookie resultCookie = cookieCaptor.getValue();
        assertThat(resultCookie.getName(), equalTo("cookie"));
        assertThat(resultCookie.getValue(), equalTo("yum"));
        assertThat(resultCookie.getPath(), equalTo("/"));
        assertThat(resultCookie.getSecure(), equalTo(false));
        assertThat(resultCookie.getMaxAge(), equalTo(-1));
    }
    
    
    @Test
    public void testGetPathParameter() {
    	//init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "parameter");
        
        //and return the parameter map when any parameter is called...
        when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);	
		
        context.setRoute(route);
        
        //this parameter is not there and must return null
        assertEquals(null, context.getPathParameter("parameter_not_set")); 
        
        assertEquals("parameter", context.getPathParameter("parameter")); 

    }
    
    @Test
    public void testGetPathParameterDecodingWorks() {
        //init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "blue%2Fred%3Fand+green%E2%82%AC%2f");
        
        //and return the parameter map when any parameter is called...
        when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);   
        
        context.setRoute(route);
        
        //that is how the above parameter looks decoded correctly:
        assertEquals("blue/red?and+green€/", context.getPathParameter("parameter")); 

    }
    
    
    @Test
    public void testGetPathParameterAsInteger() {
    	//init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "parameter");
        
        //and return the parameter map when any parameter is called...
        when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);	
		
        context.setRoute(route);
        
        //this will not work and return null
        assertEquals(null, context.getPathParameterAsInteger("parameter")); 
        
        //now set an integer into the parametermap:
        parameterMap.put("parameter", "1");
        
        //this will work and return 1
        assertEquals(new Integer(1), context.getPathParameterAsInteger("parameter")); 
        
    }
    
    @Test
    public void testGetParameter() {
    	
    	//init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //and return the parameter map when any parameter is called...
        when(httpServletRequest.getParameter("key")).thenReturn("value");	

        //this will not work and return null
        assertEquals(null, context.getParameter("key_not_there")); 
        
        //this will return the default value:
        assertEquals("defaultValue", context.getParameter("key_not_there", "defaultValue")); 
        
        //this will work as the value is there...
        assertEquals("value", context.getParameter("key")); 

    }
    
    @Test
    public void testGetParameterAsInteger() {
    	
    	//init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //and return the parameter map when any parameter is called...
        when(httpServletRequest.getParameter("key")).thenReturn("1");	

        //this will not work and return null
        assertEquals(null, context.getParameterAsInteger("key_not_there")); 
        
        //this will return the default value:
        assertEquals(new Integer(100), context.getParameterAsInteger("key_not_there", 100)); 
        
        //this will work as the value is there...
        assertEquals(new Integer(1), context.getParameterAsInteger("key"));  
    	
    }
    
    @Test
    public void testContentTypeGetsConvertedProperlyUponFinalize() {
        
        //init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //this must be Content-Type: application/json; encoding=utf-8
        Result result = Results.json();
        
        context.finalizeHeaders(result);
        
        verify(httpServletResponse).setCharacterEncoding(result.getCharset());
        verify(httpServletResponse).setContentType(result.getContentType());
    }
    
    
    @Test
    public void testContentTypeWithNullEncodingGetsConvertedProperlyUponFinalize() {
        
        //init the context
        context.init(httpServletRequest, httpServletResponse);
        
        //this must be Content-Type: application/json; encoding=utf-8
        Result result = Results.json();
        //force a characterset that is not there. Stupid but tests that its working.
        result.charset(null);
        
        context.finalizeHeaders(result);
        
        //make sure utf-8 is used under all circumstances:
        verify(httpServletResponse).setCharacterEncoding(NinjaConstant.UTF_8);
    }
    
    
    @Test
    public void testGetRequestPathWorksAsExpectedWithContext() {
        
        // we got a context
        when(httpServletRequest.getContextPath()).thenReturn("/my/funky/prefix");
        
        // we got a request uri
        when(httpServletRequest.getRequestURI()).thenReturn("/my/funky/prefix/myapp/is/here");       
        
        
        context.init(httpServletRequest, httpServletResponse);
        

        assertEquals("/myapp/is/here", context.getRequestPath());
        
    }
    
    @Test
    public void testGetRequestPathWorksAsExpectedWithOutContext() {
        
        // we got not context.
        // according to spec it will return an empty string
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(httpServletRequest.getRequestURI()).thenReturn("/index");
        // we got a request uri
        when(httpServletRequest.getRequestURI()).thenReturn("/myapp/is/here");       
        
        
        context.init(httpServletRequest, httpServletResponse);
        

        assertEquals("/myapp/is/here", context.getRequestPath());
        
    }

}
