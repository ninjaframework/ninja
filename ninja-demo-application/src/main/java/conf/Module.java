package conf;

import com.google.inject.AbstractModule;

import controllers.UdpPingController;
import etc.GreetingService;
import etc.GreetingServiceImpl;

public class Module extends AbstractModule {

	protected void configure() {

		// /////////////////////////////////////////////////////////////////////
		// Some guice bindings
		// /////////////////////////////////////////////////////////////////////
		// some additional bindings for the application:
		bind(GreetingService.class).to(GreetingServiceImpl.class);
        // Bind the UDP ping controller so it starts up on server start
        bind(UdpPingController.class);
	}

}
