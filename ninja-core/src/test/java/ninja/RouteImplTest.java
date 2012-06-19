package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class RouteImplTest {

	@Test
	public void testBasicRoutes() {

		Route route1 = new RouteImpl(null);
		route1.route("/index");

		assertTrue(route1.matches("/index"));

	}

	@Test
	public void testBasicRoutesWithRegex() {

		Route route1 = new RouteImpl(null);
		route1.route("/.*");

		// make sure the route catches everything:
		assertTrue(route1.matches("/index"));
		assertTrue(route1.matches("/stylesheet.css"));
		assertTrue(route1.matches("/public/stylesheet.css"));
		assertTrue(route1.matches("/public/bootstrap.js"));

	}

	@Test
	public void testBasicPlaceholersAndParameters() {

		// /////////////////////////////////////////////////////////////////////
		// One parameter:
		// /////////////////////////////////////////////////////////////////////
		Route route1 = new RouteImpl(null);
		route1.route("/{name}/dashboard");

		assertFalse(route1.matches("/dashboard"));

		assertTrue(route1.matches("/John/dashboard"));

		Map<String, String> map = route1.getParameters("/John/dashboard");

		assertEquals(1, map.entrySet().size());
		assertEquals("John", map.get("name"));

		// /////////////////////////////////////////////////////////////////////
		// More parameters
		// /////////////////////////////////////////////////////////////////////
		route1 = new RouteImpl(null);
		route1.route("/{name}/{id}/dashboard");

		assertFalse(route1.matches("/dashboard"));

		assertTrue(route1.matches("/John/20/dashboard"));

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
		route1.route("/John/{id}/.*");
		assertTrue(route1.matches("/John/20/dashboard"));
		Map<String, String> map = route1.getParameters("/John/20/dashboard");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

		assertTrue(route1.matches("/John/20/admin"));
		map = route1.getParameters("/John/20/admin");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

		assertTrue(route1.matches("/John/20/mock"));
		map = route1.getParameters("/John/20/mock");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

	}

}
