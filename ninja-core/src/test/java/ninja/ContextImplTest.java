/**
 * Copyright (C) 2012 the original author or authors.
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

package ninja;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

    @Test
    public void testGetRequestContentType() {
        String contentType = "text/html";
        when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(contentType, context.getRequestContentType());

        contentType = null;
        when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(httpServletRequest, httpServletResponse);

        assertNull(context.getRequestContentType());

        contentType = "text/html; charset=UTF-8";
        when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(contentType, context.getRequestContentType());
    }

    @Test
    public void testGetAcceptContentType() {
        when(httpServletRequest.getHeader("accept")).thenReturn(null);
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("totally_unknown");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("application/json");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.APPLICATON_JSON, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("text/html, application/json");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("application/xhtml, application/json");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("text/plain");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_PLAIN, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("text/plain, application/json");
        context.init(httpServletRequest, httpServletResponse);
        assertEquals(Result.APPLICATON_JSON, context.getAcceptContentType());
    }

    @Test
    public void testGetAcceptEncoding() {
        String encoding = "compress, gzip";
        when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(encoding, context.getAcceptEncoding());

        encoding = null;
        when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(httpServletRequest, httpServletResponse);

        assertNull(context.getAcceptEncoding());

        encoding = "gzip;q=1.0, identity; q=0.5, *;q=0";
        when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(encoding, context.getAcceptEncoding());
    }

    @Test
    public void testGetAcceptLanguage() {
        String language = "de";
        when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(language, context.getAcceptLanguage());

        language = null;
        when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(httpServletRequest, httpServletResponse);

        assertNull(context.getAcceptLanguage());

        language = "da, en-gb;q=0.8, en;q=0.7";
        when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(language, context.getAcceptLanguage());
    }

    @Test
    public void testGetAcceptCharset() {
        String charset = "UTF-8";
        when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(charset, context.getAcceptCharset());

        charset = null;
        when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(httpServletRequest, httpServletResponse);

        assertNull(context.getAcceptCharset());

        charset = "iso-8859-5, unicode-1-1;q=0.8";
        when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(httpServletRequest, httpServletResponse);

        assertEquals(charset, context.getAcceptCharset());
    }

}
