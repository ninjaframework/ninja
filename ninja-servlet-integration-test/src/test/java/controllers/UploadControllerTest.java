/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import ninja.NinjaTest;

import org.junit.Test;

import com.google.common.collect.Maps;

public class UploadControllerTest extends NinjaTest {
	
	@Test
	public void testHtmlEscapingInTeamplateWorks() {
		
		// Some empty headers for now...
		Map<String, String> headers = Maps.newHashMap();
		
		// /redirect will send a location: redirect in the headers
		
		String result = ninjaTestBrowser
				.makeRequest(getServerAddress() + "upload", headers);

		// If the redirect has worked we must see the following text
		// from the index screen:
		assertTrue(result.contains(
				"Please specify file to upload:"));

	}
	
	@Test
	public void testThatUploadWorks() {
		
		File file = new File("src/test/resources/test_for_upload.txt");
		
		// Let's upload a simple txt file...
		String result = ninjaTestBrowser
				.uploadFile(getServerAddress() + "uploadFinish", "file", file);

		// The upload simply displays back the file we uploaded.
		// Let's see if that has worked...
		assertTrue(result.contains(
				"test_for_upload.txt"));

	}
	
}
