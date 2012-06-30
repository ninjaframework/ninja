package ninja.session;

public interface Cookie {
	
	/** prefix used for all Ninja cookies. */
	final String COOKIE_PREFIX = "NINJA";
	
	/** Suffix used for Ninja cookies. Usually results
	 * in cookies like "NINJA_SESSION */
	final String SESSION_SUFFIX = "_SESSION";
	
	/** Suffix used for Ninja cookies. Usually results
	 * in cookies like "NINJA_FLASH */
	final String FLASH_SUFFIX = "_FLASH";

}
