/**
 * Copyright (C) 2012 the original author or authors.
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

import java.util.Locale;
import java.util.Map;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LangImplTest {
	
	@Mock
	private NinjaProperties ninjaProperties;
	
	@Test
	public void testiSimple18n() {
		
		when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String [] {"en", "de", "fr-FR"});
		
		LangImpl lang = new LangImpl(ninjaProperties);
		
		//that will refer to messages_en.properties:
		assertEquals("english", lang.get("language", "en-US"));
		assertEquals("english", lang.get("language", "en-CA"));
		assertEquals("english", lang.get("language", "en-UK"));	
		
		//that will refer to messages_de.properties:
		assertEquals("deutsch", lang.get("language", "de"));
		assertEquals("deutsch", lang.get("language", "de-DE"));
		
	}
	
	
	@Test
	public void testiParameterized18n() {
		when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String [] {"en", "de", "fr-FR"});
		
		LangImpl lang = new LangImpl(ninjaProperties);
		
		
		//that will refer to messages_en.properties:
		assertEquals("this is the placeholder: test_parameter", lang.get("message_with_placeholder", "en-US", "test_parameter"));
		assertEquals("this is the placeholder: test_parameter", lang.get("message_with_placeholder", "en-CA", "test_parameter"));
		assertEquals("this is the placeholder: test_parameter", lang.get("message_with_placeholder", "en-UK", "test_parameter"));	
		
		//that will refer to messages_de.properties:
		assertEquals("das ist der platzhalter: test_parameter", lang.get("message_with_placeholder", "de", "test_parameter"));
		assertEquals("das ist der platzhalter: test_parameter", lang.get("message_with_placeholder", "de-DE", "test_parameter"));
		
	}
	
	
	@Test
	public void testi18nGetAll() {
		
		
		when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String [] {"en", "de", "fr-FR"});
		LangImpl lang = new LangImpl(ninjaProperties);
		
		
		//US locale testing:
		Map<Object, Object> map = lang.getAll("en-US");

		
		assertEquals(4, map.keySet().size());
		assertTrue(map.containsKey("language"));
		assertTrue(map.containsKey("message_with_placeholder"));
		assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
		assertTrue(map.containsKey("a_propert_with_commas"));
		
		assertEquals("english", map.get("language"));	
		
		
		
		//GERMAN locale testing:
		map = lang.getAll("de");
		assertEquals(4, map.keySet().size());
		assertTrue(map.containsKey("language"));
		assertTrue(map.containsKey("message_with_placeholder"));
		assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
		assertTrue(map.containsKey("a_propert_with_commas"));
		
		assertEquals("deutsch", map.get("language"));	
		assertEquals("das ist der platzhalter: {0}", map.get("message_with_placeholder"));
		
		
	}
	
	
	@Test
	public void testAgainstCorrectParsingOfDelimitersInPropertiesFiles() {
		
		when(ninjaProperties.getStringArray(NinjaConstant.applicationLanguages)).thenReturn(new String [] {"en", "de", "fr-FR"});
		LangImpl lang = new LangImpl(ninjaProperties);
		
		assertEquals("prop1, prop2, prop3", lang.get("a_propert_with_commas", "en-US"));
				
	}


}
