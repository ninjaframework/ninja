package ninja.utils;

import javax.servlet.http.Cookie;

public class CookieHelper {

	public static Cookie getCookie(String name, Cookie[] cookies) {

		Cookie returnCookie = null;

		if (cookies != null) {

			for (Cookie cookie : cookies) {

				if (cookie.getName().equals(name)) {
					returnCookie = cookie;
					break;
				}
			}

		}
		return returnCookie;
	}

}
