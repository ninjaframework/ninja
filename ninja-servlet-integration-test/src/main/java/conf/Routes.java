/**
 * Copyright (C) 2012-2015 the original author or authors.
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
import ninja.Results;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninja.utils.NinjaProperties;

import com.google.inject.Inject;

import controllers.ApplicationController;
import controllers.AsyncController;
import controllers.AuthenticityController;
import controllers.FilterController;
import controllers.I18nController;
import controllers.InjectionExampleController;
import controllers.PersonController;
import controllers.PrettyTimeController;
import controllers.UdpPingController;
import controllers.UploadController;
import controllers.UploadControllerAuto;

public class Routes implements ApplicationRoutes {

    private NinjaProperties ninjaProperties;

    @Inject
    public Routes(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;

    }

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

        // with result
        router.GET().route("/route_with_result").with(Results.html().template("/views/routeWithResult.ftl.html"));

        // render a page with variable route parts:
        router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");

        router.GET().route("/validation").with(ApplicationController.class, "validation");

        router.GET().route("/jsonp").with(ApplicationController.class, "testJsonP");

        // redirect back to /
        router.GET().route("/redirect").with(ApplicationController.class, "redirect");

        router.GET().route("/session").with(ApplicationController.class, "session");

        router.GET().route("/flash_success").with(ApplicationController.class, "flashSuccess");
        router.GET().route("/flash_error").with(ApplicationController.class, "flashError");
        router.GET().route("/flash_any").with(ApplicationController.class, "flashAny");

        router.GET().route("/htmlEscaping").with(ApplicationController.class, "htmlEscaping");
        router.GET().route("/test_reverse_routing").with(ApplicationController.class, "testReverseRouting");
        router.GET().route("/test_get_context_path_works").with(ApplicationController.class, "testGetContextPathWorks");
        router.GET().route("/test_that_freemarker_emits_400_when_template_not_found").with(Results.html().template("/views/A_TEMPLATE_THAT_DOES_NOT_EXIST.ftl.html"));
        // /////////////////////////////////////////////////////////////////////
        // Json support
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/api/person.json").with(PersonController.class, "getPersonJson");
        router.POST().route("/api/person.json").with(PersonController.class, "postPersonJson");

        router.GET().route("/api/person.xml").with(PersonController.class, "getPersonXml");
        router.POST().route("/api/person.xml").with(PersonController.class, "postPersonXml");

        router.GET().route("/api/person").with(PersonController.class, "getPersonViaContentNegotiation");
        router.GET().route("/api/person_with_content_negotiation_fallback").with(PersonController.class, "getPersonViaContentNegotiationAndFallback");

        // /////////////////////////////////////////////////////////////////////
        // Form parsing support
        // /////////////////////////////////////////////////////////////////////
        router.POST().route("/form").with(ApplicationController.class, "postForm");

        // /////////////////////////////////////////////////////////////////////
        // Direct object rendering with template test
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/direct_rendering").with(ApplicationController.class, "directObjectTemplateRendering");

        // /////////////////////////////////////////////////////////////////////
        // Cache support test
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/test_caching").with(ApplicationController.class, "testCaching");

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
        router.GET().route("/serviceInitTime").with(InjectionExampleController.class, "serviceInitTime");

        // /////////////////////////////////////////////////////////////////////
        // Async example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/async").with(AsyncController.class, "asyncEcho");

        // /////////////////////////////////////////////////////////////////////
        // I18n:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/i18n").with(I18nController.class, "index");
        router.GET().route("/i18n/{language}").with(I18nController.class, "indexWithLanguage");

        // /////////////////////////////////////////////////////////////////////
        // PrettyTime:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/prettyTime").with(PrettyTimeController.class, "index");
        router.GET().route("/prettyTime/{language}").with(PrettyTimeController.class, "indexWithLanguage");

        // /////////////////////////////////////////////////////////////////////
        // Upload showcase
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/upload").with(UploadController.class, "upload");
        router.POST().route("/uploadFinish").with(UploadController.class, "uploadFinish");
        router.POST().route("/uploadFinishAuto").with(UploadControllerAuto.class, "uploadFinishAuto");
        
        // /////////////////////////////////////////////////////////////////////
        // Authenticity
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/token").with(AuthenticityController.class, "token");
        router.GET().route("/form").with(AuthenticityController.class, "form");
        router.GET().route("/authenticate").with(AuthenticityController.class, "authenticate");
        router.GET().route("/notauthenticate").with(AuthenticityController.class, "notauthenticate");
        router.GET().route("/unauthorized").with(AuthenticityController.class, "unauthorized");
        router.POST().route("/authorized").with(AuthenticityController.class, "authorized");
        
        //this is a route that should only be accessible when NOT in production
        // this is tested in RoutesTest
        if (!ninjaProperties.isProd()) {
            router.GET().route("/_test/testPage").with(ApplicationController.class, "testPage");
        }

        router.GET().route("/bad_request").with(ApplicationController.class, "badRequest");

        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
        router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");

    }

}