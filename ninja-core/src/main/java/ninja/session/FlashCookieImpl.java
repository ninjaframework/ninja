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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.utils.CookieDataCodec;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;

/**
 * Flash scope: A client side cookie that can be used to transfer information
 * from one request to another.
 * 
 * Stuff in a flash cookie gets deleted after the next request.
 * 
 * Please note also that flash cookies are not signed.
 */
public class FlashCookieImpl implements FlashCookie {

    private Map<String, String> currentFlashCookieData = new HashMap<String, String>();
    private Map<String, String> outgoingFlashCookieData = new HashMap<String, String>();

    private String applicationCookiePrefix;
    
    private static Logger logger = LoggerFactory.getLogger(FlashCookieImpl.class);

    @Inject
    public FlashCookieImpl(NinjaProperties ninjaProperties) {
        this.applicationCookiePrefix = ninjaProperties
                .getOrDie(NinjaConstant.applicationCookiePrefix);
    }

    @Override
    public void init(Context context) {
        // get flash cookie:
        Cookie flashCookie = context.getCookie(applicationCookiePrefix
                + ninja.utils.NinjaConstant.FLASH_SUFFIX);

        if (flashCookie != null) {
            try {
                
                CookieDataCodec.decode(currentFlashCookieData, flashCookie.getValue());

            } catch (UnsupportedEncodingException e) {
                logger.error("Encoding exception - this must not happen", e); 
            }
        }

    }

    @Override
    public void save(Context context, Result result) {

        if (outgoingFlashCookieData.isEmpty()) {

            if (context.hasCookie(applicationCookiePrefix
                    + ninja.utils.NinjaConstant.FLASH_SUFFIX)) {

                Cookie.Builder cookie = Cookie.builder(applicationCookiePrefix
                    + NinjaConstant.FLASH_SUFFIX, "");
                cookie.setPath("/");
                cookie.setSecure(false);
                cookie.setMaxAge(0);

                result.addCookie(cookie.build());

            }

            return;

        }

        else {
            try {

                String flashData = CookieDataCodec.encode(outgoingFlashCookieData);

                Cookie.Builder cookie = Cookie.builder(applicationCookiePrefix
                        + ninja.utils.NinjaConstant.FLASH_SUFFIX, flashData);
                cookie.setPath("/");
                cookie.setSecure(false);
                // "-1" does not set "Expires" for that cookie
                // => Cookie will live as long as the browser is open theoretically
                cookie.setMaxAge(-1);

                result.addCookie(cookie.build());

            } catch (Exception e) {
                logger.error("Encoding exception - this must not happen", e);
            }
        }
    }

    @Override
    public void put(String key, String value) {
        if (key.contains(":")) {
            throw new IllegalArgumentException(
                    "Character ':' is invalid in a flash key.");
        }
        currentFlashCookieData.put(key, value);
        outgoingFlashCookieData.put(key, value);
    }

    @Override
    public void put(String key, Object value) {
        if (value == null) {
            put(key, (String) null);
        }
        put(key, value + "");
    }

    @Override
    public void now(String key, String value) {
        if (key.contains(":")) {
            throw new IllegalArgumentException(
                    "Character ':' is invalid in a flash key.");
        }
        currentFlashCookieData.put(key, value);
    }

    @Override
    public void error(String value) {
        put("error", value);
    }

    @Override
    public void success(String value) {
        put("success", value);
    }

    @Override
    public void discard(String key) {
        outgoingFlashCookieData.remove(key);
    }

    @Override
    public void discard() {
        outgoingFlashCookieData.clear();
    }

    @Override
    public void keep(String key) {
        if (currentFlashCookieData.containsKey(key)) {
            outgoingFlashCookieData.put(key, currentFlashCookieData.get(key));
        }
    }

    @Override
    public void keep() {
        outgoingFlashCookieData.putAll(currentFlashCookieData);
    }

    @Override
    public String get(String key) {
        return currentFlashCookieData.get(key);
    }

    @Override
    public boolean remove(String key) {
        return currentFlashCookieData.remove(key) != null;
    }

    @Override
    public void clearCurrentFlashCookieData() {
        currentFlashCookieData.clear();
    }

    @Override
    public boolean contains(String key) {
        return currentFlashCookieData.containsKey(key);
    }

    @Override
    public Map<String, String> getCurrentFlashCookieData() {
        return currentFlashCookieData;
    }

    @Override
    public Map<String, String> getOutgoingFlashCookieData() {
        return outgoingFlashCookieData;
    }
}
