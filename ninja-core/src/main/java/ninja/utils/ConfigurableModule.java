package ninja.utils;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * This is just a little helper let simplifies setting properties.
 * Properties will then be injected by Guice into annotated classes.
 * 
 * something like @Named("myProperty") String myProperty.
 * 
 * Simply set it via:
 * setProperty("myProperty", "value"). 
 * 
 * That's it.
 * 
 * => in the future the discrimination with more than one prefix
 * can be made here...
 * 
 * @author ra
 *
 */
public abstract class ConfigurableModule extends AbstractModule {
	
	protected void setProperty(String key, Integer value) {

		bind(Integer.class).annotatedWith(Names.named(key)).toInstance(value);

	}

	protected void setProperty(String key, String value) {

		bind(String.class).annotatedWith(Names.named(key)).toInstance(value);

	}

	protected void setProperty(String key, Boolean value) {

		bind(Boolean.class).annotatedWith(Names.named(key)).toInstance(value);

	}

}
