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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessagesImplTest {

    @Mock
    private NinjaProperties ninjaProperties;
    
    @Mock
    Context context;
    
    Result result;

    @Test
    public void testiSimple18n() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Messages lang = new MessagesImpl(ninjaProperties, null);

        // that will refer to messages_en.properties:
        assertEquals("english", lang.get("language", "en-US"));
        assertEquals("english", lang.get("language", "en-CA"));
        assertEquals("english", lang.get("language", "en-UK"));

        // that will refer to messages_de.properties:
        assertEquals("deutsch", lang.get("language", "de"));
        assertEquals("deutsch", lang.get("language", "de-DE"));

        // that will refer to messages_fr-FR.properties:
        assertEquals("francaise", lang.get("language", "fr-FR"));

    }
    
    @Test
    public void testiSimple18nWithContextResult() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        
        
        result = Results.ok();

        // test with context Accept Header
        when(context.getAcceptLanguage()).thenReturn("en-US");
        assertEquals("english", messages.get("language", context, result));
        when(context.getAcceptLanguage()).thenReturn("en-CA");
        assertEquals("english", messages.get("language", context, result));
        when(context.getAcceptLanguage()).thenReturn("en-UK");
        assertEquals("english", messages.get("language", context, result));

        // test that result overwrites context AcceptHeader
        lang.setLanguage("de", result);
        assertEquals("deutsch", messages.get("language", context, result));
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        assertEquals("deutsch", messages.get("language", context, result));

        // that forced language from context works with empty result       
        result = Results.ok();
        when(context.getCookie(Mockito.anyString())).thenReturn(
                Cookie.builder("name", "fr-FR").build());
        assertEquals("francaise", messages.get("language", context, result));
        //and the result overwrites it again...        
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        assertEquals("deutsch", messages.get("language", context, result));
        

    }
    
    @Test
    public void testI18nAcceptLanguageHttpHeaderWithQualityScores() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Messages lang = new MessagesImpl(ninjaProperties, null);

        // that will refer to messages_fr-FR.properties:
        assertEquals("francaise", lang.get("language", "da,fr-FR;q=0.8"));
        assertEquals("francaise", lang.get("language", "da;q=0.9, fr-FR; q=0.8"));
        
        // that will refer to messages_de.properties:
        assertEquals("deutsch", lang.get("language", "de,fr-FR;q=0.8"));
        assertEquals("deutsch", lang.get("language", "de;q=0.9, fr-FR; q=0.8"));

    }

    @Test
    public void testiParameterized18n() {
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Messages lang = new MessagesImpl(ninjaProperties, null);

        // that will refer to messages_en.properties:
        assertEquals("this is the placeholder: test_parameter",
                lang.get("message_with_placeholder", "en-US", "test_parameter"));
        assertEquals("this is the placeholder: test_parameter",
                lang.get("message_with_placeholder", "en-CA", "test_parameter"));
        assertEquals("this is the placeholder: test_parameter",
                lang.get("message_with_placeholder", "en-UK", "test_parameter"));

        // that will refer to messages_de.properties:
        assertEquals("das ist der platzhalter: test_parameter",
                lang.get("message_with_placeholder", "de", "test_parameter"));
        assertEquals("das ist der platzhalter: test_parameter",
                lang.get("message_with_placeholder", "de-DE", "test_parameter"));

    }
    
    
    @Test
    public void testiParameterized18nWithContextAndResult() {
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        
        result = Results.ok();

        // test with context Accept Header
        when(context.getAcceptLanguage()).thenReturn("en-US");

        // that will refer to messages_en.properties:
        assertEquals("this is the placeholder: test_parameter",
                messages.get("message_with_placeholder", context, result, "test_parameter"));       
        when(context.getAcceptLanguage()).thenReturn("en-CA");
        assertEquals("this is the placeholder: test_parameter",
                messages.get("message_with_placeholder", context, result, "test_parameter"));
        when(context.getAcceptLanguage()).thenReturn("en-UK");
        assertEquals("this is the placeholder: test_parameter",
                messages.get("message_with_placeholder", context, result, "test_parameter"));

        
        // that will refer to messages_de.properties:
        lang.setLanguage("de", result);
        assertEquals("das ist der platzhalter: test_parameter",
                messages.get("message_with_placeholder", context, result, "test_parameter"));
        
        lang.setLanguage("de-DE", result);
        assertEquals("das ist der platzhalter: test_parameter",
                messages.get("message_with_placeholder", context, result, "test_parameter"));

        
        // that forced language from context works with empty result       
        result = Results.ok();
        when(context.getCookie(Mockito.anyString())).thenReturn(
                Cookie.builder("name", "fr-FR").build());
        assertEquals("cest le placeholder: test_parameter", messages.get("message_with_placeholder", context, result, "test_parameter"));
        //and the result overwrites it again...        
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        assertEquals("das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", context, result, "test_parameter"));
    }
    
    

    @Test
    public void testi18nGetAll() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });
        Messages lang = new MessagesImpl(ninjaProperties, null);
        
        // US locale testing:
        Map<Object, Object> map = lang.getAll("en-US");

        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("english", map.get("language"));

        // GERMAN locale testing:
        map = lang.getAll("de");
        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("deutsch", map.get("language"));
        assertEquals("das ist der platzhalter: {0}",
                map.get("message_with_placeholder"));

    }
    
    
    @Test
    public void testi18nGetAllWithContextAndResult() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });
        
        Lang lang = new LangImpl(ninjaProperties);
        Messages messages = new MessagesImpl(ninjaProperties, lang);
        
        result = Results.ok();
        when(context.getAcceptLanguage()).thenReturn("en-US");
        
        // US locale testing:
        Map<Object, Object> map = messages.getAll(context, result);

        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("english", map.get("language"));

        // GERMAN locale testing:
        lang.setLanguage("de", result);
        
        map = messages.getAll(context, result);
        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("deutsch", map.get("language"));
        assertEquals("das ist der platzhalter: {0}",
                map.get("message_with_placeholder"));
        
        
        // reset result and set context cookie:        
        result = Results.ok();
        when(context.getCookie(Mockito.anyString())).thenReturn(
                Cookie.builder("name", "en").build());
        map = messages.getAll(context, result);

        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("english", map.get("language"));

    }
    
    
    

    @Test
    public void testAgainstCorrectParsingOfDelimitersInPropertiesFiles() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });
        Messages lang = new MessagesImpl(ninjaProperties, null);

        assertEquals("prop1, prop2, prop3",
                lang.get("a_propert_with_commas", "en-US"));

    }

}
