package conf;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import ninjaModuleLoginlogout.controllers.RegistrationController;
import controllers.ApplicationController;
import controllers.AsyncController;
import controllers.FilterController;
import controllers.I18nController;
import controllers.InjectionExampleController;
import controllers.PersonController;
import controllers.UploadController;

public class Routes implements ApplicationRoutes {
	
	/**
	 * Using a (almost) nice DSL we can configure the router.
	 * 
	 * The second argument NinjaModuleDemoRouter contains
	 * all routes of a submodule. By simply injecting it we activate the routes.
	 * 
	 * @param router The default router of this application
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
        //router.GET().route("/udpcount").with(UdpPingController.class, "getCount");

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

        
        // /////////////////////////////////////////////////////////////////////
        // Routes from a plugin:
        // /////////////////////////////////////////////////////////////////////
        
        //router.GET().route("/pending").with(RegistrationController.class, "pending");
        
        //GET         /login                                     casino.Secure.login
        //POST        /login                                     casino.Secure.authenticate
        //GET         /logout                                    casino.Secure.logout

        //GET         /login/auth_via_token                      casino.SessionTransfer.loginViaToken
        //GET         /logout/auth_via_token                     casino.SessionTransfer.logoutViaToken

        //GET         /registration                              casino.Registration.registration
        router.GET().route("/registration").with(RegistrationController.class, "registration");
        //POST        /registration/finish                       casino.Registration.registrationFinish
        //router.POST().route("/registration/finish").with(RegistrationController.class, "registrationFinish");
        //GET         /registration/pending                      casino.Registration.pending
        router.GET().route("/registration/pending").with(RegistrationController.class, "registrationPending");
        //GET         /registration/confirm/{code}               casino.Registration.confirm
        //router.GET().route("/registration/confirm/{code}").with(RegistrationController.class, "registrationConfirm");

//        GET         /registration/lostpassword                 casino.Registration.lostPassword
//        POST        /registration/lostpassword/finish          casino.Registration.lostPasswordFinish
//        GET         /registration/lostpassword/pending         casino.Registration.lostPasswordEmailSentCheckInbox
//        GET         /registration/lostpassword/confirm/{code}  casino.Registration.lostPasswordNewPassword
//        POST        /registration/lostpassword/confirm/finish  casino.Registration.lostPasswordNewPasswordFinish
        
        router.GET().route("/assets/.*").with(AssetsController.class, "serve");
	}

}
