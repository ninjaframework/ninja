package com.example.conf;

import com.example.controllers.Application;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        router.GET().route("/home").with(Application.class, "home");
        router.GET().route("/request_path").with(Application.class, "request_path");
        router.GET().route("/context_path").with(Application.class, "context_path");
        
    }
    
}