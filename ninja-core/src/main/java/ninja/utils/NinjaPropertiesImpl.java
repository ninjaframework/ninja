package ninja.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

@Singleton
public class NinjaPropertiesImpl implements NinjaProperties {

	private static final Logger logger = LoggerFactory
			.getLogger(NinjaPropertiesImpl.class);
	
	
	Mode mode;

	private enum Mode {
		prod(NinjaConstant.MODE_PROD), 
		dev(NinjaConstant.MODE_DEV), 
		test(NinjaConstant.MODE_TEST);

		private String mode;

		Mode(String mode) {
			this.mode = mode;
		}

		public String toString() {
			return mode;
		}
	}


	private final String ERROR_KEY_NOT_FOUND = "Key %s does not exist. Please include it in your application.conf. Otherwise this app will not work";

	//private final Properties allCurrentNinjaProperties;

	// private final String mode;
	
	CompositeConfiguration compositeConfiguration;

	@Inject
	public NinjaPropertiesImpl() {
		//this.allCurrentNinjaProperties = new Properties();

		// get system variables... load application conf files...
		String modeFromGetSystemProperty = System
				.getProperty(NinjaConstant.MODE_KEY_NAME);

		// initially we are in dev mode.
		mode = Mode.dev;

		// if the user specified something we set the mode accordingly:
		if (modeFromGetSystemProperty != null) {

			if (modeFromGetSystemProperty.equals(NinjaConstant.MODE_TEST)) {
				mode = Mode.test;
			} else if (modeFromGetSystemProperty
					.equals(NinjaConstant.MODE_PROD)) {
				mode = Mode.prod;
			}

			// else dev as set before...

		}
		
		compositeConfiguration = new CompositeConfiguration();	
		
		PropertiesConfiguration applicationConf;
		try {
			applicationConf = new PropertiesConfiguration(CONF_FILE_LOCATION_BY_CONVENTION);
			compositeConfiguration.addConfiguration(applicationConf);

		} catch (ConfigurationException e) {
			e.printStackTrace();
		} 
		
	
		

//		// 1. load application.conf
//		Optional<Properties> applicationProperties = loadPropertiesInUtf8(CONF_FILE_LOCATION_BY_CONVENTION);
//
//		if (!applicationProperties.isPresent()) {
//			throw new RuntimeException(
//					"No basic configuration file found. Please make sure you got a file called: "
//							+ CONF_FILE_LOCATION_BY_CONVENTION);
//		}
//
//		// 2. Add all properties that are relevant for this mode:
//		allCurrentNinjaProperties.putAll(getAllPropertiesOfThatMode(
//				applicationProperties.get(), mode.name()));
//
//		// 3. load an external configuration file:
//		// get system variables... load application conf files...
//		if (System.getProperty(NINJA_EXTERNAL_CONF) != null) {
//
//			String ninjaExternalConf = System.getProperty(NINJA_EXTERNAL_CONF);
//
//			Optional<Properties> externalConfiguration = loadPropertiesInUtf8(ninjaExternalConf);
//
//			if (!applicationProperties.isPresent()) {
//				throw new RuntimeException(
//						"A system property called "
//								+ NINJA_EXTERNAL_CONF
//								+ " was set. But the correspinding file cannot be found. Make sure it is visible to this application and on the classpath.");
//			}
//
//			allCurrentNinjaProperties.putAll(externalConfiguration.get());
//
//		}

	}

	@Override
	public String get(String key) {
		return compositeConfiguration.getString(key);

	}

	@Override
	public String getOrDie(String key) {

		String value = get(key);

		if (value == null) {
			logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
			throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
		} else {
			return value;
		}

	}

	@Override
	public Integer getInteger(String key) {
		
		return compositeConfiguration.getInt(key);

	}

	@Override
	public Integer getIntegerOrDie(String key) {

		Integer value = getInteger(key);

		if (value == null) {
			logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
			throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
		} else {
			return value;
		}

	}

	@Override
	public Boolean getBooleanOrDie(String key) {

		Boolean value = getBoolean(key);

		if (value == null) {
			logger.error(String.format(ERROR_KEY_NOT_FOUND, key));
			throw new RuntimeException(String.format(ERROR_KEY_NOT_FOUND, key));
		} else {
			return value;
		}

	}

	@Override
	public Boolean getBoolean(String key) {
		
		
		return compositeConfiguration.getBoolean(key);


	}

	public void bindProperties(Binder binder) {
		Names.bindProperties(binder, ConfigurationConverter.getProperties(compositeConfiguration));
	}

	/**
	 * This properties loader uses UTF-8... that's important...
	 * 
	 * @param classLoaderUrl
	 * @return
	 */
	private Optional<Properties> loadPropertiesInUtf8(String classLoaderUrl) {
		Properties props = new Properties();
		URL resource = getClass().getClassLoader().getResource(classLoaderUrl);

		try {
			props.load(new InputStreamReader(resource.openStream(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			props = null;
			logger.error(
					"Unsupported encoding while loading configuration file", e);
		} catch (IOException e) {
			props = null;
			logger.error("Could not find configuration file. Was looking for "
					+ classLoaderUrl, e);
		} catch (NullPointerException e) {
			props = null;
			logger.error("Could not find configuration file. Was looking for "
					+ classLoaderUrl, e);
		}

		return Optional.fromNullable(props);
	}

	/**
	 * Of course we want to have environments. By default ninja supports 3
	 * modes: - "test" - "dev" - "prod"
	 * 
	 * We are using one application.conf file containing all relevant
	 * configuration properties. As convention you have to use "%" when
	 * prefixing a property.
	 * 
	 * For instance if we are in mode "test" This: myproperty = funk is
	 * overwritten by %test.myproperty = funkier
	 * 
	 */
	private Properties getAllPropertiesOfThatMode(Properties properties,
			String mode) {

		Properties returnProperties = new Properties();

		// The hashmap we get from properties is not ordered.
		// We therefore do two passes
		// Pass 1: Add all non % arguments
		// Pass 2: Add all % arguments matching this mode
		// => therefore matching % arguments will override the original ones
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {

			if (!entry.getKey().toString().startsWith("%")) {
				returnProperties.put(entry.getKey(), entry.getValue());

			}
		}

		// Pass 2: Add all % arguments matching this mode
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {

			if (entry.getKey().toString().startsWith("%")) {

				// check if this is the mode I want to use...
				String myModePrefix = "%" + mode;

				if (entry.getKey().toString().startsWith(myModePrefix)) {
					// please replace me:
					// %test.myproperty=test should become
					// myproperty=test
					String keyToReplace = "%" + mode + "\\.";

					String newKeyName = entry.getKey().toString()
							.replaceFirst(keyToReplace, "");

					returnProperties.put(newKeyName, entry.getValue());
				} // else do nothing... that's a property I don't want to
					// use....

			}
		}

		return returnProperties;

	}

	@Override
	public boolean isProd() {
		return (mode.equals(Mode.prod));
	}

	public boolean isDev() {
		return (mode.equals(Mode.dev));
	}

	@Override
	public boolean isTest() {
		return (mode.equals(Mode.test));
	}
	
	@Override
	public Properties getAllCurrentNinjaProperties() {
		
		return ConfigurationConverter.getProperties(compositeConfiguration);
		
	}

}
