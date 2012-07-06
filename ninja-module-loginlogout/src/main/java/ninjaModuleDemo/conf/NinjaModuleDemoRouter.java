package ninjaModuleDemo.conf;

import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninjaModuleDemo.controllers.ApplicationController;

public class NinjaModuleDemoRouter implements ApplicationRoutes {

	@Override
	public void init(Router router) {
		// /////////////////////////////////////////////////////////////////////
		// some default functions
		// /////////////////////////////////////////////////////////////////////
		// simply render a page:
		router.GET().route("/registration").with(ApplicationController.class, "registration");
	}

}
