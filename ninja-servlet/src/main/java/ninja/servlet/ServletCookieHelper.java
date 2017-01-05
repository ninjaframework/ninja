/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import javax.servlet.http.Cookie;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletCookieHelper {
    private static final Logger log = LoggerFactory
            .getLogger(ServletCookieHelper.class);

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
            SERVLET_COOKIE_FALLBACK_HANDLER.setHttpOnly(servletCookie);
        }
        return servletCookie;
    }
    
    public static ninja.Cookie convertServletCookieToNinjaCookie(
                                                                 @NotNull Cookie cookie) {
        ninja.Cookie.Builder ninjaCookieBuilder
            = ninja.Cookie.builder(cookie.getName(), cookie.getValue());
        

        ninjaCookieBuilder.setMaxAge(cookie.getMaxAge());
        
        if (cookie.getComment() != null) {
            ninjaCookieBuilder.setComment(cookie.getComment());
        }
        
        if (cookie.getDomain() != null) {
            ninjaCookieBuilder.setDomain(cookie.getDomain());
        }

        ninjaCookieBuilder.setSecure(cookie.getSecure());
        
        if (cookie.getPath() != null) {
            ninjaCookieBuilder.setPath(cookie.getPath());
        }
        
        
        boolean isHttpOnly = SERVLET_COOKIE_FALLBACK_HANDLER.isHttpOnly(cookie);        
        ninjaCookieBuilder.setHttpOnly(isHttpOnly);
        
        
        return ninjaCookieBuilder.build();
    }

    public static void setHttpOnly(Cookie cookie) {
        SERVLET_COOKIE_FALLBACK_HANDLER.setHttpOnly(cookie);
    }

    /**
     * HTTP only is only available in Servlet 3 spec.
     */
    private interface ServletCookieFallbackHandler {
        void setHttpOnly(Cookie cookie);
        boolean isHttpOnly(Cookie cookie);
    }

    private static class Servlet3CookieFallbackHandler implements ServletCookieFallbackHandler {
        @Override
        public void setHttpOnly(Cookie cookie) {
            cookie.setHttpOnly(true);
        }
        
        @Override
        public boolean isHttpOnly(Cookie cookie) {
            return cookie.isHttpOnly();
        }
    }

    private static class Servlet25CookieFallbackHandler implements ServletCookieFallbackHandler {
        
        private boolean warningAlreadyPrintedOut = false;
        
        @Override
        public void setHttpOnly(Cookie cookie) {
            printWarning();
        }
        
        @Override
        public boolean isHttpOnly(Cookie cookie) {
            printWarning();
            return false;
        }
        
        private void printWarning() {
            
            //don't pollute log.
            if (!warningAlreadyPrintedOut) {
                
                log.warn("Running inside Servlet 2.5 container. " +
                		"Ignoring HttpSecure and HttpOnly for now.");
                
                warningAlreadyPrintedOut = true;
            }
            
        }
    }

    private static final ServletCookieFallbackHandler SERVLET_COOKIE_FALLBACK_HANDLER;

    static {
        ServletCookieFallbackHandler httpOnlySetter;
        try {
            Cookie.class.getMethod("setHttpOnly", boolean.class);
            httpOnlySetter = new Servlet3CookieFallbackHandler();
        } catch (NoSuchMethodException e) {
            httpOnlySetter = new Servlet25CookieFallbackHandler();
        }
        SERVLET_COOKIE_FALLBACK_HANDLER = httpOnlySetter;
    }

}
