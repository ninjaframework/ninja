package conf;

import ninja.Router;

import com.google.inject.Inject;

import controllers.ApplicationController;
import controllers.FilterController;
import controllers.InjectionExampleController;
import controllers.JsonController;

public class Routes {

	@Inject
	public Routes(Router router) {

		///////////////////////////////////////////////////////////////////////
		// some default functions
		///////////////////////////////////////////////////////////////////////
		//simply render a page:
		router.GET().route("/")
				.with(ApplicationController.class, "index");

		//render a page with variable route parts:
		router.GET().route("/user/{id}/{name}/userDashboard")
				.with(ApplicationController.class, "userDashboard");

		//redirect back to /
		router.GET().route("/redirect")
				.with(ApplicationController.class, "redirect");

		
		///////////////////////////////////////////////////////////////////////
		// Json support
		///////////////////////////////////////////////////////////////////////
		router.GET().route("/json")
				.with(JsonController.class, "json");
		
		///////////////////////////////////////////////////////////////////////
		// Route filtering example:
		///////////////////////////////////////////////////////////////////////
		router.GET().route("/filter")
				.with(FilterController.class, "filter");
		
		
		///////////////////////////////////////////////////////////////////////
		// Route filtering example:
		///////////////////////////////////////////////////////////////////////
		router.GET().route("/injection")
				.with(InjectionExampleController.class, "injection");

	}

}
