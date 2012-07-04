package ninjaModuleDemo.conf;

import ninja.Router;
import ninjaModuleDemo.controllers.ApplicationController;

import com.google.inject.Inject;

public class NinjaModuleDemoRouter {

		public NinjaModuleDemoRouter(Router router) {
			// /////////////////////////////////////////////////////////////////////
			// some default functions
			// /////////////////////////////////////////////////////////////////////
			// simply render a page:
			router.GET().route("/registration").with(ApplicationController.class, "registration");
		}

}
