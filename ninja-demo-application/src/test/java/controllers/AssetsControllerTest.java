package controllers;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;
import ninja.utils.MimeTypes;

import org.apache.http.HttpResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class AssetsControllerTest extends NinjaApiTest {
	
	@Test
	public void testThatSettingOfMimeTypeWorks() {
		
		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();
		
		// /redirect will send a location: redirect in the headers
		HttpResponse httpResponse = NinjaApiTestHelper
				.makeRequestAndGetResponse(getServerAddress() + "assets/files/test_for_mimetypes.dxf", headers);

		//this is a mimetype nobody knows of...
		//but it is listetd in the ninja mimetypes... therefore it will be found:
		assertEquals("application/dxf", httpResponse.getHeaders("Content-Type")[0].getValue());

	}
	
}
