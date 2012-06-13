package controllers;

import javax.servlet.http.Cookie;

import ninja.Context;
import ninja.FilterWith;
import ninja.SecureFilter;
import ninja.Tuple;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import etc.GreetingService;
import filters.TeaPotFilter;

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

		context.html();

	}
	
	
	@FilterWith(TeaPotFilter.class)
	public void teapot(Context context) {
		
		//this will never be executed. Have a look at the TeaPotFilter.class!
		
	}

}
