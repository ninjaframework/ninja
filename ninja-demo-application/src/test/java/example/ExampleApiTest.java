package example;

import static org.junit.Assert.assertTrue;
import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;

import org.junit.Test;


public class ExampleApiTest extends NinjaApiTest {


	@Test
	public void testThatStaticAssetsWork() {

		String apiCallResult = NinjaApiTestHelper.makeJsonRequest(getServerAddress() + "/person");
		
		assertTrue(apiCallResult.startsWith("{\"name\":\"zeeess name -"));
		
	}

	

}
