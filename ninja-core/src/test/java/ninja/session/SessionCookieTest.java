package ninja.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.Context;
import ninja.session.SessionCookie;
import ninja.utils.Crypto;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SessionCookieTest {

	@Mock
	private Context context;

	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private HttpServletResponse httpServletResponse;

	@Captor
	private ArgumentCaptor<Cookie> cookieCaptor;

	private Crypto crypto = new Crypto("secret");

	private Integer sessionExpiryTime = 10000;

	private Boolean sessionSendOnlyIfChanged = true;

	@Before
	public void setUp() {

		MockitoAnnotations.initMocks(this);

		when(context.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(context.getHttpServletResponse()).thenReturn(httpServletResponse);

	}

	@Test
	public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {

		// something we want to test:
		sessionSendOnlyIfChanged = true;

		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookie(crypto);

		sessionCookie
		        .init(context, sessionExpiryTime, sessionSendOnlyIfChanged);

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
		sessionSendOnlyIfChanged = false;

		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookie(crypto);

		sessionCookie
		        .init(context, sessionExpiryTime, sessionSendOnlyIfChanged);

		// put nothing => intentionally to check if no flash cookie will be
		// saved
		sessionCookie.save(context);

		// There will be a cookie of that session. Empty, but it will be there:
		verify(httpServletResponse).addCookie(Matchers.any(Cookie.class));
	}

	@Test
	public void testSessionCookieSettingWorks() {
		// setup this testmethod
		// empty cookies
		Cookie[] emptyCookies = new Cookie[0];

		// that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(
		        emptyCookies);

		SessionCookie sessionCookie = new SessionCookie(crypto);

		sessionCookie
		        .init(context, sessionExpiryTime, sessionSendOnlyIfChanged);

		sessionCookie.put("hello", "session!");

		// put nothing => intentionally to check if no session cookie will be
		// saved
		sessionCookie.save(context);

		// a cookie will be set
		verify(httpServletResponse).addCookie(cookieCaptor.capture());

		// verify some stuff on the set cookie
		assertEquals("NINJA_SESSION", cookieCaptor.getValue().getName());

		// assert some stuff...
		// Make sure the message is there and the delimiter "-" is okay...
		// Also make sure that the timestamp ___TS is there...
		assertTrue(cookieCaptor.getValue().getValue()
		        .contains("-%00hello%3Asession%21%00%00___TS"));

		// Make sure the cookie does not start with "-" => therefore there is
		// the crypto
		// stuff at the beginning...
		assertFalse(cookieCaptor.getValue().getValue().startsWith("-"));

		// Make sure the cookie ends with %00 correctly:
		assertTrue(cookieCaptor.getValue().getValue().endsWith("%00"));

	}

}
