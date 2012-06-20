package example;

import static org.junit.Assert.assertTrue;
import ninja.NinjaIntegrationTest;

import org.junit.Test;

public class ExampleIntegrationTest extends NinjaIntegrationTest {

	@Test
	public void testit7() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());
		
		assertTrue(pageSource().contains("placeholder"));

	}

	@Test
	public void testit6() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());

	}

	@Test
	public void testit5() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());

	}

	@Test
	public void testit4() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());

	}

	@Test
	public void testit3() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());

	}

	@Test
	public void testit2() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());

	}

	@Test
	public void testit1() {
		System.out.println("before...:");

		goTo("http://localhost:8080/");
		System.out.println("sirce: " + pageSource());

	}

}
