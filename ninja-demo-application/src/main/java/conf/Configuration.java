package conf;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

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
			
		
		/////
		// stuff that will go into a .conf file soon... (now done as guice annotation...):
		
		// for the Crypto class:
		bind(String.class).annotatedWith(Names.named("secret")).toInstance("sddasdasdadad");
		
		//for flash and session cookies
		bind(Integer.class).annotatedWith(Names.named("sessionExpireTime")).toInstance(100000);
		bind(Boolean.class).annotatedWith(Names.named("sessionSendOnlyIfChanged")).toInstance(true);
		
	}

}
