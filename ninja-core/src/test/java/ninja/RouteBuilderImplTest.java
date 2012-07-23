package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

@RunWith(MockitoJUnitRunner.class)
public class RouteBuilderImplTest {

    @Mock
    Injector injector;

	@Test
	public void testBasicGETRoute() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/index");

		assertTrue(buildRoute(routeBuilder).matches("GET", "/index"));

	}
	
	@Test
	public void testBasicPOSTRoute() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.POST().route("/index");

		assertTrue(buildRoute(routeBuilder).matches("POST", "/index"));

	}
	
	@Test
	public void testBasicPUTRoute() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.PUT().route("/index");

		assertTrue(buildRoute(routeBuilder).matches("PUT", "/index"));

	}
	
	@Test
	public void testBasicRoutes() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.OPTION().route("/index");

		assertTrue(buildRoute(routeBuilder).matches("OPTION", "/index"));

	}

	@Test
	public void testBasicRoutesWithRegex() {

		RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
		routeBuilder.GET().route("/.*");

        Route route = buildRoute(routeBuilder);
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

        Route route = buildRoute(routeBuilder);
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
        route = buildRoute(routeBuilder);

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
        Route route = buildRoute(routeBuilder);
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

    @Test
    public void testParametersDontCrossSlashes() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/{id2}/{id3}/morestuff/at/the/end");
        Route route = buildRoute(routeBuilder);
        //this must match
        assertTrue(route.matches("GET", "/blah/id/id2/id3/morestuff/at/the/end"));
        //this should not match as the last "end" is missing
        assertFalse(route.matches("GET", "/blah/id/id2/id3/morestuff/at/the"));
    }
    
    @Test
    public void testPointsInRegexDontCrashRegexInTheMiddleOfTheRoute() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/myname");
        Route route = buildRoute(routeBuilder);
        
        //the "." in the route should not make any trouble:
        String routeFromServer = "/blah/my.id/myname";
        
        assertTrue(route.matches("GET", routeFromServer));
        assertEquals(1, route.getParameters(routeFromServer).entrySet().size());
        assertEquals("my.id", route.getParameters(routeFromServer).get("id")); 
        
        //and another slightly different route
        routeFromServer = "/blah/my.id/myname/should_not_match";
        assertFalse(route.matches("GET", routeFromServer));
        assertEquals(0, route.getParameters(routeFromServer).entrySet().size());
    }
    
    @Test
    public void testPointsInRegexDontCrashRegexAtEnd() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}");
        Route route = buildRoute(routeBuilder);
        //the "." in the route should not make any trouble:
        //even if it's the last part of the route
        String routeFromServer = "/blah/my.id";
        assertTrue(route.matches("GET", "/blah/my.id"));
        assertEquals(1, route.getParameters(routeFromServer).entrySet().size());
        assertEquals("my.id", route.getParameters(routeFromServer).get("id"));   
    }
    
    
    @Test
    public void testRegexInRouteWorksWithoutSlashAtTheEnd() {
        RouteBuilderImpl routeBuilder = new RouteBuilderImpl();
        routeBuilder.GET().route("/blah/{id}/.*");
        Route route = buildRoute(routeBuilder);
        
        //the "." in the real route should work without any problems:
        String routeFromServer = "/blah/my.id/and/some/more/stuff";
        
        assertTrue(route.matches("GET", routeFromServer));
        assertEquals(1, route.getParameters(routeFromServer).entrySet().size());
        assertEquals("my.id", route.getParameters(routeFromServer).get("id"));        
        
        //another slightly different route.
        routeFromServer = "/blah/my.id/";
        assertTrue(route.matches("GET", "/blah/my.id/"));
        assertEquals(1, route.getParameters(routeFromServer).entrySet().size());
        assertEquals("my.id", route.getParameters(routeFromServer).get("id")); 
        
        assertFalse(route.matches("GET", "/blah/my.id"));
        
        
    }
    

    private Route buildRoute(RouteBuilderImpl builder) {
        builder.with(MockController.class, "execute");
        return builder.buildRoute(injector);
    }

    public static class MockController {
        public Result execute() {
            return null;
        }
    }

}
