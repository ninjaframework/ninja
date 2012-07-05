package conf;

import com.google.inject.Inject;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;
import controllers.ApplicationController;
import controllers.FilterController;
import controllers.InjectionExampleController;
import controllers.PersonController;

public class Routes implements ApplicationRoutes {
    
    private NinjaProperties ninjaProperties;

    @Inject
    public Routes(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
  
    }

	/**
	 * Using a (almost) nice DSL we can configure the router.
	 * 
	 * The second argument NinjaModuleDemoRouter contains
	 * all routes of a submodule. By simply injecting it we activate the routes.
	 * 
	 * @param router The default router of this application
	 * @param rideOn This is a router of a submodule.
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
