package ninja.i18n;

import java.text.MessageFormat;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.inject.ImplementedBy;

@ImplementedBy(LangImpl.class)
public interface Lang {

    /**
     * 
     * Get a translated string. The language is determined by the provided
     * locale or a suitable fallback.
     * 
     * values of keys can use the MessageFormat.
     * 
     * More here:
     * http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html
     * 
     * But in short you can use something like mymessage=my message with a
     * placeholder {0}
     * 
     * parameter will then be used to fill {0} with the content.
     * 
     * 
     * 
     * @param key
     *            The key used in your message file (conf/messages.properties)
     * @param language
     *            The language to get. Can be null - then the default language
     *            is returned. It also looks for a fallback. Eg. A request for
     *            "en-US" will fallback to "en" if there is no matching language
     *            file.
     * @param parameter
     *            Parameters to use in formatting the message of the key (in
     *            {@link MessageFormat}).
     * @return The matching and formatted value or null if not found.
     */
    String get(String key, @Nullable String language, Object... parameter);

    /**
     * 
     * Gets a message for a message key. Returns a defaultValue if not found.
     * 
     * 
     * @param key
     *            The key used in your message file (conf/messages.properties)
     * 
     * @param defaultMessage
     *            A default message that will be used when no matching message
     *            can be retrieved.
     * @param language
     *            The language to get. Can be null - then the default language
     *            is returned. It also looks for a fallback. Eg. A request for
     *            "en-US" will fallback to "en" if there is no matching language
     *            file.
     * @param parameter
     *            Parameters to use in formatting the message of the key (in
     *            {@link MessageFormat}).
     * @return The matching and formatted value (either from messages or the
     *         default one).
     */
    String getWithDefault(String key,
                          String defaultMessage,
                          @Nullable String language,
                          Object... params);

    /**
     * 
     * Returns all messages for a language we have. Please note that this method
     * does NOT format any MessageFormat values. You have to do that yourself in
     * the controller logic most likely.
     * 
     * @param language
     *            The language to get. Can be null - then the default language
     *            is returned. It also looks for a fallback. Eg. A request for
     *            "en-US" will fallback to "en" if there is no matching language
     *            file.
     * 
     * @return A map with all messages as <String, String>
     */
    Map<Object, Object> getAll(@Nullable String language);

}
