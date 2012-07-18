package ninja.i18n;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {

	private String DEFAULT_MESSAGES_LOCATION = "conf.messages";

	/**
	 * prefix used for the getAll methods.
	 * 
	 * All keys of the original properties file will be prefixed with 
	 * this one.
	 * 
	 * This is important as getAll is usually used by the templating engine...
	 */
	private String allPrefix = "i18n_";
	

	@Inject
	public LangImpl() {
		// nothing to do...
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

		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				DEFAULT_MESSAGES_LOCATION, locale, getClass().getClassLoader(),
				new UTF8Control());

		try {
			return MessageFormat.format(resourceBundle.getString(key), params);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	@Override
	public Map<String, String> getAll(Locale locale, Object... params) {

		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				DEFAULT_MESSAGES_LOCATION, locale, getClass().getClassLoader(),
				new UTF8Control());

		return convertResourceBundleToMap(resourceBundle);

	}

	/**
	 * Returns a possibly formatted message.
	 * 
	 * @param key
	 * @param params
	 * @return
	 */
	@Override
	public String get(String key, Object... params) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				DEFAULT_MESSAGES_LOCATION, new UTF8Control());

		try {
			return MessageFormat.format(resourceBundle.getString(key), params);
		} catch (MissingResourceException e) {
			return null;
		}
	}

    /**
     * Returns a possibly formatted message.
     *
     * @param key The key
     * @param defaultMessage The default message if the key isn't found
     * @param params The params
     * @return The formatted message
     */
    @Override
    public String getWithDefault(String key, String defaultMessage, Object... params) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                DEFAULT_MESSAGES_LOCATION, new UTF8Control());

        try {
            return MessageFormat.format(resourceBundle.getString(key), params);
        } catch (MissingResourceException e) {
            return MessageFormat.format(defaultMessage, params);
        }
    }


    /**
     * Returns a possibly formatted message.
     *
     * @param key The key
     * @param defaultMessage The default message if the key isn't found
     * @param locale The locale
     * @param params The params
     * @return The formatted message
     */
    @Override
    public String getWithDefault(String key, String defaultMessage, Locale locale, Object... params) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(
                DEFAULT_MESSAGES_LOCATION, locale, new UTF8Control());

        try {
            return MessageFormat.format(resourceBundle.getString(key), params);
        } catch (MissingResourceException e) {
            return MessageFormat.format(defaultMessage, params);
        }
    }

    @Override
	public Map<String, String> getAll(Object... params) {

		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				DEFAULT_MESSAGES_LOCATION, new UTF8Control());

		return convertResourceBundleToMap(resourceBundle);

	}

	private Map<String, String> convertResourceBundleToMap(
			ResourceBundle resourceBundle) {
		Map<String, String> map = Maps.newHashMap();

		Enumeration<String> keys = resourceBundle.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			map.put(allPrefix + key, resourceBundle.getString(key));
		}

		return map;
	}

}
