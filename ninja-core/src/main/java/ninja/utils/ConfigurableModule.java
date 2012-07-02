package ninja.utils;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

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
