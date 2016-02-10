package com.session.conf;

import com.session.controllers.Application;
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        // these combination of routes are being used to verify the overwriting
        // of sessions on redirects -- where the global session filter runs...
        router.GET().route("/getOrCreateSession").with(Application.class, "getOrCreateSession");
        router.GET().route("/getSession").with(Application.class, "getSession");
        router.GET().route("/badRoute").with(Results.redirect("/getSession"));
        
    }
    
}