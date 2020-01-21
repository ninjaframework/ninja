/**
 * Copyright (C) 2012-2020 the original author or authors.
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

package ninja.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MimeTypesTest {

	@Mock
	private NinjaProperties ninjaProperties;

	@Before
	public void setup() {

		// mock some mime types
		Properties properties = new Properties();
		properties.put("mimetype.custom", "application/custom");

		when(ninjaProperties.getAllCurrentNinjaProperties()).thenReturn(
				properties);
		when(ninjaProperties.get("mimetype.custom")).thenReturn(
				"application/custom");
	}

	@Test
	public void testLoadingWorks() {

		MimeTypes mimeTypes = new MimeTypes(ninjaProperties);

		// some random tests that come from the built in mime types:
		assertEquals("application/vnd.ms-cab-compressed",
				mimeTypes.getMimeType("superfilename.cab"));

		assertEquals("application/vndms-pkiseccat",
				mimeTypes.getMimeType("superfilename.cat"));

		assertEquals("text/x-c", mimeTypes.getMimeType("superfilename.cc"));

		assertEquals("application/clariscad",
				mimeTypes.getMimeType("superfilename.ccad"));

		assertEquals("application/custom",
				mimeTypes.getMimeType("superfilename.custom"));

	}

}
