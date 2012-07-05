package conf;

import ninja.session.SessionCookie;
import ninja.utils.ConfigurableModule;
import ninja.utils.NinjaConstant;
import etc.GreetingService;
import etc.GreetingServiceImpl;

public class Configuration extends ConfigurableModule {

	protected void configure() {

		// /////////////////////////////////////////////////////////////////////
		// Some guice bindings
		// /////////////////////////////////////////////////////////////////////
		// some additional bindings for the application:
		bind(GreetingService.class).to(GreetingServiceImpl.class);

		// /////////////////////////////////////////////////////////////////////
		// Setting of ninja variables for startup:
		// /////////////////////////////////////////////////////////////////////
		setProperty(NinjaConstant.applicationSecret, "sdfsdfsdfsfsdfsdfsdfsdfsdfsdfsfsdfsdf");

		// for flash and session cookies
		setProperty(SessionCookie.Config.sessionExpireTimeInMs, 200000);
		setProperty(SessionCookie.Config.sessionSendOnlyIfChanged, true);
		setProperty(SessionCookie.Config.sessionTransferredOverHttpsOnly, false);

	}

}
