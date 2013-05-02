#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import ninja.NinjaTest;
import ninja.utils.NinjaTestServer;

import org.apache.http.HttpResponse;
import org.junit.Test;

import com.google.common.collect.Maps;

public class NotFoundTest extends NinjaTest {

	@Test
	public void testThatNotFoundWorks() {

		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();

		// Get raw response
		HttpResponse httpResponse = ninjaTestBrowser.makeRequestAndGetResponse(getServerAddress()
				+ "/_non_existing_url", headers);

		// make sure the status code is correct:
		assertEquals(404, httpResponse.getStatusLine().getStatusCode());
		
		// Now get the content in another request...
		String content = ninjaTestBrowser.makeRequest(getServerAddress()
				+ "/_non_existing_url", headers);
		
		// Check that we get a working "not found" template from views/system/404notFound.ftl.html
		assertTrue(content
				.contains("Oops. Not found."));

	}

}
