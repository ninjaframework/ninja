/**
 * Copyright (C) 2013 the original author or authors.
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

package ninja.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.Context;
import ninja.utils.Crypto;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SessionCookieTest {

	@Mock
	private Context context;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private HttpServletResponse httpServletResponse;

	@Captor
	private ArgumentCaptor<Cookie> cookieCaptor;

	private Crypto crypto;

	@Mock
	NinjaProperties ninjaProperties;

	@Before
	public void setUp() {

		when(context.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(context.getHttpServletResponse()).thenReturn(httpServletResponse);
		

		when(ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds)).thenReturn(10000);
		when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionSendOnlyIfChanged, true)).thenReturn(true);
		when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true)).thenReturn(true);
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionHttpOnly, true)).thenReturn(true);
		
		when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret)).thenReturn("secret");
		
		when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA");
		
		
		crypto = new Crypto(ninjaProperties);

	}

	@Test
	public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {

		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
				ninjaProperties);

		sessionCookie.init(context);

		// put nothing => empty session will not be sent as we send only changed
		// stuff...
		sessionCookie.save(context);

		// no cookie should be set as the flash scope is empty...:
		verify(httpServletResponse, never()).addCookie(
		        Matchers.any(Cookie.class));
	}

	@Test
	public void testSessionCookieSettingWorks() throws Exception {
		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context);

		// a cookie will be set
		verify(httpServletResponse).addCookie(cookieCaptor.capture());

		// verify some stuff on the set cookie
		assertEquals("NINJA_SESSION", cookieCaptor.getValue().getName());

		// assert some stuff...
		// Make sure that sign is valid:
		String cookieString = cookieCaptor.getValue().getValue();

		String cookieFromSign = cookieString.substring(cookieString
		        .indexOf("-") + 1);

		String computedSign = crypto.signHmacSha1(cookieFromSign);

		assertEquals(computedSign,
		        cookieString.substring(0, cookieString.indexOf("-")));

		// Make sure that cookie contains timestamp
		assertTrue(cookieString.contains("___TS"));

	}
	
	@Test
	public void testHttpsOnlyWorks() throws Exception {
		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context);

		// a cookie will be set
		verify(httpServletResponse).addCookie(cookieCaptor.capture());

		// verify some stuff on the set cookie
		assertEquals(true, cookieCaptor.getValue().getSecure());

	}
	
	
	@Test
	public void testNoHttpsOnlyWorks() throws Exception {
		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];
		

		when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true)).thenReturn(false);

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context);

		// a cookie will be set
		verify(httpServletResponse).addCookie(cookieCaptor.capture());

		// verify some stuff on the set cookie
		assertEquals(false, cookieCaptor.getValue().getSecure());

	}

    @Test
    public void testHttpOnlyWorks() throws Exception {
        // setup this testmethod
        // empty cookies
        Cookie[] emptyCookies = new Cookie[0];

        // that will be returned by the httprequest...
        when(context.getHttpServletRequest().getCookies()).thenReturn(
                emptyCookies);

        SessionCookie sessionCookie = new SessionCookieImpl(crypto,
                ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);

        // a cookie will be set
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(true, cookieCaptor.getValue().isHttpOnly());

    }

    @Test
    public void testNoHttpOnlyWorks() throws Exception {
        // setup this testmethod
        // empty cookies
        Cookie[] emptyCookies = new Cookie[0];

        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionHttpOnly, true))
                .thenReturn(false);

        // that will be returned by the httprequest...
        when(context.getHttpServletRequest().getCookies()).thenReturn(
                emptyCookies);

        SessionCookie sessionCookie = new SessionCookieImpl(crypto,
                ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context);

        // a cookie will be set
        verify(httpServletResponse).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(false, cookieCaptor.getValue().isHttpOnly());

    }
	
	

	@Test
	public void testThatCookieSavingAndInitingWorks() {

		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("key1", "value1");
		sessionCookie.put("key2", "value2");
		sessionCookie.put("key3", "value3");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context);

		// a cookie will be set
		verify(httpServletResponse).addCookie(cookieCaptor.capture());

		// now we simulate a new request => the session storage will generate a
		// new cookie:
		Cookie[] newSessionCookies = new Cookie[1];
		newSessionCookies[0] = new Cookie(cookieCaptor.getValue().getName(),
		        cookieCaptor.getValue().getValue());

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        newSessionCookies);

		// init new session from that cookie:
		SessionCookie sessionCookie2 = new SessionCookieImpl(crypto,
		        ninjaProperties);
		
		sessionCookie2.init(context);

		assertEquals("value1", sessionCookie2.get("key1"));
		assertEquals("value2", sessionCookie2.get("key2"));
		assertEquals("value3", sessionCookie2.get("key3"));

	}
	
	@Test
	public void testThatCorrectMethodOfNinjaPropertiesIsUsedSoThatStuffBreaksWhenPropertyIsAbsent() {
		
		//we did not set the cookie prefix
		when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn(null);
		
		//stuff must break => ...
		SessionCookie sessionCookie = new SessionCookieImpl(crypto, ninjaProperties);
		
		verify(ninjaProperties).getOrDie(NinjaConstant.applicationCookiePrefix);
	}

	@Test
	public void testSessionCookieDelete() {
		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);
		sessionCookie.init(context);
		final String key = "mykey";
		final String value = "myvalue";
		sessionCookie.put(key, value);

		// value should have been set:
		assertEquals(value, sessionCookie.get(key));

		// value should be returned when removing:
		assertEquals(value, sessionCookie.remove(key));

		// after removing, value should not be there anymore:
		assertNull(sessionCookie.get(key));
	}

}
