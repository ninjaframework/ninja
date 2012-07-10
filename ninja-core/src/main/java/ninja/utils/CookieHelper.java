package ninja.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;

public class CookieHelper {
    private static final Logger log = LoggerFactory.getLogger(CookieHelper.class);

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

    public static String getCookieValue(String name, Cookie[] cookies) {
        Cookie cookie = getCookie(name, cookies);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public static Cookie convertNinjaCookieToServletCookie(ninja.Cookie cookie) {
        Cookie servletCookie = new Cookie(cookie.getName(), cookie.getValue());
        servletCookie.setMaxAge(cookie.getMaxAge());
        if (cookie.getComment() != null) {
            servletCookie.setComment(cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            servletCookie.setDomain(cookie.getDomain());
        }
        if (cookie.isSecure()) {
            servletCookie.setSecure(true);
        }
        if (cookie.getPath() != null) {
            servletCookie.setPath(cookie.getPath());
        }
        if (cookie.isHttpOnly()) {
            HTTP_ONLY_SETTER.setHttpOnly(servletCookie);
        }
        return servletCookie;
    }

    /**
     * HTTP only is only available in Servlet 3 spec.
     */
    private interface HttpOnlySetter {
        void setHttpOnly(Cookie cookie);
    }

    private static class Servlet3HttpOnlySetter implements HttpOnlySetter {
        @Override
        public void setHttpOnly(Cookie cookie) {
            cookie.setHttpOnly(true);
        }
    }

    private static class Servlet2HttpOnlySetter implements HttpOnlySetter {
        @Override
        public void setHttpOnly(Cookie cookie) {
            log.warn("HTTP only set for cookie " + cookie.getName() + " but ignored because container is not a" +
                    " servlet 3 container");
        }
    }

    private static final HttpOnlySetter HTTP_ONLY_SETTER;

    static {
        HttpOnlySetter httpOnlySetter;
        try {
            Cookie.class.getMethod("setHttpOnly", boolean.class);
            httpOnlySetter = new Servlet3HttpOnlySetter();
        } catch (NoSuchMethodException e) {
            httpOnlySetter = new Servlet2HttpOnlySetter();
        }
        HTTP_ONLY_SETTER = httpOnlySetter;
    }

}
