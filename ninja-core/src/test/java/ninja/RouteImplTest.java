package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class RouteImplTest {

	@Test
	public void testBasicGETRoute() {

		Route route1 = new RouteImpl(null);
		route1.GET().route("/index");

		assertTrue(route1.matches("GET", "/index"));

	}
	
	@Test
	public void testBasicPOSTRoute() {

		Route route1 = new RouteImpl(null);
		route1.POST().route("/index");

		assertTrue(route1.matches("POST", "/index"));

	}
	
	@Test
	public void testBasicPUTRoute() {

		Route route1 = new RouteImpl(null);
		route1.PUT().route("/index");

		assertTrue(route1.matches("PUT", "/index"));

	}
	
	@Test
	public void testBasicRoutes() {

		Route route1 = new RouteImpl(null);
		route1.OPTION().route("/index");

		assertTrue(route1.matches("OPTION", "/index"));

	}

	@Test
	public void testBasicRoutesWithRegex() {

		Route route1 = new RouteImpl(null);
		route1.GET().route("/.*");

		// make sure the route catches everything:
		assertTrue(route1.matches("GET", "/index"));
		assertTrue(route1.matches("GET","/stylesheet.css"));
		assertTrue(route1.matches("GET","/public/stylesheet.css"));
		assertTrue(route1.matches("GET","/public/bootstrap.js"));

	}

	@Test
	public void testBasicPlaceholersAndParameters() {

		// /////////////////////////////////////////////////////////////////////
		// One parameter:
		// /////////////////////////////////////////////////////////////////////
		Route route1 = new RouteImpl(null);
		route1.GET().route("/{name}/dashboard");

		assertFalse(route1.matches("GET","/dashboard"));

		assertTrue(route1.matches("GET","/John/dashboard"));

		Map<String, String> map = route1.getParameters("/John/dashboard");

		assertEquals(1, map.entrySet().size());
		assertEquals("John", map.get("name"));

		// /////////////////////////////////////////////////////////////////////
		// More parameters
		// /////////////////////////////////////////////////////////////////////
		route1 = new RouteImpl(null);
		route1.GET().route("/{name}/{id}/dashboard");

		assertFalse(route1.matches("GET","/dashboard"));

		assertTrue(route1.matches("GET","/John/20/dashboard"));

		map = route1.getParameters("/John/20/dashboard");

		assertEquals(2, map.entrySet().size());
		assertEquals("John", map.get("name"));
		assertEquals("20", map.get("id"));

	}

	@Test
	public void testBasicPlaceholersParametersAndRegex() {

		// test that parameter parsing works in conjunction with
		// regex expressions...
		Route route1 = new RouteImpl(null);
		route1.GET().route("/John/{id}/.*");
		assertTrue(route1.matches("GET","/John/20/dashboard"));
		Map<String, String> map = route1.getParameters("/John/20/dashboard");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

		assertTrue(route1.matches("GET","/John/20/admin"));
		map = route1.getParameters("/John/20/admin");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

		assertTrue(route1.matches("GET","/John/20/mock"));
		map = route1.getParameters("/John/20/mock");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

	}

}
