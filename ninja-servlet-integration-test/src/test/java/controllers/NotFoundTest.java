/**
 * Copyright (C) the original author or authors.
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
import ninja.NinjaDocTester;
import ninja.utils.Message;
import ninja.utils.NinjaConstant;

import org.doctester.testbrowser.HttpConstants;
import org.doctester.testbrowser.Request;
import org.doctester.testbrowser.Response;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class NotFoundTest extends NinjaDocTester {

	@Test
	public void testThatNotFoundWorksHtml() {
        
        Response response 
                = makeRequest(Request.GET().url(testServerUrl().path("/_non_existing_url")));

		assertEquals(404, response.httpStatus);
		
		assertTrue(response.payload
				.contains("The requested route cannot be found."));

	}
    
    @Test
	public void testThatNotFoundWorksJson() {
        
        Response response 
                = makeRequest(
                        Request
                                .GET()
                                .url(testServerUrl().path("/_non_existing_url"))
                                .addHeader(HttpConstants.HEADER_ACCEPT, HttpConstants.APPLICATION_JSON));

		assertEquals(404, response.httpStatus);
        
		Message message = response.payloadJsonAs(Message.class);
        Assert.assertThat(message.text, CoreMatchers.equalTo(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT));

	}
    
    @Test
	public void testThatNotFoundWorksXml() {
        
        Response response 
                = makeRequest(
                        Request
                                .GET()
                                .url(testServerUrl().path("/_non_existing_url"))
                                .addHeader(HttpConstants.HEADER_ACCEPT, HttpConstants.APPLICATION_XML));

		assertEquals(404, response.httpStatus);

		Message message = response.payloadXmlAs(Message.class);
        Assert.assertThat(message.text, CoreMatchers.equalTo(NinjaConstant.I18N_NINJA_SYSTEM_NOT_FOUND_TEXT_DEFAULT));

	}

}
