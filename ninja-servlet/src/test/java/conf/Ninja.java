package conf;

import ninja.Context;
import ninja.NinjaDefault;


// Just a dummy for testing.
// Allows to check that custom Ninja configuration in user's conf directory
// works properly.
public class Ninja extends NinjaDefault {

    @Override
    public void onRouteRequest(Context.Impl context) {
        super.onRouteRequest(context); //To change body of generated methods, choose Tools | Templates.
    }
    
}
