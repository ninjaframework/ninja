/**
 * Copyright (C) the original author or authors.
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

import java.util.Map;
import ninja.Context;
import com.google.inject.ImplementedBy;

/**
 * Flash Scope consists of two kinds of data: "current" and "outgoing". Current
 * data will only exist for the current request.  Outgoing data will exist for
 * the current and next request.  Neither should be considered secure or encrypted.
 * Its useful for communicating error messages or form submission results.
 * 
 * A FlashScope is i18n aware and the values will be looked up for i18n translations
 * by template engines that support it.
 * 
 * If the Flash Scope has outgoing data then a cookie will be sent to the client
 * and will be valid on the next request. Stuff in a flash cookie gets deleted
 * after the next request.
 * 
 * If an incoming request has a flash cookie then the data from it will be 
 * loaded as "current" flash data.  Unless you keep() those keys that data will
 * only be valid for the current request.
 */
@ImplementedBy(FlashScopeImpl.class)
public interface FlashScope {

    /**
     * Intended for use by implementations only. Initializes the FlashScope from
     * the context.  Ninja will call this when a new request is being handled.
     * 
     * @param context The Ninja context
     */
    void init(Context context);

    /**
     * Intended for use by implementations only. Saves the FlashScope to the
     * context.  Will write/delete cookies, etc. Ninja will call this when a
     * request will be completed.
     * 
     * @param context The Ninja context
     */
    void save(Context context);

    /**
     * Gets a value if its in either the "current" or "outgoing" flash data.
     * 
     * @param key The flash key
     * @return The flash value or null if none exists by that key
     */
    String get(String key);

    /**
     * Removes a value completely from both "current" and "outgoing" flash data.
     * @param key The flash key
     * @return True if removed or false if it didn't exist
     */
    boolean remove(String key);

    /**
     * Checks if the key exists in the "current" flash data.
     * @param key The flash key
     * @return True if the key exists or false if it doesn't
     */
    boolean contains(String key);
    
    /**
     * Puts the key and value into only the "current" flash data.  Will NOT
     * be written as a cookie and will only exist for the current request.
     * Accessible via ${flash.key} in your html templating engine.
     *
     * @param key The flash key
     * @param value The i18n key used to retrieve value of that message
     *      OR an already translated message (if your template engine supports it)
     * @see #put(java.lang.String, java.lang.String) If you need the value for
     *      both the current and next request
     */
    void now(String key, String value);
    
    /**
     * Puts the key and value into both "current" and "outgoing" flash data.
     * Will be written as a cookie and available in the current and next request.
     * If you only need the value in your current request its a good idea to
     * use the <code>now()</code> method instead so you can eliminate the possibility
     * of showing unexpected flash messages on the next request :-).
     * 
     * @param key The flash key
     * @param value The i18n key used to retrieve value of that message
     *      OR an already translated message (if your template engine supports it)
     * @see #now(java.lang.String, java.lang.String) If you only need the value
     *      in your current request.
     */
    void put(String key, String value);

    /**
     * @deprecated Convert your value to a String in your application since
     *      this method implies Serialization could be used (which is not true).
     */
    @Deprecated
    void put(String key, Object value);

    /**
     * Discards the key from the "outgoing" flash data but retains it in the
     * "current" flash data.
     * 
     * @param key The flash key
     * @see #keep(java.lang.String) To reverse this operation and keep the key
     *      as part of the "outgoing" flash data.
     */
    void discard(String key);

    /**
     * Discards all "outgoing" flash data but retains all "current" flash data.
     * 
     * @see #keep() To reverse this operation and keep all keys as part of the
     *      "outgoing" flash data.
     */
    void discard();

    /**
     * Will copy the "current" flash data specified by the key into the "outgoing"
     * flash data.
     * 
     * @param key The flash key
     */
    void keep(String key);

    /**
     * Copies all "current" flash data into the "outgoing" flash data.
     */
    void keep();

    /**
     * Same as calling <code>flash.put("error", "your value");</code>.  The value
     * will be added to both "current" and "outgoing" flash data.
     * 
     * @param value The i18n key used to retrieve value of that message
     *      OR an already translated message (if your template engine supports it)
     */
    void error(String value);

    /**
     * Same as calling <code>flash.put("success", "your value");</code>.  The value
     * will be added to both "current" and "outgoing" flash data.
     * 
     * @param value The i18n key used to retrieve value of that message
     *      OR an already translated message (if your template engine supports it)
     */
    void success(String value);

    /**
     * Clears all "current" flash data.  If you need to ensure all "current"
     * and "outgoing" flash data is deleted then call this as well as discard().
     */
    void clearCurrentFlashCookieData();

    /**
     * Gets all "current" flash data.
     * 
     * @return All current flash data
     */
    Map<String,String> getCurrentFlashCookieData();

    /**
     * Gets all "outgoing" flash data.
     * 
     * @return All outgoing flash data
     */
    Map<String,String> getOutgoingFlashCookieData();
}
