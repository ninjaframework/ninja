package ninja.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.SwissKnife;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {

	private static Logger logger = LoggerFactory.getLogger(LangImpl.class);

	private Map<String, Configuration> langToKeyAndValuesMapping;

	private final NinjaProperties ninjaProperties;

	@Inject
	public LangImpl(NinjaProperties ninjaProperties) {

		this.ninjaProperties = ninjaProperties;
		this.langToKeyAndValuesMapping = Maps.newHashMap();
		
		loadAllMessageFilesForRegisteredLanguages();


	}
	


	/**
	 * Returns a possibly formatted message.
	 * 
	 * @param key
	 * @param params
	 * @return a pssibly formatted message or null if not found.
	 */
	@Override
	public String get(String key, Locale locale, Object... params) {

		Configuration configuration = getLanguageConfigurationForLocale(locale);
		
		String value = configuration.getString(key);
		
		if (value != null) {
			return MessageFormat.format(value, params);
		} else {
			return null;
		}

	}

	@Override
	public Map<Object, Object> getAll(Locale locale, Object... params) {

		Configuration configuration = getLanguageConfigurationForLocale(locale);

		return ConfigurationConverter.getMap(configuration);

	}

	// /**
	// * Returns a possibly formatted message.
	// *
	// * @param key
	// * @param params
	// * @return
	// */
	// @Override
	// public String get(String key, Object... params) {
	//
	// Configuration configuration = getLanguageConfigurationForLocale(locale);
	//
	//
	// ResourceBundle resourceBundle = ResourceBundle.getBundle(
	// DEFAULT_MESSAGES_LOCATION, new UTF8Control());
	//
	// try {
	// return MessageFormat.format(resourceBundle.getString(key), params);
	// } catch (MissingResourceException e) {
	// return null;
	// }
	// }

	// /**
	// * Returns a possibly formatted message.
	// *
	// * @param key The key
	// * @param defaultMessage The default message if the key isn't found
	// * @param params The params
	// * @return The formatted message
	// */
	// @Override
	// public String getWithDefault(String key, String defaultMessage, Object...
	// params) {
	// ResourceBundle resourceBundle = ResourceBundle.getBundle(
	// DEFAULT_MESSAGES_LOCATION, new UTF8Control());
	//
	// try {
	// return MessageFormat.format(resourceBundle.getString(key), params);
	// } catch (MissingResourceException e) {
	// return MessageFormat.format(defaultMessage, params);
	// }
	// }

	/**
	 * Returns a possibly formatted message.
	 * 
	 * @param key
	 *            The key
	 * @param defaultMessage
	 *            The default message if the key isn't found
	 * @param locale
	 *            The locale
	 * @param params
	 *            The params
	 * @return The formatted message
	 */
	@Override
	public String getWithDefault(String key, String defaultMessage,
	        Locale locale, Object... params) {
		
		String value = get(key, locale, params);
		
		if (value != null) {
			
			return MessageFormat.format(value, params);
				
		} else {
			//return default message
			return MessageFormat.format(defaultMessage, params);
			
		}
		
	}

	// @Override
	// public Map<Obje, String> getAll(Object... params) {
	//
	// ResourceBundle resourceBundle = ResourceBundle.getBundle(
	// DEFAULT_MESSAGES_LOCATION, new UTF8Control());
	//
	// return convertResourceBundleToMap(resourceBundle);
	//
	// }

	/*
	 * private Map<String, String> convertResourceBundleToMap( ResourceBundle
	 * resourceBundle) { Map<String, String> map = Maps.newHashMap();
	 * 
	 * Enumeration<String> keys = resourceBundle.getKeys(); while
	 * (keys.hasMoreElements()) { String key = keys.nextElement();
	 * map.put(allPrefix + key, resourceBundle.getString(key)); }
	 * 
	 * return map; }
	 */



	
	
	/**
	 * Does all the loading of message files.
	 * 
	 * Only registered messages in application.conf are loaded.
	 * 
	 */
	private void loadAllMessageFilesForRegisteredLanguages() {
		

		// Load default messages:
		Configuration defaultLanguage = SwissKnife.loadConfigurationInUtf8("conf/messages.properties");

		// Make sure we got the file.
		// Everything else does not make much sense.
		if (defaultLanguage == null) {
			throw new RuntimeException(
			        "Did not find conf/messages.properties. Please add a default language file.");
		} else {
			langToKeyAndValuesMapping.put("", defaultLanguage);
		}

		// Get the languages from the application configuration.
		String[] applicationLangs = ninjaProperties
		        .getStringArray(NinjaConstant.applicationLanguages);
		
		//If we don't have any languages declared we just return.
		//We'll use the default messages.properties file.
		if (applicationLangs == null) {
			return;
		}
		
		// Load each language into the HashMap containing the languages:
		for (String lang : applicationLangs) {

			// First step: Load complete language eg. en-US
			Configuration configuration = SwissKnife.loadConfigurationInUtf8(String.format(
			        "conf/messages.%s.properties", lang));

			Configuration configurationLangOnly = null;

			// If the language has a country code load the default values for 
			// the language, too. For instance missing variables in en-US will be
			// Overwritten by the default languages.
			if (lang.contains("-")) {
				// get the lang
				String langOnly = lang.split("-")[0];
				
				// And load the configuraion
				configurationLangOnly = SwissKnife.loadConfigurationInUtf8(String.format(
				        "conf/messages.%s.properties", langOnly));

			}

			//This is strange. If you defined the language in application.conf it should be there propably.
			if (configuration == null) {
				logger.info(String
				        .format("Did not find conf/messages.%s.properties but it was specified in application.conf. Using default language instead.",
				                lang));

			} else {

				// add new language, but combine with default language if stuff
				// is missing...
				CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
				//Add eg. "en-US"
				compositeConfiguration.addConfiguration(configuration);
				
				//Add eg. "en"
				if (configurationLangOnly != null) {
					compositeConfiguration
					        .addConfiguration(configurationLangOnly);
				}
				//Add messages.conf (default pack)
				compositeConfiguration.addConfiguration(defaultLanguage);

				//and add the composed configuration to the hashmap with the mapping.
				langToKeyAndValuesMapping.put(lang,
				        (Configuration) compositeConfiguration);
			}

		}
		
		
	}

	/**
	 * Converts locale into "en-US" when possible.
	 * 
	 * @param locale 
	 * @return The 2 letter lang-country ISO code (Eg."en-US") Or null if not possible.
	 */
	private String getLangCountryFromLocale(Locale locale) {

		String country = locale.getCountry();

		if (country.isEmpty())
			return null;

		String language = locale.getLanguage();

		if (language.isEmpty())
			return null;

		return String.format("%s-%s", language, country);

	}

	/**
	 * Returns locale into "en" (ISO language code)
	 * @param locale
	 * @return the 2 letter language ISO code or null if not possible.
	 */
	private String getLangFromLocale(Locale locale) {

		String language = locale.getLanguage();

		if (language.isEmpty())
			return null;

		return String.format("%s", language);

	}

	/**
	 * When a locale comes in this method determines which cached
	 * locale to use from the hashmap.
	 * 
	 * 
	 * 
	 * @param locale
	 * @return The matching configuration from the hashmap.
	 */
	private Configuration getLanguageConfigurationForLocale(Locale locale) {

		String lang = getLangCountryFromLocale(locale);

		if (lang != null) {
			Configuration configuration = langToKeyAndValuesMapping.get(lang);
			if (configuration != null) {

				return configuration;
			}
		}

		lang = getLangFromLocale(locale);

		if (lang != null) {
			Configuration configuration = langToKeyAndValuesMapping.get(lang);

			if (configuration != null) {

				return configuration;
			}
		}

		// this is guaranteed to work => default language.
		return langToKeyAndValuesMapping.get("");

	}
}
