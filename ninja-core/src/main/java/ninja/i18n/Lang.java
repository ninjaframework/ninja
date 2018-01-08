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

import java.util.Locale;
import java.util.Optional;

import ninja.Context;
import ninja.Result;

import com.google.inject.ImplementedBy;

@ImplementedBy(LangImpl.class)
public interface Lang {

    /**
     * Retrieve the current language or null if not set.
     * It will try to determine the language by:
     * 1) Checking if result contains a forced language
     * 2) Checking if context has a NINJA_LANG cookie with a forced language
     * 3) Getting the first language from the Accept-Language header
     * 
     * @return The current language (fr, ja, it ...) - may be absent
     */
    Optional<String> getLanguage(Context context, Optional<Result> result);
    

    /**
     * Force a language in Ninja framwork.
     * This is usually done by a cookie NINJA_LANG.
     * 
     * This overrides any Accept-Language languages.
     * 
     * @param locale (fr, ja, it ...)
     */
    Result setLanguage(String locale, Result result);


    /**
     * Clears the current language.
     * This will trigger resolving language from request (Accept lang) 
     * if not manually set.
     * 
     * Note: The language is set by a cookie. To delete a cookie
     * the max-age is set to 0. It can therefore be the case that the lang cookie
     * still exists in the thread. Make sure your module / app handles this properly.
     * 
     * @param Result result clear language commands merged into result.
     * 
     */
    void clearLanguage(Result result);
    
    /**
     * application.conf usually contains the following:
     * application.languages=en,de
     * 
     * This little helper checks if the language is supported.
     * 
     * @param language The language to check (en, en-US etc)
     * @return true if supported directly, false if not
     */
    boolean isLanguageDirectlySupportedByThisApplication(String language);
    
    /**
     * application.conf usually contains the following:
     * application.languages=en,de
     * 
     * This little helper converts a language code
     * like (en, en-US etc) to a Java locale.
     * 
     * It takes null as input and falls back to the default language.
     * 
     * By convention this is the first language of application.languages.
     * 
     * In the case of 
     * application.languages=en,de
     * 
     * The default language is "en".
     * 
     * 
     * @param language The language to check (en, en-US etc)
     * @return The Java locale or a default locale based on the first language
     *          in your application.languages configuration.
     */
    Locale getLocaleFromStringOrDefault(Optional<String> language);

}
