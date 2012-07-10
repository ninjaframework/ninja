package ninja.i18n;

import com.google.inject.ImplementedBy;

@ImplementedBy(LangImpl.class)
public interface Lang {
    
    String get(String string, Object... object);

}
