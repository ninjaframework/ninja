package ninja.session;

import ninja.Context;

import com.google.inject.ImplementedBy;

/**
 * Flash scope:
 * A client side cookie that can be used to transfer information from one request to another.
 * 
 * Stuff in a flash cookie gets deleted after the next request.
 * 
 * Please note also that flash cookies are not signed.
 */
@ImplementedBy(FlashCookieImpl.class)
public interface FlashCookie {

	public void init(Context context);

	public void save(Context context);

	public void put(String key, String value);

	public void put(String key, Object value);

	public void now(String key, String value);

	public void error(String value, Object... args);

	public void success(String value, Object... args);

	public void discard(String key);

	public void discard();

	public void keep(String key);

	public void keep();

	public String get(String key);

	public boolean remove(String key);

	public void clearCurrentFlashCookieData();

	public boolean contains(String key);

	public String toString();
}
