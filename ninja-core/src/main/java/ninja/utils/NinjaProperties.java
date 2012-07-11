package ninja.utils;

import com.google.inject.ImplementedBy;

@ImplementedBy(NinjaPropertiesImpl.class)
public interface NinjaProperties {

	/**
	 * Get a String property or null if it is not there...
	 * 
	 * @param key
	 * @return the property of null if not there
	 */
	String get(String key);

	/**
	 * Get a property as Integer of null if not there / or property no integer
	 * 
	 * @param key
	 * @return the property or null if not there or property no integer
	 */
	Integer getInteger(String key);

	/**
	 * 
	 * @param key
	 * @return the property or null if not there or property no boolean
	 */
	Boolean getBoolean(String key);

	/**
	 * The "die" method forces this key to be set. Otherwise a runtime exception
	 * will be thrown.
	 * 
	 * @param key
	 * @return the boolean or a RuntimeException will be thrown.
	 */
	Boolean getBooleanOrDie(String key);

	/**
	 * The "die" method forces this key to be set. Otherwise a runtime exception
	 * will be thrown.
	 * 
	 * @param key
	 * @return the Integer or a RuntimeException will be thrown.
	 */
	Integer getIntegerOrDie(String key);

	/**
	 * The "die" method forces this key to be set. Otherwise a runtime exception
	 * will be thrown.
	 * 
	 * @param key
	 * @return the String or a RuntimeException will be thrown.
	 */
	String getOrDie(String key);

    /**
     * Whether we are in dev mode
     *
     * @return True if we are in dev mode
     */
    boolean isDev();
}
