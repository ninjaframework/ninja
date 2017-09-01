package com.example.conf;

import com.example.controllers.Application;
import ninja.Router;
import ninja.application.ApplicationRoutes;

public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        router.GET().route("/").with(Application::home);
        router.GET().route("/home").with(Application::home);
        router.GET().route("/request_path").with(Application::request_path);
        router.GET().route("/context_path").with(Application::context_path);
        
    }
    
}