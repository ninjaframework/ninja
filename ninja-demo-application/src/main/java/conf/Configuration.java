package conf;

import com.google.inject.AbstractModule;

import etc.GreetingService;
import etc.GreetingServiceImpl;

public class Configuration extends AbstractModule {

	protected void configure() {
		// load platform specific routes:
		bind(Routes.class).asEagerSingleton();
		
		// start the framework
		install(new ninja.Configuration());	
		
		// some additional bindings for the application:
		bind(GreetingService.class).to(GreetingServiceImpl.class);
		
	}

}
