package ninja.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {

	private String DEFAULT_MESSAGES_LOCATION = "conf.messages";

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


}
