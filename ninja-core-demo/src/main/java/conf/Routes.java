/**
 * Copyright (C) 2012 the original author or authors.
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

package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ApplicationController;
import controllers.AsyncController;
import controllers.FilterController;
import controllers.I18nController;
import controllers.InjectionExampleController;
import controllers.PersonController;
import controllers.UdpPingController;
import controllers.UploadController;

public class Routes implements ApplicationRoutes {

    /**
     * Using a (almost) nice DSL we can configure the router.
     * 
     * The second argument NinjaModuleDemoRouter contains all routes of a
     * submodule. By simply injecting it we activate the routes.
     * 
     * @param router
     *            The default router of this application
     */
    @Override
    public void init(Router router) {

        // /////////////////////////////////////////////////////////////////////
        // some default functions
        // /////////////////////////////////////////////////////////////////////
        // simply render a page:
        router.GET().route("/").with(ApplicationController.class, "index");
        router.GET().route("/examples").with(ApplicationController.class, "examples");

        // render a page with variable route parts:
        router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");

        router.GET().route("/validation").with(ApplicationController.class, "validation");

        // redirect back to /
        router.GET().route("/redirect").with(ApplicationController.class, "redirect");

        router.GET().route("/session").with(ApplicationController.class, "session");

        router.GET().route("/htmlEscaping").with(ApplicationController.class, "htmlEscaping");

        // /////////////////////////////////////////////////////////////////////
        // Json support
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/person").with(PersonController.class, "getPerson");
        router.POST().route("/person").with(PersonController.class, "postPerson");

        // /////////////////////////////////////////////////////////////////////
        // Lifecycle support
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/udpcount").with(UdpPingController.class, "getCount");

        // /////////////////////////////////////////////////////////////////////
        // Route filtering example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/filter").with(FilterController.class, "filter");
        router.GET().route("/teapot").with(FilterController.class, "teapot");

        // /////////////////////////////////////////////////////////////////////
        // Route filtering example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/injection").with(InjectionExampleController.class, "injection");

        // /////////////////////////////////////////////////////////////////////
        // Async example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/async").with(AsyncController.class, "asyncEcho");

        // /////////////////////////////////////////////////////////////////////
        // I18n:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/i18n").with(I18nController.class, "index");

        // /////////////////////////////////////////////////////////////////////
        // Upload showcase
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/upload").with(UploadController.class, "upload");
        router.POST().route("/uploadFinish").with(UploadController.class, "uploadFinish");

        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
    }

}
