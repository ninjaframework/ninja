/**
 * Copyright (C) 2012-2016 the original author or authors.
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

package ninja.utils;

import com.google.common.collect.Maps;

import java.util.HashSet;
import java.util.Map;
import ninja.ContentTypes;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.params.ParamParser;
import ninja.params.ParamParsers;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.validation.Validation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractContextTest {

    @Mock
    private Session sessionCookie;

    @Mock
    private FlashScope flashCookie;

    @Mock
    private BodyParserEngineManager bodyParserEngineManager;

    @Mock
    private Route route;

    @Mock
    private Validation validation;

    @Mock
    private BodyParserEngine bodyParserEngine;
    
    @Mock
    private NinjaProperties ninjaProperties;

    private AbstractContextImpl abstractContext;

    @Before
    public void setUp() {
        abstractContext = new AbstractContextImpl(
                bodyParserEngineManager, 
                flashCookie, 
                ninjaProperties,
                sessionCookie,
                validation,
                new ParamParsers(new HashSet<ParamParser>()));
        
        abstractContext.init("", "/");
    }

    @Test
    public void getRemoteAddr() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn("1.1.1.1").when(context).getRealRemoteAddr();

        assertThat(context.getRemoteAddr(), is("1.1.1.1"));
    }
    
    @Test
    public void getRemoteAddrIgnoresXForwardHeader() {
        AbstractContextImpl context = spy(abstractContext);
        
        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.FALSE);
        doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        doReturn("2.2.2.2").when(context).getHeader(Context.X_FORWARD_HEADER);

        assertThat(context.getRemoteAddr(), is("1.1.1.1"));
    }
    
    @Test
    public void getRemoteAddrUsesXForwardHeader() {
        AbstractContextImpl context = spy(abstractContext);
        
        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        doReturn("2.2.2.2").when(context).getHeader(Context.X_FORWARD_HEADER);

        assertThat(context.getRemoteAddr(), is("2.2.2.2"));
    }
    
    @Test
    public void getRemoteAddrParsesXForwardedForIfMoreThanOneHostPresent() {
        AbstractContextImpl context = spy(abstractContext);

        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        doReturn("192.168.1.1, 192.168.1.2, 192.168.1.3").when(context).getHeader(Context.X_FORWARD_HEADER);

        //make sure this is correct
        assertThat(context.getRemoteAddr(), is("192.168.1.1"));
    }
    
    @Test
    public void getRemoteAddrUsesFallbackIfXForwardedForIsNotValidInetAddr() {
        AbstractContextImpl context = spy(abstractContext);

        when(ninjaProperties.getBooleanWithDefault(Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false)).thenReturn(Boolean.TRUE);
        doReturn("1.1.1.1").when(context).getRealRemoteAddr();
        doReturn("I_AM_NOT_A_VALID_ADDRESS").when(context).getHeader(Context.X_FORWARD_HEADER);

        assertThat(context.getRemoteAddr(), is("1.1.1.1"));
    }

    @Test
    public void addCookieViaResult() {
        AbstractContextImpl context = spy(abstractContext);
        
        Cookie cookie0 = Cookie.builder("cookie0", "yum0").setDomain("domain").build();
        Cookie cookie1 = Cookie.builder("cookie1", "yum1").setDomain("domain").build();
        
        // adding a cookie in the result will eventually trigger addCookie()...
        Result result = Results.html();
        result.addCookie(cookie0);
        result.addCookie(cookie1);

        doNothing().when(context).addCookie(cookie0);
        doNothing().when(context).addCookie(cookie1);
        
        // finalize the headers => the cookies must be copied over to the servletcookies
        context.finalizeHeaders(result);

        verify(context, times(1)).addCookie(cookie0);
        verify(context, times(1)).addCookie(cookie1);
    }

    @Test
    public void unsetCookieAddsCookieWithMaxAgeZero() {
        AbstractContextImpl context = spy(abstractContext);
        
        Cookie cookie = Cookie.builder("cookie", "yummy").setDomain("domain").build();
        
        ArgumentCaptor<Cookie> argument = ArgumentCaptor.forClass(Cookie.class);
        
        doNothing().when(context).addCookie(argument.capture());
        
        context.unsetCookie(cookie);
        
        assertThat(argument.getValue().getMaxAge(), is(0));
    }

    @Test
    public void getPathParameter() {
        AbstractContextImpl context = spy(abstractContext);

        //mock a parametermap:
        Map<String, String> parameterMap = Maps.newHashMap();
        parameterMap.put("parameter", "parameter");

        //and return the parameter map when any parameter is called...
        when(route.getPathParametersEncoded(Matchers.anyString())).thenReturn(parameterMap);

        context.setRoute(route);

        // this parameter is not there and must return null
        assertEquals(null, context.getPathParameter("parameter_not_set"));

        assertEquals("parameter", context.getPathParameter("parameter"));
    }
    
    @Test
    public void getPathParameterDecodingWorks() {
        AbstractContextImpl context = spy(abstractContext);

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
    public void getPathParameterAsInteger() {
        AbstractContextImpl context = spy(abstractContext);

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
    public void getParameterAsInteger() {
        AbstractContextImpl context = spy(abstractContext);

        //this will not work and return null
        doReturn(null).when(context).getParameter("key_not_there");
        assertEquals(null, context.getParameterAsInteger("key_not_there"));

        //this will return the default value:
        doReturn(null).when(context).getParameter("key_not_there");
        assertEquals(new Integer(100), context.getParameterAsInteger("key_not_there", 100));

        //this will work as the value is there...
        doReturn("1").when(context).getParameter("key");
        assertEquals(new Integer(1), context.getParameterAsInteger("key"));
    }

    @Test
    public void getParameterAs() {
        AbstractContextImpl context = spy(abstractContext);

        doReturn(null).when(context).getParameter("key");
        doReturn("100").when(context).getParameter("key1");
        doReturn("true").when(context).getParameter("key2");
        doReturn("10.1").when(context).getParameter("key3");
        doReturn("x").when(context).getParameter("key4");

        //this will not work and return null
        assertEquals(null, context.getParameterAs("key", Long.class));

        assertEquals(new Integer(100), context.getParameterAs("key1", Integer.class));
        assertEquals(new Long(100), context.getParameterAs("key1", Long.class));
        assertEquals(Boolean.TRUE, context.getParameterAs("key2", Boolean.class));
        assertEquals(new Float(10.1), context.getParameterAs("key3", Float.class));
        assertEquals(new Character('x'), context.getParameterAs("key4", Character.class));
    }

    @Test
    public void finalizeInAbstractContextSavesFlashSessionCookies() {
        AbstractContextImpl context = spy(abstractContext);
        
        Result result = Results.json();
        
        Cookie cookie = Cookie.builder("TEST", "value").build();
        
        result.addCookie(cookie);
        
        doNothing().when(context).addCookie(cookie);
        
        ResponseStreams streams = context.finalizeHeaders(result);
        
        // abstract finalizeHeaders does not return anything
        assertThat(streams, is(nullValue()));

        verify(flashCookie, times(1)).save(context);
        verify(sessionCookie, times(1)).save(context);
        verify(context, times(1)).addCookie(cookie);
    }

    @Test
    public void getAcceptContentType() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn(null).when(context).getHeader("accept");
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        doReturn("").when(context).getHeader("accept");
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        doReturn("totally_unknown").when(context).getHeader("accept");
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        doReturn("application/json").when(context).getHeader("accept");
        assertEquals(Result.APPLICATION_JSON, context.getAcceptContentType());

        doReturn("text/html, application/json").when(context).getHeader("accept");
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        doReturn("application/xhtml, application/json").when(context).getHeader("accept");
        assertEquals(Result.TEXT_HTML, context.getAcceptContentType());

        doReturn("text/plain").when(context).getHeader("accept");
        assertEquals(Result.TEXT_PLAIN, context.getAcceptContentType());

        doReturn("text/plain, application/json").when(context).getHeader("accept");
        assertEquals(Result.APPLICATION_JSON, context.getAcceptContentType());
    }

    @Test
    public void getAcceptEncoding() {
        AbstractContextImpl context = spy(abstractContext);
        
        String encoding = "compress, gzip";
        doReturn(encoding).when(context).getHeader("accept-encoding");
        assertEquals(encoding, context.getAcceptEncoding());

        encoding = null;
        doReturn(encoding).when(context).getHeader("accept-encoding");
        assertNull(context.getAcceptEncoding());

        encoding = "gzip;q=1.0, identity; q=0.5, *;q=0";
        doReturn(encoding).when(context).getHeader("accept-encoding");
        assertEquals(encoding, context.getAcceptEncoding());
    }

    @Test
    public void getAcceptLanguage() {
        AbstractContextImpl context = spy(abstractContext);
        
        String language = "de";
        doReturn(language).when(context).getHeader("accept-language");
        assertEquals(language, context.getAcceptLanguage());

        language = null;
        doReturn(language).when(context).getHeader("accept-language");
        assertNull(context.getAcceptLanguage());

        language = "da, en-gb;q=0.8, en;q=0.7";
        doReturn(language).when(context).getHeader("accept-language");
        assertEquals(language, context.getAcceptLanguage());
    }
    
    @Test
    public void getAcceptCharset() {
        AbstractContextImpl context = spy(abstractContext);
        
        String charset = "UTF-8";
        doReturn(charset).when(context).getHeader("accept-charset");
        assertEquals(charset, context.getAcceptCharset());

        charset = null;
        doReturn(charset).when(context).getHeader("accept-charset");
        assertNull(context.getAcceptCharset());

        charset = "iso-8859-5, unicode-1-1;q=0.8";
        doReturn(charset).when(context).getHeader("accept-charset");
        assertEquals(charset, context.getAcceptCharset());
    }
    
    static public class Dummy { 
        String name;
        Long count;
    }
    
    @Test
    public void testParseBodyJsonWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn("application/json; charset=utf-8").when(context).getRequestContentType();

        when(bodyParserEngineManager.getBodyParserEngineForContentType("application/json")).thenReturn(bodyParserEngine);
        when(bodyParserEngine.invoke(context, Dummy.class)).thenReturn(new Dummy());

        Object o = context.parseBody(Dummy.class);

        verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/json");
        assertTrue(o instanceof Dummy);
    }
    
    @Test
    public void testParseBodyPostWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn(ContentTypes.APPLICATION_POST_FORM).when(context).getRequestContentType();

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

    @Test
    public void testIsJsonWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn(ContentTypes.APPLICATION_JSON).when(context).getRequestContentType();

        assertTrue(context.isRequestJson());
    }

    @Test
    public void testIsXmlWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn(ContentTypes.APPLICATION_XML).when(context).getRequestContentType();

        assertTrue(context.isRequestXml());
    }

    @Test
    public void testParseBodyXmlWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn(ContentTypes.APPLICATION_XML).when(context).getRequestContentType();

        when(bodyParserEngineManager.getBodyParserEngineForContentType("application/xml")).thenReturn(bodyParserEngine);
        when(bodyParserEngine.invoke(context, Dummy.class)).thenReturn(new Dummy());

        Object o = context.parseBody(Dummy.class);

        verify(bodyParserEngineManager).getBodyParserEngineForContentType("application/xml");
        assertTrue(o instanceof Dummy);
    }

    @Test
    public void testParseBodyWithUnkownContentTypeWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn(null).when(context).getRequestContentType();

        Object o = context.parseBody(Dummy.class);

        assertNull(o);
    }

    @Test
    public void testParseBodyWithUnknownRequestContentTypeWorks() {
        AbstractContextImpl context = spy(abstractContext);
        
        doReturn("application/UNKNOWN").when(context).getRequestContentType();

        Object o = context.parseBody(Dummy.class);

        assertNull(o);
    }
}
