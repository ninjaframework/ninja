package controllers;


import ninja.Context;
import ninja.FilterWith;
import ninja.SecureFilter;

import com.google.inject.Singleton;

import filters.LoggerFilter;
import filters.TeaPotFilter;

import javax.servlet.http.Cookie;

@Singleton
public class FilterController {

	/**
	 * Not yet finished.
	 * 
	 * Simply demonstrates how controllers can be annotated and filtered using
	 * the FilterWith annotation:
	 * 
	 * @param context
	 */
	@FilterWith(SecureFilter.class)
	public void filter(Context context) {

		context.getHttpServletResponse().addCookie(
				new Cookie("myname", "myvalue"));

		// System.out.println("cookies: " +
		// context.getHttpServletRequest().getCookies());

		if (context.getHttpServletRequest().getCookies() != null) {
			for (int i = 0; i < context.getHttpServletRequest().getCookies().length; i++) {
				System.out.println("cookie: "
						+ context.getHttpServletRequest().getCookies()[i]
								.getName());

			}
		}

		context.renderHtml();

	}
	/**
	 * Really cool. We are using two filters on the method.
	 * 
	 * Filters are executed sequentially. First the LoggerFilter then the
	 * TeaPotFilter.
	 * 
	 * The TeaPotFilter changes completely the output and the status.
	 * 
	 * @param context
	 */
	@FilterWith({
		LoggerFilter.class, 
		TeaPotFilter.class})
	public void teapot(Context context) {
		
		//this will never be executed. Have a look at the TeaPotFilter.class!
		
	}

}
