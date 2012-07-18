package ninja.session;

import java.util.Map;

import ninja.Context;

import com.google.inject.ImplementedBy;

/**
 * Session Cookie... Mostly an adaption of Play1's excellent cookie system that
 * in turn is based on the new client side rails cookies.
 */
@ImplementedBy(SessionCookieImpl.class)
public interface SessionCookie {
	
	public void init(Context context);

	/**
	 * @return id of a session.
	 */
	public String getId();

	/**
	 * @return complete content of session.
	 */
	public Map<String, String> getData();

	/**
	 * @return an authenticity token or generates a new one.
	 */
	public String getAuthenticityToken();

	public void save(Context context);


	public void put(String key, String value);

	/**
	 * Returns the value of the key or null.
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key);

	/**
	 * Removes the value of the key and returns the value or null.
	 * 
	 * @param key
	 * @return
	 */
	public String remove(String key);

	public void clear();
	/**
	 * Returns true if the session is empty, e.g. does not contain anything else
	 * than the timestamp key.
	 */
	public boolean isEmpty();

}
