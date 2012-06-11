package ninja;

import java.util.Map;

public interface Router {
	
	public Route getRouteFor(String uri);

	/**
	 * If route contains parameters like so:
	 * /{user}/dashboard
	 * parameterMap must contain user.
	 *
	 * @param uri
	 * @param parameterMap
	 * @return
	 */
	public Route getRouteFor(String uri, Map<String, String> parameterMap);
	
	///////////////////////////////////////////////////////////////////////////
	// convenience methods to use the route in a DSL like way
	// router.GET().route("/index").with(.....)
	///////////////////////////////////////////////////////////////////////////
	public Route GET();
	public Route POST();	
	public Route PUT();
	public Route DELETE();
	
	public Route OPTIONS();
	

}