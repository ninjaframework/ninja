package ninja;

public interface Router {

    /**
     * Get the route for the given method and URI
     *
     * @param httpMethod The method
     * @param uri The URI
     * @return The route
     */
	public Route getRouteFor(String httpMethod, String uri);

    /**
     * Compile all the routes that have been registered with the router.  This should be called
     * once, during initialisation, before the application starts serving requests.
     */
    public void compileRoutes();

	// /////////////////////////////////////////////////////////////////////////
	// convenience methods to use the route in a DSL like way
	// router.GET().route("/index").with(.....)
	// /////////////////////////////////////////////////////////////////////////
	public RouteBuilder GET();

	public RouteBuilder POST();

	public RouteBuilder PUT();

	public RouteBuilder DELETE();

	public RouteBuilder OPTIONS();

}