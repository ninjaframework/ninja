package example;

import static org.junit.Assert.assertTrue;
import ninja.NinjaIntegrationTest;

import org.fluentlenium.core.annotation.Page;
import org.junit.Test;

public class ExampleIntegrationTest extends NinjaIntegrationTest {

	@Test
	public void testThatStaticAssetsWork() {

		goTo("http://localhost:8080/assets/bootstrap.css");

		assertTrue(pageSource().contains("Bootstrap"));

	}

	@Test
	public void testThatInvalidStaticAssetsAreNotFound() {

		goTo("http://localhost:8080/assets/INVALID_FILE");
		
		assertTrue(pageSource().isEmpty());
		


	}

	@Test
	public void testIndexRoute() {

		goTo("http://localhost:8080/");

		assertTrue(pageSource().contains("placeholder"));

	}
	
	@Test
	public void testExamples() {

		goTo("http://localhost:8080/examples");

		assertTrue(pageSource().contains("placeholder"));

	}

}
