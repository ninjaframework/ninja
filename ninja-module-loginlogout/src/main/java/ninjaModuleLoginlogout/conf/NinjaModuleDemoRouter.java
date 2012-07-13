package ninjaModuleLoginlogout.conf;

import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninjaModuleLoginlogout.controllers.RegistrationController;

public class NinjaModuleDemoRouter implements ApplicationRoutes {

	@Override
	public void init(Router router) {
		// /////////////////////////////////////////////////////////////////////
		// some default functions
		// /////////////////////////////////////////////////////////////////////
		// simply render a page:
		router.GET().route("/registration").with(RegistrationController.class, "registration");
	}

}
