package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;

import org.apache.http.HttpResponse;
import org.junit.Test;

import com.google.common.collect.Maps;

public class ApplicationControllerTest extends NinjaApiTest {

	@Test
	public void testThatRedirectWorks() {

		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();

		// /redirect will send a location: redirect in the headers
		String result = NinjaApiTestHelper.makeRequest(getServerAddress()
				+ "/redirect", headers);

		// If the redirect has worked we must see the following text
		// from the index screen:
		assertTrue(result
				.contains("And developing large web applications becomes fun again."));

	}

	@Test
	public void testHtmlEscapingInTeamplateWorks() {

		//IF the escaping works I expect the following string inside the page:
		String expectedContent = "&lt;script&gt;alert('Hello');&lt;/script&gt;";
		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();

		// /redirect will send a location: redirect in the headers

		String result = NinjaApiTestHelper.makeRequest(getServerAddress()
				+ "htmlEscaping", headers);

		// If the redirect has worked we must see the following text
		// from the index screen:
		assertTrue(result.contains(expectedContent));

	}

	@Test
	public void makeSureSessionsGetSentToClient() {

		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();

		// /redirect will send a location: redirect in the headers
		HttpResponse httpResponse = NinjaApiTestHelper
				.makeRequestAndGetResponse(getServerAddress() + "session",
						headers);

		String setCookie = httpResponse.getHeaders("Set-Cookie")[0].getValue();

		assertTrue(setCookie.contains("NINJA_SESSION"));
		assertTrue(setCookie.contains("___TS"));
		assertTrue(setCookie.contains("username"));
		assertTrue(setCookie.contains("kevin"));

	}
	
	
	@Test
	public void testThatPathParamParsingWorks() {
		
		// Simply connect to the userDashboard place
		// and make sure that parsing of paramters works as expected.
		
		
		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();

		//do the request
		String response = NinjaApiTestHelper
				.makeRequest(getServerAddress() + "user/12345/john@example.com/userDashboard",
						headers);

		// And assert that stuff is visible on page:
		assertTrue(response.contains("john@example.com"));
		assertTrue(response.contains("12345"));


	}

    @Test
    public void testThatValidationWorks() {
        // Some empty headers for now...
        Map<String, String> headers = Maps.newHashMap();

        String response = NinjaApiTestHelper
                .makeRequest(getServerAddress() + "validation?email=john@example.com");

        // And assert that stuff is visible on page:
        assertEquals(response, "john@example.com");

        response = NinjaApiTestHelper
                .makeRequest(getServerAddress() + "validation");

        // And assert that stuff is visible on page:
        assertEquals(response, "email is required");

    }

}
