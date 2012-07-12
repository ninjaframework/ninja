package ninja.utils;

/**
 * Usually all strings the templating engine gets are HTML escaped by default.
 * 
 * If you don't want to escape them use this class to wrap them.
 */
public class NoEscapeString {

	private String string;
	
	private NoEscapeString(String string) {
		this.string = string;
	}
	
	public static NoEscapeString of(String string) {
		NoEscapeString noEscapeString = new NoEscapeString(string);
		return noEscapeString;
	}
	
}
