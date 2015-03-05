package ninja.bodyparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import ninja.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class BodyParserTest {

	@Mock
	Context context;

	@Test
	public void testJsonBodyParser() throws IOException {

		Dto dto = new Dto();
		dto.name = "Peter";
		dto.id = 12345;

		ObjectMapper mapper = new ObjectMapper();
		InputStream stream = new ByteArrayInputStream(mapper.writeValueAsBytes(dto));
		Mockito.when(context.getInputStream()).thenReturn(stream);

		// do
		BodyParserEngineJson bodyParserEnginePost = new BodyParserEngineJson(mapper);
		Dto parsedDto = bodyParserEnginePost.invoke(context, Dto.class);

		// and test:
		assertNotNull(parsedDto);
		assertEquals("Peter", parsedDto.name);
		assertEquals(12345, dto.id);
		dto = (Dto) parsedDto;
	}

	@Test
	public void testXmlBodyParser() throws IOException {

		Dto dto = new Dto();
		dto.name = "Peter";
		dto.id = 12345;

		XmlMapper mapper = new XmlMapper();
		InputStream stream = new ByteArrayInputStream(mapper.writeValueAsBytes(dto));
		Mockito.when(context.getInputStream()).thenReturn(stream);

		// do
		BodyParserEngineXml bodyParserEnginePost = new BodyParserEngineXml(mapper);
		Dto parsedDto = bodyParserEnginePost.invoke(context, Dto.class);

		// and test:
		assertNotNull(parsedDto);
		assertEquals("Peter", parsedDto.name);
		assertEquals(12345, dto.id);
		dto = (Dto) parsedDto;
	}

	public static class Dto {
		private int id;
		private String name;

		public Dto() {
		}

		public int getId() {
			return id;
		}

		public void setId(final int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}
	}
}
