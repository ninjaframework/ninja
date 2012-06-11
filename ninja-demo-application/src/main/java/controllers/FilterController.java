package controllers;

import ninja.Context;
import ninja.FilterWith;
import ninja.SecureFilter;
import ninja.Tuple;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import etc.GreetingService;

@Singleton
public class FilterController {

	/**
	 * Not yet finished.
	 * 
	 * Simply demonstrates how controllers can be annotated and filtered
	 * using the FilterWith annotation:
	 * @param context
	 */
	@FilterWith(SecureFilter.class)
	public void filter(Context context) {

		context.html();

	}

}
