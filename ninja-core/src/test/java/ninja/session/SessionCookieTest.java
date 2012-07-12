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
		

		when(ninjaProperties.getInteger(SessionCookieConfig.sessionExpireTimeInMs)).thenReturn(10000);
		when(ninjaProperties.getBoolean(SessionCookieConfig.sessionSendOnlyIfChanged)).thenReturn(true);
		when(ninjaProperties.getBoolean(SessionCookieConfig.sessionTransferredOverHttpsOnly)).thenReturn(true);
		
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
	public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndSentAlwaysEvenWhenUnchanged() {

		// Something we want to test
		// Session will be sent always. EVEN if it is empty:
		when(ninjaProperties.getBoolean(SessionCookieConfig.sessionSendOnlyIfChanged)).thenReturn(false);

		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookieImpl(crypto,
		        ninjaProperties);

		sessionCookie.init(context);

		// put nothing => intentionally to check if no flash cookie will be
		// saved
		sessionCookie.save(context);

		// There will be a cookie of that session. Empty, but it will be there:
		verify(httpServletResponse).addCookie(Matchers.any(Cookie.class));
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
		

		when(ninjaProperties.getBoolean(SessionCookieConfig.sessionTransferredOverHttpsOnly)).thenReturn(false);

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
		
		verify(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix));
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
