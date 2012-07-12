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
