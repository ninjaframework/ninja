package ninja.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
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
		prod(NinjaConstant.MODE_PROD), dev(NinjaConstant.MODE_DEV), test(
				NinjaConstant.MODE_TEST);

		private String mode;

		Mode(String mode) {
			this.mode = mode;
		}

		public String toString() {
			return mode;
		}
	}

	private final String ERROR_KEY_NOT_FOUND = "Key %s does not exist. Please include it in your application.conf. Otherwise this app will not work";

	/** 
	 * This is the final configuration holding all information from
	 * 1. application.conf.
	 * 2. Special properties for the mode you are running on extracted from application.cof
	 * 3. An external configuration file defined by a system property and on the classpath.
	 */
	private CompositeConfiguration compositeConfiguration;

	@Inject
	public NinjaPropertiesImpl() {

		// Get mode possibly set via a system property
		String modeFromGetSystemProperty = System
				.getProperty(NinjaConstant.MODE_KEY_NAME);

		// Initially we are in dev mode.
		mode = Mode.dev;

		// If the user specified a mode we set the mode accordingly:
		if (modeFromGetSystemProperty != null) {

			if (modeFromGetSystemProperty.equals(NinjaConstant.MODE_TEST)) {
				mode = Mode.test;
			} else if (modeFromGetSystemProperty
					.equals(NinjaConstant.MODE_PROD)) {
				mode = Mode.prod;
			}

			// else dev as set initially...

		}

		// This is our main configuration.
		// In the following we'll read the individual configurations and merge
		// them into the composite configuration at the end.
		compositeConfiguration = new CompositeConfiguration();

		// That is the default config.
		Configuration defaultConfiguration = null;

		// Config of prefixed mode corresponding to current mode (eg. %test.myproperty=...)
		Configuration prefixedDefaultConfiguration = null;

		// (Optional) Config set via a system property
		Configuration externalConfiguration = null;
		
	    // (Optional) Config of prefixed mode corresponding to current mode (eg. %test.myproperty=...)
        Configuration prefixedExternalConfiguration = null;

		// First step => load application.conf and also merge properties that
		// correspond to a mode into the configuration.
		

		defaultConfiguration = SwissKnife.loadConfigurationInUtf8(NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);
		
		if (defaultConfiguration != null) {
		    // Second step:
			// Copy special prefix of mode to parent configuration
			// By convention it will be something like %test.myproperty
			prefixedDefaultConfiguration = defaultConfiguration.subset("%"
					+ mode.name());

		} else {
			
			// If the property was set, but the file not found we emit
			// a RuntimeException			
			String errorMessage = String.format("Error reading configuration file. Make sure you got a default config file %s",
					NinjaProperties.CONF_FILE_LOCATION_BY_CONVENTION);
			
			logger.error(errorMessage);
			
			throw new RuntimeException(errorMessage);
		}

		
		
		
		// third step => load external configuration when a system property is
		// defined.
		String ninjaExternalConf = System.getProperty(NINJA_EXTERNAL_CONF);

		if (ninjaExternalConf != null) {
			
				// only load it when the property is defined.

				externalConfiguration = SwissKnife.loadConfigurationInUtf8(ninjaExternalConf);
				
				//this should not happen:
				if (externalConfiguration == null) {
					
					String errorMessage = String
							.format("Ninja was told to use an external configuration%n"
									+ " %s = %s %n."
									+ "But the corresponding file cannot be found.%n"
									+ " Make sure it is visible to this application and on the classpath.",
									NINJA_EXTERNAL_CONF, ninjaExternalConf);

					logger.error(errorMessage);

					throw new RuntimeException(errorMessage);
					
					
				} else {
				    
		            // Copy special prefix of mode to parent configuration
		            // By convention it will be something like %test.myproperty
				    prefixedExternalConfiguration = externalConfiguration.subset("%"
		                    + mode.name());
				}
			
		}
		
		
		
		///////////////////////////////////////////////////////////////////////
		// Finally add the stuff to the composite configuration
		// Note: Configurations added earlier will overwrite configurations 
		// added later.
		///////////////////////////////////////////////////////////////////////	
		
	    if (prefixedExternalConfiguration != null) {
	        compositeConfiguration.addConfiguration(prefixedExternalConfiguration);
	    } 
	      
		if (externalConfiguration != null) {
			compositeConfiguration.addConfiguration(externalConfiguration);
		}
		
		if (prefixedDefaultConfiguration != null) {
			compositeConfiguration.addConfiguration(prefixedDefaultConfiguration);
		}
		
		if (defaultConfiguration != null) {
			compositeConfiguration.addConfiguration(defaultConfiguration);
		}
		

	}

	@Override
	public String get(String key) {

		String value;

		try {
			value = compositeConfiguration.getString(key);
		} catch (Exception e) {
			// Fail silently because we handle errors differently. Simply set
			// them null.
			value = null;
		}

		return value;

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

		Integer value;

		try {
			value = compositeConfiguration.getInt(key);
		} catch (Exception e) {
			// Fail silently because we handle errors differently. Simply set
			// them null.
			value = null;
		}

		return value;

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

		Boolean value;

		try {
			value = compositeConfiguration.getBoolean(key);
		} catch (Exception e) {
			// Fail silently because we handle errors differently. Simply set
			// them null.
			value = null;
		}

		return value;

	}

    public void setProperty(String key, String value) {
        compositeConfiguration.setProperty(key, value);
    }

	public void bindProperties(Binder binder) {
		Names.bindProperties(binder,
				ConfigurationConverter.getProperties(compositeConfiguration));
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

	@Override
	public String[] getStringArray(String key) {
		String value = compositeConfiguration.getString(key);
		if (value != null) {
			return Iterables.toArray(Splitter.on(",").trimResults().omitEmptyStrings().split(value), String.class);
		} else {
			return null;
		}
	}

	@Override
	public String getWithDefault(String key, String defaultValue) {
		String value = get(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public Integer getIntegerWithDefault(String key, Integer defaultValue) {
		Integer value = getInteger(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

	@Override
	public Boolean getBooleanWithDefault(String key, Boolean defaultValue) {
		Boolean value = getBoolean(key);
		if (value != null) {
			return value;
		} else {
			return defaultValue;
		}
	}

}
