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

import com.google.common.base.Optional;

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
        assertEquals("english", lang.get("language", Optional.of("en-US")).get());
        assertEquals("english", lang.get("language", Optional.of("en-CA")).get());
        assertEquals("english", lang.get("language", Optional.of("en-UK")).get());

        // that will refer to messages_de.properties:
        assertEquals("deutsch", lang.get("language", Optional.of("de")).get());
        assertEquals("deutsch", lang.get("language", Optional.of("de-DE")).get());

        // that will refer to messages_fr-FR.properties:
        assertEquals("francaise", lang.get("language", Optional.of("fr-FR")).get());

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
        assertEquals("english", messages.get("language", context, Optional.of(result)).get());
        when(context.getAcceptLanguage()).thenReturn("en-CA");
        assertEquals("english", messages.get("language", context, Optional.of(result)).get());
        when(context.getAcceptLanguage()).thenReturn("en-UK");
        assertEquals("english", messages.get("language", context, Optional.of(result)).get());

        // test that result overwrites context AcceptHeader
        lang.setLanguage("de", result);
        assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());

        // that forced language from context works with empty result       
        result = Results.ok();
        when(context.getCookie(Mockito.anyString())).thenReturn(
                Cookie.builder("name", "fr-FR").build());
        assertEquals("francaise", messages.get("language", context, Optional.of(result)).get());
        //and the result overwrites it again...        
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        assertEquals("deutsch", messages.get("language", context, Optional.of(result)).get());
        

    }
    
    @Test
    public void testI18nAcceptLanguageHttpHeaderWithQualityScores() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Messages lang = new MessagesImpl(ninjaProperties, null);

        // that will refer to messages_fr-FR.properties:
        assertEquals("francaise", lang.get("language", Optional.of("da,fr-FR;q=0.8")).get());
        assertEquals("francaise", lang.get("language", Optional.of("da;q=0.9, fr-FR; q=0.8")).get());
        
        // that will refer to messages_de.properties:
        assertEquals("deutsch", lang.get("language", Optional.of("de,fr-FR;q=0.8")).get());
        assertEquals("deutsch", lang.get("language", Optional.of("de;q=0.9, fr-FR; q=0.8")).get());

    }

    @Test
    public void testiParameterized18n() {
        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });

        Messages lang = new MessagesImpl(ninjaProperties, null);

        // that will refer to messages_en.properties:
        assertEquals("this is the placeholder: test_parameter",
                lang.get("message_with_placeholder", Optional.of("en-US"), "test_parameter").get());
        assertEquals("this is the placeholder: test_parameter",
                lang.get("message_with_placeholder", Optional.of("en-CA"), "test_parameter").get());
        assertEquals("this is the placeholder: test_parameter",
                lang.get("message_with_placeholder", Optional.of("en-UK"), "test_parameter").get());

        // that will refer to messages_de.properties:
        assertEquals("das ist der platzhalter: test_parameter",
                lang.get("message_with_placeholder", Optional.of("de"), "test_parameter").get());
        assertEquals("das ist der platzhalter: test_parameter",
                lang.get("message_with_placeholder", Optional.of("de-DE"), "test_parameter").get());

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
                messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());       
        when(context.getAcceptLanguage()).thenReturn("en-CA");
        assertEquals("this is the placeholder: test_parameter",
                messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        when(context.getAcceptLanguage()).thenReturn("en-UK");
        assertEquals("this is the placeholder: test_parameter",
                messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());

        
        // that will refer to messages_de.properties:
        lang.setLanguage("de", result);
        assertEquals("das ist der platzhalter: test_parameter",
                messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        
        lang.setLanguage("de-DE", result);
        assertEquals("das ist der platzhalter: test_parameter",
                messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());

        
        // that forced language from context works with empty result       
        result = Results.ok();
        when(context.getCookie(Mockito.anyString())).thenReturn(
                Cookie.builder("name", "fr-FR").build());
        assertEquals("cest le placeholder: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
        //and the result overwrites it again...        
        result = Results.ok();
        lang.setLanguage("de-DE", result);
        assertEquals("das ist der platzhalter: test_parameter", messages.get("message_with_placeholder", context, Optional.of(result), "test_parameter").get());
    }
    
    

    @Test
    public void testi18nGetAll() {

        when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages))
                .thenReturn(new String[] { "en", "de", "fr-FR" });
        Messages lang = new MessagesImpl(ninjaProperties, null);
        
        // US locale testing:
        Map<Object, Object> map = lang.getAll(Optional.of("en-US"));

        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("english", map.get("language"));

        // GERMAN locale testing:
        map = lang.getAll(Optional.of("de"));
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
        Map<Object, Object> map = messages.getAll(context, Optional.of(result));

        assertEquals(4, map.keySet().size());
        assertTrue(map.containsKey("language"));
        assertTrue(map.containsKey("message_with_placeholder"));
        assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
        assertTrue(map.containsKey("a_propert_with_commas"));

        assertEquals("english", map.get("language"));

        // GERMAN locale testing:
        lang.setLanguage("de", result);
        
        map = messages.getAll(context, Optional.of(result));
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
        map = messages.getAll(context, Optional.of(result));

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
                lang.get("a_propert_with_commas", Optional.of("en-US")).get());

    }

}
