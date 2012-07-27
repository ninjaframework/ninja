package controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class I18nController {
    
    /**
     * This is the system wide logger. You can still use any config you like.
     * Or create your own custom logger.
     * 
     * But often this is just a simple solution:
     */
    @Inject
    public Logger logger;
    
    @Inject
    Lang lang;

	public Result index(Context context) {
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		return Results.html();

	}
	
}
