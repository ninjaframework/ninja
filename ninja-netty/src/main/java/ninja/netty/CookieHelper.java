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

package ninja.netty;

import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.DefaultCookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import javax.validation.constraints.NotNull;

public class CookieHelper {
    private static final Logger log = LoggerFactory.getLogger(CookieHelper.class);

    public static Cookie getCookie(String name, Set<Cookie> cookies) {

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

    public static String getCookieValue(String name, Set<Cookie> cookies) {
        Cookie cookie = getCookie(name, cookies);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public static Cookie convertNinjaCookieToNettyCookie(ninja.Cookie cookie) {
        Cookie nettyCookie = new DefaultCookie(cookie.getName(), cookie.getValue());
        nettyCookie.setMaxAge(cookie.getMaxAge());
        if (cookie.getComment() != null) {
            nettyCookie.setComment(cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            nettyCookie.setDomain(cookie.getDomain());
        }
        if (cookie.isSecure()) {
            nettyCookie.setSecure(true);
        }
        if (cookie.getPath() != null) {
            nettyCookie.setPath(cookie.getPath());
        }
        nettyCookie.setHttpOnly(cookie.isHttpOnly());
        return nettyCookie;
    }

    public static ninja.Cookie convertNettyCookieToNinjaCookie(@NotNull Cookie cookie) {
        ninja.Cookie.Builder ninjaCookieBuilder
            = ninja.Cookie.builder(cookie.getName(), cookie.getValue());

        ninjaCookieBuilder.setMaxAge((int) cookie.getMaxAge());

        if (cookie.getComment() != null) {
            ninjaCookieBuilder.setComment(cookie.getComment());
        }

        if (cookie.getDomain() != null) {
            ninjaCookieBuilder.setDomain(cookie.getDomain());
        }

        ninjaCookieBuilder.setSecure(cookie.isSecure());

        if (cookie.getPath() != null) {
            ninjaCookieBuilder.setPath(cookie.getPath());
        }

        ninjaCookieBuilder.setHttpOnly(cookie.isHttpOnly());


        return ninjaCookieBuilder.build();
    }
}
