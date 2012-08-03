package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;

import org.apache.http.HttpResponse;
import org.junit.Test;

import com.google.common.collect.Maps;

public class NotFoundTest extends NinjaApiTest {

	@Test
	public void testThatNotFoundWorks() {

		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();

		// Get raw response
		HttpResponse httpResponse = NinjaApiTestHelper.makeRequestAndGetResponse(getServerAddress()
				+ "/_non_existing_url", headers);

		// make sure the status code is correct:
		assertEquals(404, httpResponse.getStatusLine().getStatusCode());
		
		// Now get the content in another request...
		String content = NinjaApiTestHelper.makeRequest(getServerAddress()
				+ "/_non_existing_url", headers);
		
		// Check that we get a working "not found" template from views/system/404notFound.ftl.html
		assertTrue(content
				.contains("Oops. Not found."));

	}

}
