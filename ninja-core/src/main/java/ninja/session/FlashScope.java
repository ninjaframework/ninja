/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import java.text.MessageFormat;
import java.util.Map;

import ninja.Context;
import ninja.Result;

import com.google.inject.ImplementedBy;

/**
 * Flash scope: A client side cookie that can be used to transfer information
 * from one request to another.
 * 
 * Stuff in a flash cookie gets deleted after the next request.
 * 
 * Please note also that flash cookies are not signed.
 */
@ImplementedBy(FlashScopeImpl.class)
public interface FlashScope {

    void init(Context context);

    void save(Context context, Result result);

    void put(String key, String value);

    void put(String key, Object value);

    void now(String key, String value);

    /**
     * Sets the error flash cookie value.
     * Usually accessible via ${flash.error} in html templating engine.
     * 
     * @param value The i18n key used to retrieve value of that message
     *        OR an already translated message that will be displayed right away.
     */
    void error(String value);

    /**
     * Sets the success flash cookie value.
     * Usually accessible via ${flash.success} in html templating engine.
     * 
     * @param value The i18n key used to retrieve value of that message
     *        OR an already translated message that will be displayed right away.
     */
    void success(String value);

    void discard(String key);

    void discard();

    void keep(String key);

    void keep();

    String get(String key);

    boolean remove(String key);

    void clearCurrentFlashCookieData();

    boolean contains(String key);

    String toString();

    Map<String, String> getCurrentFlashCookieData();

    Map<String, String> getOutgoingFlashCookieData();
}
