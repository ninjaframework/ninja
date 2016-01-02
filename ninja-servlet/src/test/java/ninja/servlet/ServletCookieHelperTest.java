/*
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import javax.servlet.http.Cookie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Class.class,Cookie.class})
public class ServletCookieHelperTest {

	private Cookie[] cookies;

	private static final String domain = "test";

	private static final String comment = "This is a name cookie";
	
	private static final String COOKIE_JSESSION_NAME="jsessionid";
	
	private static final String COOKIE_JSESSION_VALUE="456789";
	
	private static final String COOKIE_DOMAIN_DEV="dev";
	
	private static final String COOKIE_DOMAIN_DEV_VALUE="dev env";
	
	private static final String COOKIE_EXPIRES="expire";
	
	private static final String COOKIE_EXPIRES_VALUE="0";
	
	private static final int ZERO=0;

	private Cookie servletCookie;

	private ninja.Cookie ninjaCookie;

	@Test
	public void testGetCookieNullCheck() {
		// given
		cookies = null;

		// when
		servletCookie = ServletCookieHelper.getCookie(COOKIE_DOMAIN_DEV, cookies);

		// then
		assertNull("No match should found as the cookies object is null", servletCookie);
	}

	@Test
	public void testGetCookieForNoMatchedCookie() {
		// given
		cookies = new Cookie[ZERO];

		// when
		servletCookie = ServletCookieHelper.getCookie(COOKIE_DOMAIN_DEV, cookies);

		// then
		assertNull("No match should found as the cookies object is empty", servletCookie);
	}

	@Test
	public void testGetCookieForMatchedCookie() {
		// given
		cookies = getCookies();

		// when
		Cookie domainCookie = ServletCookieHelper.getCookie(COOKIE_DOMAIN_DEV, cookies);

		// then
		assertEquals("Cookie name must be equal to domin", COOKIE_DOMAIN_DEV, domainCookie.getName());
		assertEquals("Cookie value must be equal to test", COOKIE_DOMAIN_DEV_VALUE, domainCookie.getValue());
	}

	@Test
	public void testGetCookieValueNullCheck() {
		// given
		cookies = null;

		// when
		String domainCookieValue = ServletCookieHelper.getCookieValue(COOKIE_DOMAIN_DEV, cookies);

		// then
		assertNull("No match should found as the cookies object is null", domainCookieValue);
	}

	@Test
	public void testGetCookieValueForMatchedCookie() {
		// given
		cookies = getCookies();

		// when
		String domainCookieValue = ServletCookieHelper.getCookieValue(COOKIE_DOMAIN_DEV, cookies);

		// then
		assertEquals("Cookie value must be equal to test", COOKIE_DOMAIN_DEV_VALUE, domainCookieValue);
	}

	@Test
	public void testConvertServletCookieToNinjaCookieWithSimpleData() {
		// given
		servletCookie = new Cookie(COOKIE_JSESSION_NAME, COOKIE_JSESSION_VALUE);
		ninjaCookie = ServletCookieHelper.convertServletCookieToNinjaCookie(servletCookie);

		// when
		servletCookie = ServletCookieHelper.convertNinjaCookieToServletCookie(ninjaCookie);

		// then
		assertEquals("Cookie name must match", servletCookie.getName(), ninjaCookie.getName());
		assertEquals("Cookie value must match", servletCookie.getValue(), ninjaCookie.getValue());
	}

	@Test
	public void testConvertNinjaCookieToServletCookieWithAllData() {
		// given
		servletCookie = getNameCookie();
		ninjaCookie = ServletCookieHelper.convertServletCookieToNinjaCookie(servletCookie);

		// when
		servletCookie = ServletCookieHelper.convertNinjaCookieToServletCookie(ninjaCookie);

		// then
		assertEquals("Cookie name must match", domain, servletCookie.getDomain());
		assertEquals("Cookie value must match", comment, servletCookie.getComment());
	}

	@Test
	public void testSetHttpOnly() {
		// given
		servletCookie = new Cookie(COOKIE_DOMAIN_DEV, COOKIE_DOMAIN_DEV_VALUE);
		ServletCookieHelper.setHttpOnly(servletCookie);

		// when
		ninjaCookie = ServletCookieHelper.convertServletCookieToNinjaCookie(servletCookie);

		// then
		assertTrue("Http flag must be on as we set using httponly method", ninjaCookie.isHttpOnly());
	}

	private Cookie getNameCookie() {
		Cookie nameCookie = new Cookie(COOKIE_JSESSION_NAME, COOKIE_JSESSION_VALUE);
		nameCookie.setComment(comment);
		nameCookie.setDomain(domain);
		nameCookie.setSecure(true);
		nameCookie.setHttpOnly(true);
		nameCookie.setPath("/");
		return nameCookie;
	}
	
	private Cookie[] getCookies() {
		Cookie nameCookie = new Cookie(COOKIE_JSESSION_NAME, COOKIE_JSESSION_VALUE);
		Cookie domainCookie = new Cookie(COOKIE_DOMAIN_DEV, COOKIE_DOMAIN_DEV_VALUE);
		Cookie expiresCookie = new Cookie(COOKIE_EXPIRES, COOKIE_EXPIRES_VALUE);
		Cookie[] cookies = { nameCookie, domainCookie, expiresCookie };
		return cookies;
	}
}