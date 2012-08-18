package ninja.session;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;

import com.google.inject.ImplementedBy;

/**
 * Flash scope: A client side cookie that can be used to transfer information
 * from one request to another.
 * 
 * Stuff in a flash cookie gets deleted after the next request.
 * 
 * Please note also that flash cookies are not signed.
 */
@ImplementedBy(FlashCookieImpl.class)
public interface FlashCookie {

    void init(Context context);

    void save(Context context);

    void put(String key, String value);

    void put(String key, Object value);

    void now(String key, String value);

    void error(String value, Object... args);

    void success(String value, Object... args);

    void discard(String key);

    void discard();

    void keep(String key);

    void keep();

    String get(String key);

    boolean remove(String key);

    void clearCurrentFlashCookieData();

    boolean contains(String key);

    String toString();

    Map<String, String> getCurrentFlashCookieData();

    Map<String, String> getOutgoingFlashCookieData();
}
