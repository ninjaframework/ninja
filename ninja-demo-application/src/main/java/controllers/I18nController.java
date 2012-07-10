package controllers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ninja.Context;
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

	public void index(Context context) {
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		context.renderHtml();

	}
	
}
