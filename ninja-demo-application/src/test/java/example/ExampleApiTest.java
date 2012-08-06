package example;

import static org.junit.Assert.assertTrue;
import ninja.NinjaTest;
import ninja.utils.NinjaTestBrowser;

import org.junit.Test;


public class ExampleApiTest extends NinjaTest {


	@Test
	public void testThatStaticAssetsWork() {

		String apiCallResult = ninjaTestBrowser.makeJsonRequest(getServerAddress() + "/person");
		
		assertTrue(apiCallResult.startsWith("{\"name\":\"zeeess name -"));
		
	}

	

}
