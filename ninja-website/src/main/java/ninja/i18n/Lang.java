package ninja.i18n;

import java.util.Locale;
import java.util.Map;

import com.google.inject.ImplementedBy;

@ImplementedBy(LangImpl.class)
public interface Lang {
    
	/**
	 * Get a translated string.
	 * The language is determined by the system we are running on or a suitable fallback.
	 * 
	 * values of keys can use the MessageFormat.
	 * 
	 * More here:
	 * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
	 * 
	 * But in short you can use something like
	 * mymessage=my message with a placeholder {0}
	 * 
	 * parameter will then be used to fill {0} with the content.
	 * 
	 */
    String get(String string, Object... parameter);

	/**
	 * Get a translated string.
	 * The language is determined by the provided locale or a suitable fallback.
	 * 
	 * values of keys can use the MessageFormat.
	 * 
	 * More here:
	 * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
	 * 
	 * But in short you can use something like
	 * mymessage=my message with a placeholder {0}
	 * 
	 * parameter will then be used to fill {0} with the content.
	 * 
	 */
	String get(String key, Locale locale, Object ... parameter);

	/**
	 * The language is determined by the system we are running on or a suitable fallback.
	 * 
	 * This returns all resources as one map. Usually you want to use that
	 * inside your templating engines so that they can access all correctly
	 * translated messages.
	 * 
	 * By convention all key names are prefixed by "i18n_". That's a convention
	 * you must follow when accessing the keys. But the prefix makes sure
	 * that nothing gets overwritten by the i18n messages.
	 * 
	 */
	Map<String, String> getAll(Object ... parameter);

	/**
	 * The language is determined by the provided locale or a suitable fallback.
	 * 
	 * This returns all resources as one map. Usually you want to use that
	 * inside your templating engines so that they can access all correctly
	 * translated messages.
	 * 
	 * By convention all key names are prefixed by "i18n_". That's a convention
	 * you must follow when accessing the keys. But the prefix makes sure
	 * that nothing gets overwritten by the i18n messages.
	 * 
	 */
	Map<String, String> getAll(Locale locale, Object ...parameter);

}
