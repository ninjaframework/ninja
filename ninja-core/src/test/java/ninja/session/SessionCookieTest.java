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
import static org.mockito.Mockito.*;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
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

import java.util.Collections;
import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class SessionCookieTest {

	@Mock
	private Context context;

	@Mock
	private Result result;

	@Captor
	private ArgumentCaptor<Cookie> cookieCaptor;

	private Crypto crypto;

	@Mock
	NinjaProperties ninjaProperties;

	@Before
	public void setUp() {

		when(ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds)).thenReturn(10000);
		when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionSendOnlyIfChanged, true)).thenReturn(true);
		when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true)).thenReturn(true);
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionHttpOnly, true)).thenReturn(true);

		when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret)).thenReturn("secret");

		when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA");


		crypto = new Crypto(ninjaProperties, new Random());

	}

	@Test
	public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
				ninjaProperties);

		sessionCookie.init(context);

        // put nothing => empty session will not be sent as we send only changed
		// stuff...
		sessionCookie.save(context, result);

		// no cookie should be set as the flash scope is empty...:
		verify(result, never()).addCookie(
		        Matchers.any(Cookie.class));
	}

	@Test
	public void testSessionCookieSettingWorks() throws Exception {

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context, result);

		// a cookie will be set
		verify(result).addCookie(cookieCaptor.capture());

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

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context, result);

		// a cookie will be set
		verify(result).addCookie(cookieCaptor.capture());

		// verify some stuff on the set cookie
		assertEquals(true, cookieCaptor.getValue().isSecure());

	}


	@Test
	public void testNoHttpsOnlyWorks() throws Exception {
		// setup this testmethod
		when(ninjaProperties.getBooleanWithDefault(NinjaConstant.sessionTransferredOverHttpsOnly, true)).thenReturn(false);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context, result);

		// a cookie will be set
		verify(result).addCookie(cookieCaptor.capture());

		// verify some stuff on the set cookie
		assertEquals(false, cookieCaptor.getValue().isSecure());

	}

    @Test
    public void testHttpOnlyWorks() throws Exception {

        SessionCookie sessionCookie = new SessionCookieImpl(crypto,
                ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(true, cookieCaptor.getValue().isHttpOnly());

    }

    @Test
    public void testNoHttpOnlyWorks() throws Exception {
        // setup this testmethod
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionHttpOnly, true))
                .thenReturn(false);

        SessionCookie sessionCookie = new SessionCookieImpl(crypto,
                ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(false, cookieCaptor.getValue().isHttpOnly());

    }



	@Test
	public void testThatCookieSavingAndInitingWorks() {

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		sessionCookie.put("key1", "value1");
		sessionCookie.put("key2", "value2");
		sessionCookie.put("key3", "value3");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context, result);

		// a cookie will be set
		verify(result).addCookie(cookieCaptor.capture());

		// now we simulate a new request => the session storage will generate a
		// new cookie:
		Cookie newSessionCookie = Cookie.builder(cookieCaptor.getValue().getName(),
		        cookieCaptor.getValue().getValue()).build();

		// that will be returned by the httprequest...
		when(context.getCookie(cookieCaptor.getValue().getName()))
                .thenReturn(newSessionCookie);

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
