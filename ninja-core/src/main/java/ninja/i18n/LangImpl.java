package ninja.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ninja.utils.NinjaProperties;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {

	private static Logger logger = LoggerFactory.getLogger(LangImpl.class);

	private String DEFAULT_MESSAGES_LOCATION = "conf.messages";

	private Map<String, Configuration> langToKeyAndValuesMapping;

	@Inject
	public LangImpl(NinjaProperties ninjaProperties) {

		langToKeyAndValuesMapping = Maps.newHashMap();

		// load default messages:
		Configuration defaultLanguage = loadPropertiesInUtf8("conf/messages.properties");

		if (defaultLanguage == null) {
			throw new RuntimeException(
			        "Did not find conf/messages.properties. Please add a default language file.");
		} else {
			langToKeyAndValuesMapping.put("", defaultLanguage);
		}

		String[] applicationLangs = ninjaProperties
		        .getStringArray("application.langs");
		for (String lang : applicationLangs) {

			Configuration configuration = loadPropertiesInUtf8(String.format(
			        "conf/messages.%s.properties", lang));

			Configuration configurationLangOnly = null;

			if (lang.contains("-")) {
				String langOnly = lang.split("-")[0];

				configurationLangOnly = loadPropertiesInUtf8(String.format(
				        "conf/messages.%s.properties", langOnly));

			}

			if (configuration == null) {
				logger.info(String
				        .format("Did not find conf/messages.%s.properties but it was specified in application.conf. Using default language instead.",
				                lang));

			} else {

				// add new language, but combine with default language if stuff
				// is missing...
				CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
				compositeConfiguration.addConfiguration(configuration);
				if (configurationLangOnly != null) {
					compositeConfiguration
					        .addConfiguration(configurationLangOnly);
				}
				compositeConfiguration.addConfiguration(defaultLanguage);

				langToKeyAndValuesMapping.put(lang,
				        (Configuration) compositeConfiguration);
			}

		}

	}

	private String getLangCountryFromLocale(Locale locale) {

		String country = locale.getCountry();

		if (country.isEmpty())
			return null;

		String language = locale.getLanguage();

		if (language.isEmpty())
			return null;

		return String.format("%s-%s", language, country);

	}

	private String getLangFromLocale(Locale locale) {

		String language = locale.getLanguage();

		if (language.isEmpty())
			return null;

		return String.format("%s", language);

	}

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

	/**
	 * Returns a possibly formatted message.
	 * 
	 * @param key
	 * @param params
	 * @return
	 */
	@Override
	public String get(String key, Locale locale, Object... params) {

		Configuration configuration = getLanguageConfigurationForLocale(locale);

		return MessageFormat.format(configuration.getString(key), params);

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
		ResourceBundle resourceBundle = ResourceBundle.getBundle(
		        DEFAULT_MESSAGES_LOCATION, locale, new UTF8Control());

		try {
			return MessageFormat.format(resourceBundle.getString(key), params);
		} catch (MissingResourceException e) {
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
	 * This is important: We load stuff as UTF-8
	 * 
	 * @param classLoaderUrl
	 *            Classpath location of the configuration file. Eg
	 *            /conf/heroku.conf
	 * @return A configuration or null if there were problems getting it.
	 */
	private Configuration loadPropertiesInUtf8(String classLoaderUrl) {

		PropertiesConfiguration c = new PropertiesConfiguration();

		URL resource = getClass().getClassLoader().getResource(classLoaderUrl);

		// if the resource cannot be found return null
		if (resource == null) {
			return null;
		}

		try {
			InputStream inputStream = resource.openStream();

			c.load(new InputStreamReader(inputStream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			c = null;
			logger.error(
			        "Unsupported encoding while loading configuration file", e);
		} catch (IOException e) {
			c = null;
			logger.error("Could not find configuration file. Was looking for "
			        + classLoaderUrl, e);
		} catch (NullPointerException e) {

			logger.error("Could not find configuration file. Was looking for "
			        + classLoaderUrl, e);
			return null;
		} catch (ConfigurationException e) {
			c = null;
			logger.error("Configuration Exception.", e);
		}

		return (Configuration) c;
	}

}
