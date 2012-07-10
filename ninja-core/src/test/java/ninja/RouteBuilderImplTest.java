package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class RouteBuilderImplTest {
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testBasicGETRoute() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/index");

		assertTrue(routeBuilder.buildRoute(null).matches("GET", "/index"));

	}
	
	@Test
	public void testBasicPOSTRoute() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.POST().route("/index");

		assertTrue(routeBuilder.buildRoute(null).matches("POST", "/index"));

	}
	
	@Test
	public void testBasicPUTRoute() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.PUT().route("/index");

		assertTrue(routeBuilder.buildRoute(null).matches("PUT", "/index"));

	}
	
	@Test
	public void testBasicRoutes() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.OPTION().route("/index");

		assertTrue(routeBuilder.buildRoute(null).matches("OPTION", "/index"));

	}

	@Test
	public void testBasicRoutesWithRegex() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/.*");

        Route route = routeBuilder.buildRoute(null);
		// make sure the route catches everything:
		assertTrue(route.matches("GET", "/index"));
		assertTrue(route.matches("GET","/stylesheet.css"));
		assertTrue(route.matches("GET","/public/stylesheet.css"));
		assertTrue(route.matches("GET","/public/bootstrap.js"));

	}

	@Test
	public void testBasicPlaceholersAndParameters() {

		// /////////////////////////////////////////////////////////////////////
		// One parameter:
		// /////////////////////////////////////////////////////////////////////
		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/{name}/dashboard");

        Route route = routeBuilder.buildRoute(null);
        assertFalse(route.matches("GET","/dashboard"));

		assertTrue(route.matches("GET","/John/dashboard"));

		Map<String, String> map = route.getParameters("/John/dashboard");

		assertEquals(1, map.entrySet().size());
		assertEquals("John", map.get("name"));

		// /////////////////////////////////////////////////////////////////////
		// More parameters
		// /////////////////////////////////////////////////////////////////////
		routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/{name}/{id}/dashboard");
        route = routeBuilder.buildRoute(null);

        assertFalse(route.matches("GET","/dashboard"));

		assertTrue(route.matches("GET","/John/20/dashboard"));

		map = route.getParameters("/John/20/dashboard");

		assertEquals(2, map.entrySet().size());
		assertEquals("John", map.get("name"));
		assertEquals("20", map.get("id"));

	}

	@Test
	public void testBasicPlaceholersParametersAndRegex() {

		// test that parameter parsing works in conjunction with
		// regex expressions...
		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/John/{id}/.*");
        Route route = routeBuilder.buildRoute(null);
        assertTrue(route.matches("GET","/John/20/dashboard"));
		Map<String, String> map = route.getParameters("/John/20/dashboard");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

		assertTrue(route.matches("GET","/John/20/admin"));
		map = route.getParameters("/John/20/admin");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

		assertTrue(route.matches("GET","/John/20/mock"));
		map = route.getParameters("/John/20/mock");
		assertEquals(1, map.entrySet().size());
		assertEquals("20", map.get("id"));

	}

}
