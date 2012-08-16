package example;

import static org.junit.Assert.assertTrue;
import ninja.NinjaFluentLeniumTest;

import org.junit.Test;

public class ExampleIntegrationTest extends NinjaFluentLeniumTest {

	@Test
	public void testThatStaticAssetsWork() {

		goTo(getServerAddress() + "/assets/css/bootstrap.css");

		assertTrue(pageSource().contains("Bootstrap"));

	}

	@Test
	public void testThatInvalidStaticAssetsAreNotFound() {

		goTo(getServerAddress() + "/assets/css/INVALID_FILE");
		
		assertTrue(pageSource().isEmpty());
		


	}

	@Test
	public void testIndexRoute() {

		goTo(getServerAddress());

		assertTrue(pageSource().contains("Ninja web framework"));

	}
	
	@Test
	public void testExamples() {

		goTo(getServerAddress() + "/examples");

		assertTrue(pageSource().contains("Examples"));

	}

}
