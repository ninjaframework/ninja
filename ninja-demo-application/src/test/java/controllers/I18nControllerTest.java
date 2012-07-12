package controllers;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;

import org.junit.Test;

import com.google.common.collect.Maps;

public class I18nControllerTest extends NinjaApiTest {

	@Test
	public void testThatI18nWorksEn() {
		
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Accept-Language", "en-US");
		
		
		String result = NinjaApiTestHelper
				.makeRequest(getServerAddress() + "/i18n", headers);

		assertTrue(result.contains(
				"Hello - this is an i18n message in the templating engine"));

	}
	
	@Test
	public void testThatI18nWorksDe() {
		
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Accept-Language", "de-DE");
		
		
		String result = NinjaApiTestHelper
				.makeRequest(getServerAddress() + "/i18n", headers);

		assertTrue(result.contains(
				"Hallo - das ist eine internationalisierte Nachricht in der Templating Eninge"));

	}
}
