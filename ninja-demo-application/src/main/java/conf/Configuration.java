package conf;

import ninja.utils.ConfigurableModule;
import etc.GreetingService;
import etc.GreetingServiceImpl;

public class Configuration extends ConfigurableModule {

	protected void configure() {
		// /////////////////////////////////////////////////////////////////////
		// Default setup:
		// /////////////////////////////////////////////////////////////////////
		// load platform specific routes:
		bind(Routes.class).asEagerSingleton();

		// start the framework
		install(new ninja.Configuration());

		// /////////////////////////////////////////////////////////////////////
		// Some guice bindings
		// /////////////////////////////////////////////////////////////////////
		// some additional bindings for the application:
		bind(GreetingService.class).to(GreetingServiceImpl.class);

		// /////////////////////////////////////////////////////////////////////
		// Setting of ninja variables for startup:
		// /////////////////////////////////////////////////////////////////////
		setProperty("secret", "ssdasdasdasd");

		// for flash and session cookies
		setProperty("sessionExpireTimeInMs", 100000);
		setProperty("sessionSendOnlyIfChanged", true);

	}

}
