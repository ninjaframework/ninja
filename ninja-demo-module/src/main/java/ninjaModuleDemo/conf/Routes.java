package ninjaModuleDemo.conf;

import ninja.Router;

import com.google.inject.Inject;

import controllers.ApplicationController;
import controllers.SecondController;

public class Routes {	
	
	@Inject
	public Routes(Router router) {
		
		//FIXME => is there a better way to route files => so that eclipse can jump
		//right into index method?
		router.route("/cathrin/{id}/test/{name}").    GET().with(ApplicationController.class, "index");
		
		router.route("/cathrin/{nachricht}/{leniort}").          GET().with(ApplicationController.class, "cathrin");		
		router.route("/technique").     GET().with(ApplicationController.class, "technique");
				
		router.route("/redirect").   GET().with(ApplicationController.class,      "redirect");
		
		router.route("/second/json").   GET().with(SecondController.class,      "second");
		
		
	}

}
