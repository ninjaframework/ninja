package conf.ninjamoduleloginlogout;

import ninja.AssetsController;
import ninja.Router;
import ninja.application.ApplicationRoutes;
import controllers.ninjamoduleloginlogout.RegistrationController;

public class Routes implements ApplicationRoutes {
	

    @Override
	public void init(Router router) {

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
        
	}

}
