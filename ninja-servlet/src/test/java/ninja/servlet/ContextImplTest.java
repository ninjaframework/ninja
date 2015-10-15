/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.servlet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.ContentTypes;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
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
    private Session sessionCookie;

    @Mock
    private FlashScope flashCookie;

    @Mock
    private BodyParserEngineManager bodyParserEngineManager;

    @Mock
    private ServletContext servletContext;

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

    @Mock
    private BodyParserEngine bodyParserEngine;
    
    @Mock
    private NinjaProperties ninjaProperties;

    private ContextImpl context;

    @Before
    public void setUp() {
        //default setup for httpServlet request.
        //According to servlet spec the following will be returned:
        when(httpServletRequest.getContextPath()).thenReturn("");
        when(httpServletRequest.getRequestURI()).thenReturn("/");


        context = new ContextImpl(
                bodyParserEngineManager, 
                flashCookie, 
                ninjaProperties,
                resultHandler, 
                sessionCookie,
                validation,
                null);
    }

    @Test
    public void testGetRequestUri() {

        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRequestURI()).thenReturn("/index");

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //make sure this is correct
        assertEquals("/index", context.getRequestPath());
    }

    @Test
    public void testGetHostname() {

        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getHeader("host")).thenReturn("test.com");

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //make sure this is correct
        assertEquals("test.com", context.getHostname());
    }

    @Test
    public void testGetRemoteAddrReturnsDefaultRemoteAddr() {

        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        when(httpServletRequest.getHeader(Context.X_FORWARD_HEADER)).thenReturn("x-forwarded-for-mockedRemoteAddr");

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //make sure this is correct
        assertEquals("mockedRemoteAddr", context.getRemoteAddr());
    }
    
    @Test
    public void testGetRemoteAddrParsesXForwardedForIfSetInApplicationConf() {

        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        when(httpServletRequest.getHeader(Context.X_FORWARD_HEADER)).thenReturn("192.168.1.44");

        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false))
                .thenReturn(Boolean.TRUE);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //make sure this is correct
        assertEquals("192.168.1.44", context.getRemoteAddr());
    }
    
    @Test
    public void testGetRemoteAddrParsesXForwardedForIfMoreThanOneHostPresent() {

        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        when(httpServletRequest.getHeader(Context.X_FORWARD_HEADER)).thenReturn("192.168.1.1, 192.168.1.2, 192.168.1.3");

        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false))
                .thenReturn(Boolean.TRUE);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //make sure this is correct
        assertEquals("192.168.1.1", context.getRemoteAddr());
    }
    
    @Test
    public void testGetRemoteAddrUsesFallbackIfXForwardedForIsNotValidInetAddr() {

        //say the httpServletRequest to return a certain value:
        when(httpServletRequest.getRemoteAddr()).thenReturn("mockedRemoteAddr");
        when(httpServletRequest.getHeader(Context.X_FORWARD_HEADER)).thenReturn("I_AM_NOT_A_VALID_ADDRESS");

        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false))
                .thenReturn(Boolean.TRUE);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //make sure this is correct
        assertEquals("mockedRemoteAddr", context.getRemoteAddr());
    }

    @Test
    public void testAddCookieViaResult() {
        Cookie cookie = Cookie.builder("cookie", "yum").setDomain("domain").build();
        context.init(servletContext, httpServletRequest, httpServletResponse);
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
    public void testAddCookieViaContext() {
        Cookie cookie = Cookie.builder("cookie", "yummy").setDomain("domain").build();
        context.init(servletContext, httpServletRequest, httpServletResponse);
        context.addCookie(cookie);
        
        //finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(Results.html());
        
        //and verify the stuff:
        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(javax.servlet.http.Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        javax.servlet.http.Cookie resultCookie = cookieCaptor.getValue();
        assertThat(resultCookie.getName(), equalTo("cookie"));
        assertThat(resultCookie.getValue(), equalTo("yummy"));
        assertThat(resultCookie.getPath(), equalTo("/"));
        assertThat(resultCookie.getSecure(), equalTo(false));
        assertThat(resultCookie.getMaxAge(), equalTo(-1));
    }
    
    @Test
    public void testUnsetCookieViaContext() {
        Cookie cookie = Cookie.builder("cookie", "yummy").setDomain("domain").build();
        context.init(servletContext, httpServletRequest, httpServletResponse);
        context.unsetCookie(cookie);
        
        //finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(Results.html());

        //and verify the stuff:
        ArgumentCaptor<javax.servlet.http.Cookie> cookieCaptor = ArgumentCaptor.forClass(javax.servlet.http.Cookie.class);
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        javax.servlet.http.Cookie resultCookie = cookieCaptor.getValue();
        assertThat(resultCookie.getName(), equalTo("cookie"));
        assertThat(resultCookie.getValue(), equalTo("yummy"));
        assertThat(resultCookie.getPath(), equalTo("/"));
        assertThat(resultCookie.getSecure(), equalTo(false));
        assertThat(resultCookie.getMaxAge(), equalTo(0));
    }

    @Test
    public void getCookieTest() {
        javax.servlet.http.Cookie servletCookie1 = new javax.servlet.http.Cookie("contextCookie1", "theValue1");
        javax.servlet.http.Cookie servletCookie2 = new javax.servlet.http.Cookie("contextCookie2", "theValue2");
        javax.servlet.http.Cookie [] servletCookies = {servletCookie1, servletCookie2};

        when(httpServletRequest.getCookies()).thenReturn(servletCookies);

        context.init(servletContext, httpServletRequest, httpServletResponse);

        // negative test:
        ninja.Cookie doesNotExist = context.getCookie("doesNotExist");
        assertNull(doesNotExist);

        // test  against cookie that is really there
        ninja.Cookie cookie1 = context.getCookie("contextCookie1");

        assertEquals(cookie1.getName(), "contextCookie1");
        assertEquals(cookie1.getValue(), "theValue1");

        // test 2 against cookie that is really there
        ninja.Cookie cookie2= context.getCookie("contextCookie2");

        assertEquals(cookie2.getName(), "contextCookie2");
        assertEquals(cookie2.getValue(), "theValue2");
    }


    @Test
    public void hasCookieTest() {
        javax.servlet.http.Cookie servletCookie1 = new javax.servlet.http.Cookie("contextCookie1", "theValue1");
        javax.servlet.http.Cookie servletCookie2 = new javax.servlet.http.Cookie("contextCookie2", "theValue2");
        javax.servlet.http.Cookie [] servletCookies = {servletCookie1, servletCookie2};

        when(httpServletRequest.getCookies()).thenReturn(servletCookies);

        context.init(servletContext, httpServletRequest, httpServletResponse);

        // negative test:
        assertFalse(context.hasCookie("doesNotExist"));

        // test  against cookie that is really there
        assertTrue(context.hasCookie("contextCookie1"));

        // test 2 against cookie that is really there
        assertTrue(context.hasCookie("contextCookie2"));
    }

    @Test
    public void getCookiesTest() {

        javax.servlet.http.Cookie servletCookie1 = new javax.servlet.http.Cookie("contextCookie1", "theValue");
        javax.servlet.http.Cookie servletCookie2 = new javax.servlet.http.Cookie("contextCookie2", "theValue");
        javax.servlet.http.Cookie [] servletCookiesEmpty = {};
        javax.servlet.http.Cookie [] servletCookies = {servletCookie1, servletCookie2};

        when(httpServletRequest.getCookies()).thenReturn(servletCookiesEmpty);

        context.init(servletContext, httpServletRequest, httpServletResponse);

        //test when there are no cookies.
        assertEquals(0, context.getCookies().size());

        // now return some cookies:
        when(httpServletRequest.getCookies()).thenReturn(servletCookies);


        assertEquals(2, context.getCookies().size());


    }


    @Test
    public void testGetPathParameter() {
    	//init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);

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
        context.init(servletContext, httpServletRequest, httpServletResponse);

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
        context.init(servletContext, httpServletRequest, httpServletResponse);

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
        context.init(servletContext, httpServletRequest, httpServletResponse);

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
        context.init(servletContext, httpServletRequest, httpServletResponse);

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
    public void testGetParameterAs() {
        //init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //and return the parameter map when any parameter is called...
        when(httpServletRequest.getParameter("key1")).thenReturn("100");
        when(httpServletRequest.getParameter("key2")).thenReturn("true");
        when(httpServletRequest.getParameter("key3")).thenReturn("10.1");
        when(httpServletRequest.getParameter("key4")).thenReturn("x");

        //this will not work and return null
        assertEquals(null, context.getParameterAs("key", Long.class));

        assertEquals(new Integer(100), context.getParameterAs("key1", Integer.class));
        assertEquals(new Long(100), context.getParameterAs("key1", Long.class));
        assertEquals(Boolean.TRUE, context.getParameterAs("key2", Boolean.class));
        assertEquals(new Float(10.1), context.getParameterAs("key3", Float.class));
        assertEquals(new Character('x'), context.getParameterAs("key4", Character.class));
    }

    @Test
    public void testContentTypeGetsConvertedProperlyUponFinalize() {

        //init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //this must be Content-Type: application/json; encoding=utf-8
        Result result = Results.json();

        context.finalizeHeaders(result);

        verify(httpServletResponse).setCharacterEncoding(result.getCharset());
        verify(httpServletResponse).setContentType(result.getContentType());
    }


    @Test
    public void testContentTypeWithNullEncodingGetsConvertedProperlyUponFinalize() {

        //init the context
        context.init(servletContext, httpServletRequest, httpServletResponse);

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


        context.init(servletContext, httpServletRequest, httpServletResponse);


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


        context.init(servletContext, httpServletRequest, httpServletResponse);


        assertEquals("/myapp/is/here", context.getRequestPath());

    }

    @Test
    public void testGetRequestContentType() {
        String contentType = "text/html";
        when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(contentType, context.getRequestContentType());

        contentType = null;
        when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertNull(context.getRequestContentType());

        contentType = "text/html; charset=UTF-8";
        when(httpServletRequest.getContentType()).thenReturn(contentType);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(contentType, context.getRequestContentType());
    }

    @Test
    public void testGetAcceptContentType() {
        when(httpServletRequest.getHeader("accept")).thenReturn(null);
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("totally_unknown");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.APPLICATION_JSON, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("text/html, application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("application/xhtml, application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("text/plain");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.TEXT_PLAIN, context.getAcceptContentType());

        when(httpServletRequest.getHeader("accept")).thenReturn("text/plain, application/json");
        context.init(servletContext, httpServletRequest, httpServletResponse);
        assertEquals(Result.APPLICATION_JSON, context.getAcceptContentType());
    }

    @Test
    public void testGetAcceptEncoding() {
        String encoding = "compress, gzip";
        when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(encoding, context.getAcceptEncoding());

        encoding = null;
        when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertNull(context.getAcceptEncoding());

        encoding = "gzip;q=1.0, identity; q=0.5, *;q=0";
        when(httpServletRequest.getHeader("accept-encoding")).thenReturn(encoding);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(encoding, context.getAcceptEncoding());
    }

    @Test
    public void testGetAcceptLanguage() {
        String language = "de";
        when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(language, context.getAcceptLanguage());

        language = null;
        when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertNull(context.getAcceptLanguage());

        language = "da, en-gb;q=0.8, en;q=0.7";
        when(httpServletRequest.getHeader("accept-language")).thenReturn(language);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(language, context.getAcceptLanguage());
    }

    @Test
    public void testGetAcceptCharset() {
        String charset = "UTF-8";
        when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(charset, context.getAcceptCharset());

        charset = null;
        when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertNull(context.getAcceptCharset());

        charset = "iso-8859-5, unicode-1-1;q=0.8";
        when(httpServletRequest.getHeader("accept-charset")).thenReturn(charset);
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(charset, context.getAcceptCharset());
    }

    /**
     * This is the default mode.
     *
     * We get a Content-Type: application/json and want to parse the incoming json.
     */
    @Test
    public void testParseBodyJsonWorks() {


        when(httpServletRequest.getContentType()).thenReturn("application/json; charset=utf-8");

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        when(bodyParserEngineManager.getBodyParserEngineForContentType("application/json")).thenReturn(bodyParserEngine);
        when(bodyParserEngine.invoke(context, Dummy.class)).thenReturn(new Dummy());

        Object o = context.parseBody(Dummy.class);

        verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/json");
        assertTrue(o instanceof Dummy);


    }
    @Test
    public void testParseBodyPostWorks() {


        when(httpServletRequest.getContentType()).thenReturn(ContentTypes.APPLICATION_POST_FORM);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        when(bodyParserEngineManager.getBodyParserEngineForContentType(ContentTypes.APPLICATION_POST_FORM)).thenReturn(bodyParserEngine);
        Dummy dummy = new Dummy();
        dummy.name = "post";
        dummy.count = 245L;
        when(bodyParserEngine.invoke(context, Dummy.class)).thenReturn(dummy);

        Dummy o = context.parseBody(Dummy.class);

        verify(bodyParserEngineManager).getBodyParserEngineForContentType(ContentTypes.APPLICATION_POST_FORM);
        assertTrue(o instanceof Dummy);
        assertTrue(o.name.equals(dummy.name));
        assertTrue(o.count.equals(dummy.count));
    }

    /**
     * Test for isJson
     */
    @Test
    public void testIsJsonWorks() {
        when(httpServletRequest.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertTrue(context.isRequestJson());
    }

    /**
     * Test is isXml
     */
    @Test
    public void testIsXmlWorks() {
        when(httpServletRequest.getContentType()).thenReturn(ContentTypes.APPLICATION_XML);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertTrue(context.isRequestXml());
    }

    /**
     * This is the default mode.
     *
     * We get a Content-Type: application/json and want to parse the incoming json.
     */
    @Test
    public void testParseBodyXmlWorks() {


        when(httpServletRequest.getContentType()).thenReturn("application/xml");

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        when(bodyParserEngineManager.getBodyParserEngineForContentType("application/xml")).thenReturn(bodyParserEngine);
        when(bodyParserEngine.invoke(context, Dummy.class)).thenReturn(new Dummy());

        Object o = context.parseBody(Dummy.class);

        verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/xml");
        assertTrue(o instanceof Dummy);


    }

    /**
     * The request does not have the Content-Type set => we get a null response.
     */
    @Test
    public void testParseBodyWithUnkownContentTypeWorks() {

        when(httpServletRequest.getContentType()).thenReturn(null);

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);


        Object o = context.parseBody(Dummy.class);


        assertNull(o);

    }

    /**
     * We get an conetnt type that does not match any registered parsers.
     * This must also return null safely.
     */
    @Test
    public void testParseBodyWithUnknownRequestContentTypeWorks() {

        when(httpServletRequest.getContentType()).thenReturn("application/UNKNOWN");

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        Object o = context.parseBody(Dummy.class);

        assertNull(o);

    }


    // Dummy class used for parseBody tests.
    class Dummy {
       public String name;
       public Long count;
    }

    /**
     * Make sure the correct character encoding is set after init.
     */
    @Test
    public void testInitEnforcingOfCorrectEncoding() throws Exception {
        context.init(servletContext, httpServletRequest, httpServletResponse);

        //this proofs that the encoding has been set:
        verify(httpServletRequest).setCharacterEncoding(NinjaConstant.UTF_8);
    }

    /**
     * Make sure the correct character encoding is set before the
     * reader is returned.
     */
    @Test
    public void testGetReaderEnforcingOfCorrectEncoding() throws Exception {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        context.getReader();
      //this proofs that the encoding has been set:
        verify(httpServletRequest).setCharacterEncoding(anyString());


    }

    /**
     * Make sure the correct character encoding is set before the
     * inputStream is returned.
     */
    @Test
    public void testGetInputStreamEnforcingOfCorrectEncoding() throws Exception {

        context.init(servletContext, httpServletRequest, httpServletResponse);

        context.getInputStream();
        //this proofs that the encoding has been set:
        verify(httpServletRequest).setCharacterEncoding(anyString());


    }

    /**
     * We get an conetnt type that does not match any registered parsers.
     * This must also return null safely.
     */
    @Test
    public void testGetServletContext() {

        //init the context from a (mocked) servlet
        context.init(servletContext, httpServletRequest, httpServletResponse);

        Object o = context.getServletContext();

        assertNotNull(o);
        assertEquals(servletContext, o);

    }

    @Test
    public void testGetScheme() {
        final String scheme = "http";
        when(httpServletRequest.getScheme()).thenReturn(scheme);

        context.init(servletContext, httpServletRequest, httpServletResponse);

        assertEquals(scheme, httpServletRequest.getScheme());
    }
}