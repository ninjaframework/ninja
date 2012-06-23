package example;

import static org.junit.Assert.assertTrue;
import ninja.NinjaIntegrationFluentLeniumTest;

import org.junit.Test;

public class ExampleIntegrationTest extends NinjaIntegrationFluentLeniumTest {

	@Test
	public void testThatStaticAssetsWork() {

		goTo("http://localhost:8080/assets/css/bootstrap.css");

		assertTrue(pageSource().contains("Bootstrap"));

	}

	@Test
	public void testThatInvalidStaticAssetsAreNotFound() {

		goTo("http://localhost:8080/assets/css/INVALID_FILE");
		
		assertTrue(pageSource().isEmpty());
		


	}

	@Test
	public void testIndexRoute() {

		goTo("http://localhost:8080/");

		assertTrue(pageSource().contains("Ninja web framework"));

	}
	
	@Test
	public void testExamples() {

		goTo("http://localhost:8080/examples");

		assertTrue(pageSource().contains("Examples"));

	}

}
