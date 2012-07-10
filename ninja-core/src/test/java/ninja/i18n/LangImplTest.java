package ninja.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LangImplTest {

	
	Lang lang = new LangImpl();
	
	@Before
	public void setup() {
		lang = new LangImpl();
	}
	
	
	@Test
	public void testiSimple18n() {
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
		
		//US locale testing:
		Map<String, String> map = lang.getAll(Locale.US);
		assertEquals(2, map.keySet().size());
		assertTrue(map.containsKey("i18n_language"));
		assertTrue(map.containsKey("i18n_message_with_placeholder"));
		
		assertEquals("english", map.get("i18n_language"));	
		//the placeholder does not get replaced in this case and must return null
		//=> this will lead to freemarker failing when the key is used not intentionally
		assertEquals(null, map.get("message_with_placeholder"));
		
		
		
		//GERMAN locale testing:
		map = lang.getAll(Locale.GERMAN);
		assertEquals(2, map.keySet().size());
		assertTrue(map.containsKey("i18n_language"));
		assertTrue(map.containsKey("i18n_message_with_placeholder"));
		
		assertEquals("deutsch", map.get("i18n_language"));	
		//the placeholder does not get replaced in this case and must return null
		//=> this will lead to freemarker failing when the key is used not intentionally
		assertEquals(null, map.get("message_with_placeholder"));
		
		
	}

}
