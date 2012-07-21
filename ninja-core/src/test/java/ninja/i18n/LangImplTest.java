package ninja.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.Map;

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
		
		when(ninjaProperties.getStringArray("application.langs")).thenReturn(new String [] {"en", "de", "fr-FR"});
		
		LangImpl lang = new LangImpl(ninjaProperties);
		
		//that will refer to messages_en.properties:
		assertEquals("english", lang.get("language", Locale.US));
		assertEquals("english", lang.get("language", Locale.CANADA));
		assertEquals("english", lang.get("language", Locale.UK));	
		
		//that will refer to messages_de.properties:
		assertEquals("deutsch", lang.get("language", Locale.GERMAN));
		assertEquals("deutsch", lang.get("language", Locale.GERMANY));
		
	}
	
	
	@Test
	public void testiParameterized18n() {
		when(ninjaProperties.getStringArray("application.langs")).thenReturn(new String [] {"en", "de", "fr-FR"});
		
		LangImpl lang = new LangImpl(ninjaProperties);
		
		
		//that will refer to messages_en.properties:
		assertEquals("this is the placeholder: test_parameter", lang.get("message_with_placeholder", Locale.US, "test_parameter"));
		assertEquals("this is the placeholder: test_parameter", lang.get("message_with_placeholder", Locale.CANADA, "test_parameter"));
		assertEquals("this is the placeholder: test_parameter", lang.get("message_with_placeholder", Locale.UK, "test_parameter"));	
		
		//that will refer to messages_de.properties:
		assertEquals("das ist der platzhalter: test_parameter", lang.get("message_with_placeholder", Locale.GERMAN, "test_parameter"));
		assertEquals("das ist der platzhalter: test_parameter", lang.get("message_with_placeholder", Locale.GERMANY, "test_parameter"));
		
	}
	
	
	@Test
	public void testi18nGetAll() {
		
		
		when(ninjaProperties.getStringArray("application.langs")).thenReturn(new String [] {"en", "de", "fr-FR"});
		LangImpl lang = new LangImpl(ninjaProperties);
		
		
		//US locale testing:
		Map<Object, Object> map = lang.getAll(Locale.US);

		
		assertEquals(3, map.keySet().size());
		assertTrue(map.containsKey("language"));
		assertTrue(map.containsKey("message_with_placeholder"));
		assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
		
		assertEquals("english", map.get("language"));	
		
		
		
		//GERMAN locale testing:
		map = lang.getAll(Locale.GERMAN);
		assertEquals(3, map.keySet().size());
		assertTrue(map.containsKey("language"));
		assertTrue(map.containsKey("message_with_placeholder"));
		assertTrue(map.containsKey("a_property_only_in_the_defaultLanguage"));
		
		assertEquals("deutsch", map.get("language"));	
		assertEquals("das ist der platzhalter: {0}", map.get("message_with_placeholder"));
		
		
	}

}
