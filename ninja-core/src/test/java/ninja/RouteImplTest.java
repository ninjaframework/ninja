package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class RouteImplTest {
	
	@Test
	public void testMatches() {
		///////////////////////////////////////////////////////////////////////
		// Test some basic simple routes
		///////////////////////////////////////////////////////////////////////
		RouteImpl route1 = new RouteImpl(null);
		route1.route("/index");
		
		assertTrue(route1.matches("/index"));
		
		///////////////////////////////////////////////////////////////////////
		// Test routes with placeholders {...}
		///////////////////////////////////////////////////////////////////////
		route1.route("/{name}/dashboard");		
		assertFalse(route1.matches("/dashboard"));
		
		assertTrue(route1.matches("/John/dashboard"));
		Map<String, String> map = route1.getParameters("/John/dashboard");
		assertEquals(1, map.entrySet().size());
		assertEquals("John", map.get("name"));
		
		
	}

}
