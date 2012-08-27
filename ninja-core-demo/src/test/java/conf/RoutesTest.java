package conf;

import org.junit.Test;

import controllers.ApplicationController;
import ninja.NinjaRouterTest;

public class RoutesTest extends NinjaRouterTest {
    
    @Test
    public void testTestRoutesAreHiddenFromProduction() {
        
        startServerInProdMode();
        
        //test that test route is not available in production mode.
        aRequestLike("GET",  "/_test/testPage").isNotHandledByRoutesInRouter();

        
    }
    
    @Test
    public void testRoutes() {
        
        startServer();
        
        //some tests that routes are there:
        aRequestLike("GET",  "/").isHandledBy(ApplicationController.class, "index");
        aRequestLike("GET",  "/examples").isHandledBy(ApplicationController.class, "examples");

    }

}
