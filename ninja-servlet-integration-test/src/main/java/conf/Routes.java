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
import controllers.ChatWebSocket;
import controllers.FilterController;
import controllers.I18nController;
import controllers.InjectionExampleController;
import controllers.PersonController;
import controllers.PrettyTimeController;
import controllers.UdpPingController;
import controllers.UploadController;
import controllers.UploadControllerAuto;
import java.nio.charset.StandardCharsets;
import ninja.Context;
import ninja.ControllerMethods;
import ninja.session.Session;

public class Routes implements ApplicationRoutes {

    private final NinjaProperties ninjaProperties;

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
        // render a page
        router.GET().route("/").with(ApplicationController::index);
        
        router.WS().route("/chat").with(ChatWebSocket::handshake);

        // with static result (not recommended w/ new lambda feature)
        router.GET().route("/route_with_result")
            .with(Results.html().template("/views/routeWithResult.ftl.html"));

        // lambda routing
        router.GET().route("/lambda_anonymous")
            .with(() -> {
                return Results.status(201).renderRaw("Hi!".getBytes(StandardCharsets.UTF_8));
            });

        // controller method using lambda and arguments
        router.GET().route("/lambda_anonymous_args")
            .with((Context context, Session session) -> {
                session.clear();
                String body = "Query: " + context.getParameter("a");
                return Results.html().renderRaw(body.getBytes(StandardCharsets.UTF_8));
            });
        
        // render a page with variable route parts:
        // use of() method to verify it works
        router.GET().route("/user/{id}/{email}/userDashboard").with(ControllerMethods.of(ApplicationController::userDashboard));

        router.GET().route("/validation").with(ApplicationController::validation);

        // retain legacy class+methodName to verify backwards compat
        router.GET().route("/jsonp").with(ApplicationController.class, "testJsonP");

        // redirect back to /
        router.GET().route("/redirect").with(ApplicationController::redirect);

        router.GET().route("/session").with(ApplicationController::session);

        router.GET().route("/flash_success").with(ApplicationController::flashSuccess);
        router.GET().route("/flash_error").with(ApplicationController::flashError);
        router.GET().route("/flash_any").with(ApplicationController::flashAny);

        router.GET().route("/htmlEscaping").with(ApplicationController::htmlEscaping);
        router.GET().route("/test_reverse_routing").with(ApplicationController::testReverseRouting);
        router.GET().route("/test_get_context_path_works").with(ApplicationController::testGetContextPathWorks);
        router.GET().route("/test_that_freemarker_emits_400_when_template_not_found")
            .with(Results.html().template("/views/A_TEMPLATE_THAT_DOES_NOT_EXIST.ftl.html"));
        // /////////////////////////////////////////////////////////////////////
        // Json support
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/api/person.json").with(PersonController::getPersonJson);
        router.POST().route("/api/person.json").with(PersonController::postPersonJson);

        router.GET().route("/api/person.xml").with(PersonController::getPersonXml);
        router.POST().route("/api/person.xml").with(PersonController::postPersonXml);

        router.GET().route("/api/person").with(PersonController::getPersonViaContentNegotiation);
        router.GET().route("/api/person_with_content_negotiation_fallback").with(PersonController::getPersonViaContentNegotiationAndFallback);

        // /////////////////////////////////////////////////////////////////////
        // Form parsing support
        // /////////////////////////////////////////////////////////////////////
        router.POST().route("/form").with(ApplicationController::postForm);

        // /////////////////////////////////////////////////////////////////////
        // Direct object rendering with template test
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/direct_rendering").with(ApplicationController::directObjectTemplateRendering);

        // /////////////////////////////////////////////////////////////////////
        // Cache support test
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/test_caching").with(ApplicationController::testCaching);

        // /////////////////////////////////////////////////////////////////////
        // Lifecycle support
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/udpcount").with(UdpPingController::getCount);

        // /////////////////////////////////////////////////////////////////////
        // Route filtering example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/filter").with(FilterController::filter);
        router.GET().route("/teapot").with(FilterController::teapot);

        // /////////////////////////////////////////////////////////////////////
        // Route filtering example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/injection").with(InjectionExampleController::injection);
        router.GET().route("/serviceInitTime").with(InjectionExampleController::serviceInitTime);

        // /////////////////////////////////////////////////////////////////////
        // Async example:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/async").with(AsyncController::asyncEcho);

        // /////////////////////////////////////////////////////////////////////
        // I18n:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/i18n").with(I18nController::index);
        router.GET().route("/i18n/{language}").with(I18nController::indexWithLanguage);

        // /////////////////////////////////////////////////////////////////////
        // PrettyTime:
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/prettyTime").with(PrettyTimeController::index);
        router.GET().route("/prettyTime/{language}").with(PrettyTimeController::indexWithLanguage);

        // /////////////////////////////////////////////////////////////////////
        // Upload showcase
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/upload").with(UploadController::upload);
        router.POST().route("/uploadFinish").with(UploadController::uploadFinish);
        router.POST().route("/uploadFinishAuto").with(UploadControllerAuto::uploadFinishAuto);
        router.POST().route("/uploadWithForm").with(UploadControllerAuto::postFormWithFile);
        
        // /////////////////////////////////////////////////////////////////////
        // Authenticity
        // /////////////////////////////////////////////////////////////////////
        router.GET().route("/token").with(AuthenticityController::token);
        router.GET().route("/form").with(AuthenticityController::form);
        router.GET().route("/authenticate").with(AuthenticityController::authenticate);
        router.GET().route("/notauthenticate").with(AuthenticityController::notauthenticate);
        router.GET().route("/unauthorized").with(AuthenticityController::unauthorized);
        router.POST().route("/authorized").with(AuthenticityController::authorized);
        
        //this is a route that should only be accessible when NOT in production
        // this is tested in RoutesTest
        if (!ninjaProperties.isProd()) {
            router.GET().route("/_test/testPage").with(ApplicationController::testPage);
        }

        router.GET().route("/bad_request").with(ApplicationController::badRequest);

        router.GET().route("/assets/webjars/{fileName: .*}").with(AssetsController::serveWebJars);
        router.GET().route("/assets/{fileName: .*}").with(AssetsController::serveStatic);
        router.GET().route("/robots.txt").with(AssetsController::serveStatic);
    }

}