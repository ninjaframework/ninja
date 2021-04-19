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

package ninja.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Optional;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LangImplTest {

    @Mock
    private NinjaProperties ninjaProperties;
    
    @Mock
    private Context context;
    
    @Captor
    private ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
    
    @Before
    public final void before() {
         when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String[] {"en"});
    }
    

    @Test
    public void testGetLanguage() {
        
        Cookie cookie = Cookie.builder("NINJA_TEST" + NinjaConstant.LANG_COOKIE_SUFFIX, "de").build();

        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA_TEST");
        when(context.getCookie("NINJA_TEST" + NinjaConstant.LANG_COOKIE_SUFFIX)).thenReturn(cookie);

        Lang lang = new LangImpl(ninjaProperties);
       
        
        // 1) with context and result => but result does not have a default lang
        Result result = Results.ok();
        Optional<String> language = lang.getLanguage(context, Optional.of(result));
        assertEquals("de", language.get());
        
        
        // 2) with context and result => result has already new lang set...
        result = Results.ok();
        cookie = Cookie.builder("NINJA_TEST" + NinjaConstant.LANG_COOKIE_SUFFIX, "en").build();
        result.addCookie(cookie);
        
        language = lang.getLanguage(context, Optional.of(result));
        assertEquals("en", language.get());
        
        

    }
    
    @Test
    public void testChangeLanguage() {
        
        Cookie cookie = Cookie.builder("NINJA_TEST" + NinjaConstant.LANG_COOKIE_SUFFIX, "de").build();
        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA_TEST");
       
        Lang lang = new LangImpl(ninjaProperties);
        
        // test with result
        Result result = Results.noContent();
        
        result = lang.setLanguage("to", result);
        assertEquals("to", result.getCookie(cookie.getName()).getValue());
        assertEquals(Result.SC_204_NO_CONTENT, result.getStatusCode());


    }
    
    @Test
    public void testClearLanguage() {
        
        Cookie cookie = Cookie.builder("NINJA_TEST" + NinjaConstant.LANG_COOKIE_SUFFIX, "de").build();

        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA_TEST");

        Lang lang = new LangImpl(ninjaProperties);
        
        Result result = Results.ok();
        
        lang.clearLanguage(result);
        
        Cookie returnCookie = result.getCookie(cookie.getName());
        assertEquals("", returnCookie.getValue());
        assertEquals(0, returnCookie.getMaxAge());
        
    }
    
    @Test
    public void testIsLanguageDirectlySupportedByThisApplication() {
  
        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix)).thenReturn("NINJA_TEST");
        when(ninjaProperties.get(NinjaConstant.applicationLanguages)).thenReturn("en");
        
        Lang lang = new LangImpl(ninjaProperties);
        
        assertTrue(lang.isLanguageDirectlySupportedByThisApplication("en"));
        assertFalse(lang.isLanguageDirectlySupportedByThisApplication("de"));
        
        when(ninjaProperties.get(NinjaConstant.applicationLanguages)).thenReturn("en, de, se");
        assertTrue(lang.isLanguageDirectlySupportedByThisApplication("en"));
        assertTrue(lang.isLanguageDirectlySupportedByThisApplication("de"));
        assertTrue(lang.isLanguageDirectlySupportedByThisApplication("se"));
        assertFalse(lang.isLanguageDirectlySupportedByThisApplication("tk"));
        
        
    }
    
    
    @Test
    public void testGetLocaleFromStringOrDefault() {
        
        // ONE DEFAULT LOCALE
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String[] {"en"});
        Lang lang = new LangImpl(ninjaProperties);
        
        Optional<String> language = Optional.empty();
        Locale locale = lang.getLocaleFromStringOrDefault(language);
        
        assertEquals(Locale.ENGLISH, locale);
        
        // GERMAN LOCALE
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String[] {"de", "en"});
        lang = new LangImpl(ninjaProperties);
        
        language = Optional.empty();
        locale = lang.getLocaleFromStringOrDefault(language);
        
        assertEquals(Locale.GERMAN, locale);
        
        // GERMANY LOCALE
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String[] {"de-DE", "en"});
        lang = new LangImpl(ninjaProperties);
        
        language = Optional.empty();
        locale = lang.getLocaleFromStringOrDefault(language);
        
        assertEquals(Locale.GERMANY, locale);
        
    
    }
    
    
    @Test(expected = IllegalStateException.class)
    public void testGetLocaleFromStringOrDefaultISEWhenNoApplicationLanguageDefined() {
        
        // ONE DEFAULT LOCALE
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String[] {});
        Lang lang = new LangImpl(ninjaProperties);
        
        Optional<String> language = Optional.empty();
        lang.getLocaleFromStringOrDefault(language);
        
        // ISE expected
        
    }
    
}