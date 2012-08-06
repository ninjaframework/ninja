package controllers;

import static org.junit.Assert.assertTrue;

import java.net.URLEncoder;
import java.util.Map;

import ninja.NinjaTest;
import ninja.utils.NinjaTestBrowser;

import org.junit.Test;

import com.google.common.collect.Maps;

public class I18nControllerTest extends NinjaTest {

	@Test
	public void testThatI18nWorksEn() {
		
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Accept-Language", "en-US");
		
		
		String result = ninjaTestBrowser
				.makeRequest(getServerAddress() + "/i18n", headers);

		assertTrue(result.contains(
				"Hello - this is an i18n message in the templating engine"));

	}
	
	@Test
	public void testThatI18nWorksDe() {
		
		Map<String, String> headers = Maps.newHashMap();
		headers.put("Accept-Language", "de-DE");
		
		
		String result = ninjaTestBrowser
				.makeRequest(getServerAddress() + "/i18n", headers);

		assertTrue(result.contains(
				"Hallo - das ist eine internationalisierte Nachricht in der Templating Eninge"));

	}
	

}
