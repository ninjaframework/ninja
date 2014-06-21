package conf;

import controllers.DummyControllerForTesting;
import ninja.NinjaDefault;
import ninja.Router;
import ninja.application.ApplicationRoutes;


// Just a dummy for testing.
// Allows to check that custom Ninja configuration in user's conf directory
// works properly.
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
       
        router.GET().route("/").with(DummyControllerForTesting.class, "dummyMethod");
        
    }

}
