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

package ninja.i18n;

import java.util.Map;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {

    private static Logger logger = LoggerFactory.getLogger(LangImpl.class);
    
    private String applicationCookiePrefix;
    
    private int TEN_YEARS = 60*60*24*365*10;

    private NinjaProperties ninjaProperties;


    @Inject
    public LangImpl(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
        
        this.applicationCookiePrefix = ninjaProperties
                .getOrDie(NinjaConstant.applicationCookiePrefix);

    }

    /** Only for compatibility => now lives in {@link Messages}. */
    @Deprecated
    @Override
    public String get(String key, String language, Object... params) {

        throw new NotImplementedException();

    }

    /** Only for compatibility => now lives in {@link Messages}. */
    @Deprecated
    @Override
    public Map<Object, Object> getAll(String language) {

        throw new NotImplementedException();

    }

    /** Only for compatibility => now lives in {@link Messages}. */
    @Deprecated
    @Override
    public String getWithDefault(String key,
                                 String defaultMessage,
                                 String language,
                                 Object... params) {
        
        throw new NotImplementedException();

    }

    
    @Override
    public void clearLanguage(Result result) {

        Cookie defaultLangCookie = generateNinjaLanguageCookie();        
        result.unsetCookie(defaultLangCookie.getName());

    }

    
    @Override
    public Result setLanguage(String locale, Result result) {
        
        Cookie defaultLangCookie = generateNinjaLanguageCookie();        
        Cookie cookie = Cookie.builder(defaultLangCookie).setValue(locale).build();    
        result.addCookie(cookie);
        
        return result;

    }
    

    @Override
    public String getLanguage(Context context, Result result) {
        
        Cookie defaultCookie = generateNinjaLanguageCookie();
        
        
        // Step 1: Determine language from result.
        // Result always has priority over context and will overwrite context.
        Cookie cookie = result.getCookie(defaultCookie.getName());
        
        if (cookie != null) {
            
            if (cookie.getValue() != null 
                    && !cookie.getValue().isEmpty()) {
                
                //forced language is:
                return cookie.getValue();
            } 
            
        }
        
        // Step 2 => we did not find the language in the result
        // We try to determine it from the context.
        cookie = context.getCookie(defaultCookie.getName());

        if (cookie != null) {
            
            if (cookie.getValue() != null 
                    && !cookie.getValue().isEmpty()) {
                //forced language is:
                return cookie.getValue();
            }
            
        }  
        
        // Step 3: Determine language from Accept-Language header.
        String acceptLanguage = context.getAcceptLanguage(); 
        if (acceptLanguage == null) {
            return null;
        }
        

        // Check if we get a registered mapping for the language input string.
        // At that point the language may be either language-country or only country.
        // extract multiple languages from Accept-Language header
        Iterable<String> languages = Splitter.on(",").trimResults().split(acceptLanguage);
        
        for (String language: languages){
            // Ignore the relative quality factor in Accept-Language header
            if (language.contains(";")){
                language = language.split(";")[0];
                
                return language;
                
            } else {
                
                return language;
                
            }
       
        }

        
        return null;
        
    }
    
    
    /**
     * Retrieves the language cookie or generates one with a very long max age (ten years).
     * 
     * @param context
     * @return The cookie
     */
    private Cookie generateNinjaLanguageCookie() {
        
            
        Cookie cookie = Cookie.builder(applicationCookiePrefix 
                    + ninja.utils.NinjaConstant.LANG_COOKIE_SUFFIX, "")
                    .setMaxAge(TEN_YEARS).build();
            
        
        
        return cookie;
        
    }

    @Override
    public boolean isLanguageDirectlySupportedByThisApplication(String language) {
        
        String applicationLanguages = ninjaProperties.get(NinjaConstant.applicationLanguages);
        Iterable<String> languages = Splitter.on(",").trimResults().split(applicationLanguages);
        
        for (String applicationLanguage : languages) {
            if (applicationLanguage.equals(language)) {
                return true;
            }
        }
        
        return false;
    }

}
