package ninja;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class Configuration extends AbstractModule {

	public void configure() {	
		
		System.setProperty("file.encoding", "utf-8");
		
		// general classes for servlet container:
		bind(Route.class).to(RouteImpl.class);
		
		bind(Router.class).to(RouterImpl.class).in(Singleton.class);
				
		bind(Ninja.class).to(NinjaImpl.class).in(Singleton.class);
		
		bind(Context.class).to(ContextImpl.class);
	}

}
