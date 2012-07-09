package conf;

import controllers.*;
import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {
	
	/**
	 * Using a (almost) nice DSL we can configure the router.
	 * 
	 * The second argument NinjaModuleDemoRouter contains
	 * all routes of a submodule. By simply injecting it we activate the routes.
	 * 
	 * @param router The default router of this application
	 */
    @Override
	public void init(Router router) {
    	
    	// /////////////////////////////////////////////////////////////////////
		// some default functions
		// /////////////////////////////////////////////////////////////////////
		// simply render a page:
		router.GET().route("/").with(ApplicationController.class, "index");
		router.GET().route("/examples").with(ApplicationController.class, "examples");

		// render a page with variable route parts:
		router.GET().route("/user/{id}/{name}/userDashboard").with(ApplicationController.class, "userDashboard");

		// redirect back to /
		router.GET().route("/redirect").with(ApplicationController.class, "redirect");
		
		router.GET().route("/session").with(ApplicationController.class, "session");

		// /////////////////////////////////////////////////////////////////////
		// Json support
		// /////////////////////////////////////////////////////////////////////
		router.GET().route("/person").with(PersonController.class, "getPerson");
		router.POST().route("/person").with(PersonController.class, "postPerson");

        // /////////////////////////////////////////////////////////////////////
        // Lifecycle support
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/udpcount").with(UdpPingController.class, "getCount");

        // /////////////////////////////////////////////////////////////////////
		// Route filtering example:
		// /////////////////////////////////////////////////////////////////////
		router.GET().route("/filter").with(FilterController.class, "filter");
		router.GET().route("/teapot").with(FilterController.class, "teapot");

		// /////////////////////////////////////////////////////////////////////
		// Route filtering example:
		// /////////////////////////////////////////////////////////////////////
		router.GET().route("/injection").with(InjectionExampleController.class, "injection");
		
		
		router.GET().route("/assets/.*").with(AssetsController.class, "serve");
	}

}
