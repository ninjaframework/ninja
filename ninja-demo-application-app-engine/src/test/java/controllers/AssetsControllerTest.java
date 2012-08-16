package controllers;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import ninja.NinjaTest;
import ninja.utils.MimeTypes;
import ninja.utils.NinjaTestBrowser;

import org.apache.http.HttpResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class AssetsControllerTest extends NinjaTest {
	
	@Test
	public void testThatSettingOfMimeTypeWorks() {
		
		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();
		
		// /redirect will send a location: redirect in the headers
		HttpResponse httpResponse = ninjaTestBrowser
				.makeRequestAndGetResponse(getServerAddress() + "assets/files/test_for_mimetypes.dxf", headers);

		//this is a mimetype nobody knows of...
		//but it is listetd in the ninja mimetypes... therefore it will be found:
		//default charset is always utf-8 by convention.
		assertEquals("application/dxf; charset=utf-8", httpResponse.getHeaders("Content-Type")[0].getValue());

	}
	
}
