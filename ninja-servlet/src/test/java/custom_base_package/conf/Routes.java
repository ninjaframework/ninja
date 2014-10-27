package custom_base_package.conf;

import ninja.Router;
import ninja.application.ApplicationRoutes;
import controller.DummyControllerForTesting;


// Just a dummy for testing.
// Allows to check that custom Ninja configuration in user's conf directory
// works properly.
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
       
        router.GET().route("/custom_base_package").with(DummyControllerForTesting.class, "dummyMethod");
        
    }

}
