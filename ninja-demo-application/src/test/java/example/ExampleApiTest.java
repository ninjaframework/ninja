package example;

import static org.junit.Assert.assertEquals;
import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;

import org.junit.Test;

public class ExampleApiTest extends NinjaApiTest {

	@Test
	public void testThatStaticAssetsWork() {

		String apiCallResult = NinjaApiTestHelper.makeJsonRequest("http://localhost:8080/person");
		
		assertEquals("{\"name\":\"zeeess name\"}", apiCallResult);

	}

	

}
