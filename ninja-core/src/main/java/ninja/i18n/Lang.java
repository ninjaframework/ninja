package ninja.i18n;

import java.util.Locale;

import com.google.inject.ImplementedBy;

@ImplementedBy(LangImpl.class)
public interface Lang {
    
    String get(String string, Object... object);

	String get(String key, Locale locale, Object ... object);

}
