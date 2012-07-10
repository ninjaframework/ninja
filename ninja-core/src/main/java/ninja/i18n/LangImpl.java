package ninja.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LangImpl implements Lang {
    
    private String DEFAULT_MESSAGES_LOCATION = "conf.messages";
    
    ResourceBundle bundle;
    
    @Inject
    public LangImpl() {
         bundle = ResourceBundle.getBundle(DEFAULT_MESSAGES_LOCATION, new UTF8Control());

    }
    
    /**
     * Returns a possibly formatted message.
     * 
     * @param key
     * @param params
     * @return
     */
    public String get(String key, Object... params) {
        try {
            return MessageFormat.format(bundle.getString(key), params);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

}
