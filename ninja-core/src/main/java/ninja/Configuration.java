package ninja;

import ninja.utils.LoggerProvider;

import org.slf4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

/**
 * The basic configuration of the main ninja framework.
 * 
 * @author ra
 *
 */
public class Configuration extends AbstractModule {

	public void configure() {

		System.setProperty("file.encoding", "utf-8");

		// general classes for servlet container:
		bind(Route.class).to(RouteImpl.class);

		bind(Router.class).to(RouterImpl.class).in(Singleton.class);

		bind(Ninja.class).to(NinjaImpl.class).in(Singleton.class);

		bind(Context.class).to(ContextImpl.class);


		//provide logging
		bind(Logger.class).toProvider(LoggerProvider.class);

		// bind the error views to their real templates
		// => can be later customized by user. but default views should be ok
		// for now.
		bind(String.class).annotatedWith(Names.named("template404")).toInstance(
		    "views/notFound404.ftl.html");

	}

}
