package ninja.session;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.Context;
import ninja.session.FlashCookie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FlashCookieTest {
	
	@Mock
	private Context context;
	
	@Mock
	private HttpServletRequest httpServletRequest;

	@Mock
	private HttpServletResponse httpServletResponse;
	
	@Captor
	private ArgumentCaptor<Cookie> cookieCaptor;
	
	@Before
	public void setUp() {
		
		MockitoAnnotations.initMocks(this);
		
		when(context.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(context.getHttpServletResponse()).thenReturn(httpServletResponse);

		
	}
	
	@Test
	public void testFlashScopeDoesNothingWhenFlashCookieEmpty() {
		
		//setup this testmethod
		//empty cookies
		Cookie [] emptyCookies = new Cookie [0];
		
		//that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(emptyCookies);
		
		FlashCookie flashCookie = new FlashCookie();
		
		flashCookie.init(context);
		
		//put nothing => intentionally to check if no flash cookie will be saved		
		flashCookie.save(context);
		
		//no cookie should be set as the flash scope is empty...:
		verify(httpServletResponse, never()).addCookie(Matchers.any(Cookie.class));
	}
	
	
	@Test
	public void testFlashCookieSettingWorks() {
		//setup this testmethod
		//empty cookies
		Cookie [] emptyCookies = new Cookie [0];
		
		//that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(emptyCookies);
		
		FlashCookie flashCookie = new FlashCookie();
		
		flashCookie.init(context);
		
		flashCookie.put("hello", "flashScope");
		
		//put nothing => intentionally to check if no flash cookie will be saved		
		flashCookie.save(context);
		
		//a cookie will be set => hello:flashScope
		verify(httpServletResponse).addCookie(cookieCaptor.capture());
		
		//verify some stuff on the set cookie
		assertEquals("NINJA_FLASH" , cookieCaptor.getValue().getName());
		assertEquals("%00hello%3AflashScope%00" , cookieCaptor.getValue().getValue());
		
	}
	
	
	@Test
	public void testThatFlashCookieWorksAndIsActiveOnlyOneTime() {
		//setup this testmethod
		//empty cookies
		Cookie [] oneCookie = new Cookie [1];
		
		Cookie cookie = new Cookie("NINJA_FLASH", "%00hello%3AflashScope%00");
		oneCookie[0] = cookie;
		
		//that will be returned by the httprequest...
		when(context.getHttpServletRequest().getCookies()).thenReturn(oneCookie);
		
		FlashCookie flashCookie = new FlashCookie();
		
		flashCookie.init(context);
		
		//make sure the old cookue gets parsed:
		assertEquals("flashScope", flashCookie.get("hello"));
		
		flashCookie.put("another message", "is there...");
		flashCookie.put("yet another message", "is there...");
		
		//put nothing => intentionally to check if no flash cookie will be saved		
		flashCookie.save(context);
		
		//a cookie will be set => hello:flashScope
		verify(httpServletResponse).addCookie(cookieCaptor.capture());
		
		//verify some stuff on the set cookie
		assertEquals("NINJA_FLASH" , cookieCaptor.getValue().getName());
		//the new flash messages must be there..
		//but the old has disappeared (flashScope):
		assertEquals("%00another+message%3Ais+there...%00%00yet+another+message%3Ais+there...%00" , cookieCaptor.getValue().getValue());
		
	}

}
