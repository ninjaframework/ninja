package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ApplicationController;

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
		router.GET().route("/").with(ApplicationController.class, "listComments");
		router.POST().route("/postComment").with(ApplicationController.class, "postComment");
        
        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
	}

}
