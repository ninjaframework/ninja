/**
 * Copyright (C) 2012-2018 the original author or authors.
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

package ninja.i18n;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;

import ninja.Context;
import ninja.Result;

import com.google.inject.ImplementedBy;

@ImplementedBy(MessagesImpl.class)
public interface Messages {

    /**
     * 
     * Get a translated string. The language is determined by the provided
     * locale or a suitable fallback.
     * 
     * values of keys can use the MessageFormat.
     * 
     * More here:
     * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
     * 
     * But in short you can use something like mymessage=my message with a
     * placeholder {0}
     * 
     * parameter will then be used to fill {0} with the content.
     * 
     * Note: If you don't want to determine the language yourself please use
     * {@link Messages#get(String, Context, Result, Object...)} 
     * 
     * @param key
     *            The key used in your message file (conf/messages.properties)
     * @param language
     *            The language to get. Can be null - then the default language
     *            is returned. It also looks for a fallback. Eg. A request for
     *            "en-US" will fallback to "en" if there is no matching language
     *            file.
     * @param parameter
     *            Parameters to use in formatting the message of the key (in
     *            {@link MessageFormat}).
     * @return The matching and formatted value or absent if not found.
     */
    Optional<String> get(String key, 
               Optional<String> language, 
               Object... parameter);
    
    /**
     * 
     * Get a translated string. The language is determined by the provided
     * locale or a suitable fallback.
     * 
     * values of keys can use the MessageFormat.
     * 
     * More here:
     * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
     * 
     * But in short you can use something like mymessage=my message with a
     * placeholder {0}
     * 
     * @param key
     *            The key used in your message file (conf/messages.properties)
     * @param context The context used to determine the language.
     * @param result The result used to determine the language.
     * @param parameter
     *            Parameters to use in formatting the message of the key (in
     *            {@link MessageFormat}).
     * @return The matching and formatted value or absent if not found.
     */
    Optional<String> get(String key, 
               Context context, 
               Optional<Result> result, 
               Object... parameter);

    /**
     * 
     * Gets a message for a message key. Returns a defaultValue if not found.
     * 
     * Note: If you don't want to determine the language yourself please use
     * {@link Messages#getWithDefault(String, Context, Result, Object...)} 
     * 
     * @param key
     *            The key used in your message file (conf/messages.properties)
     * 
     * @param defaultMessage
     *            A default message that will be used when no matching message
     *            can be retrieved.
     * @param language
     *            The language to get. May be absent - then the default language
     *            is returned. It also looks for a fallback. Eg. A request for
     *            "en-US" will fallback to "en" if there is no matching language
     *            file.
     * @param parameter
     *            Parameters to use in formatting the message of the key (in
     *            {@link MessageFormat}).
     * @return The matching and formatted value (either from messages or the
     *         default one).
     */
    String getWithDefault(String key,
                          String defaultMessage,
                          Optional<String> language,
                          Object... params);
    
    
    /**
     * Gets a message for a message key. Returns a defaultValue if not found.
     * 
     * @param key
     *            The key used in your message file (conf/messages.properties)
     * 
     * @param defaultMessage
     *            A default message that will be used when no matching message
     *            can be retrieved.
     * @param context The context used to determine the language.
     * @param result The result used to determine the language. May be absent
     * @param params Parameters to use in formatting the message of the key (in
     *            {@link MessageFormat}).
     * @return The matching and formatted value (either from messages or the
     *         default one).
     */
    String getWithDefault(String key,
                          String defaultMessage,
                          Context context,
                          Optional<Result> result,
                          Object... params);

    /**
     * @deprecated Might be removed from further releases. Usually is not implemented in a thread
     * safe way and is error prone.
     * 
     * Returns all messages for a language we have. Please note that this method
     * does NOT format any MessageFormat values. You have to do that yourself in
     * the controller logic most likely.
     * 
     * Note: If you don't want to determine the language yourself please use
     * {@link Messages#getAll(Context, Result)} 
     * 
     * @param language
     *            The language to get. May be absent - then the default language
     *            is returned. It also looks for a fallback. Eg. A request for
     *            "en-US" will fallback to "en" if there is no matching language
     *            file.
     *            
     *            language can also be the complete Accept-Language header: en-US,en;q=0.8,de;q=0.6
     * 
     * @return A map with all messages as <String, String>
     */
    @Deprecated
    Map<Object, Object> getAll(Optional<String> language);
    
    
    /**
     * Returns all messages for the default language in that context / result.
     * 
     * Does NOT format any MessageFormat values. You have to do that yourself in
     * the controller logic most likely.
     * 
     * 
     * @param context The context 
     * @param result The result - bay be absent
     * @return A map with all messages as <String, String>
     */
    Map<Object, Object> getAll(Context context, Optional<Result> result);

}
