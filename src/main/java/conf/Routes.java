package conf;

import com.google.inject.Inject;

import ninja.Router;
import controllers.ApplicationController;
import controllers.SecondController;

public class Routes {	
	
	@Inject
	public Routes(Router router) {
		//FIXME => is there a better way to route files => so that eclipse can jump
		//right into index method?
		router.route("/index/{id}/test/{name}").    GET().with(ApplicationController.class, "index");
		router.route("/main").          GET().with(ApplicationController.class, "main");		
		router.route("/technique").     GET().with(ApplicationController.class, "technique");
		
		router.route("/second/json").   GET().with(SecondController.class,      "second");
		
		
	}

}
