/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.session.conf;

import com.example.controllers.ExampleWebSocket;
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
        router.WS().route("/example").with(ExampleWebSocket::handshake);
        
    }
    
}