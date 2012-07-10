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
public class ApplicationController {
    
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

	public void examples(Context context) {
	    System.out.println("lang: " + lang.get("hello2", Locale.CANADA, "Joe"));
	    logger.info("In example ");
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		context.renderHtml();

	}
	
	
	public void index(Context context) {
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		context.renderHtml();

	}
	
	public void userDashboard(Context context) {
		//get parameters from context:
		String id = context.getPathParameter("id");				
		String name = context.getPathParameter("name");

		Map<String, String> map = new HashMap<String, String>();
		//generate tuples
		map.put("id", id);
		map.put("name", name);

		//and render page with both parameters:
		context.renderHtml(map);

	}

	public void redirect(Context context) {
		// Redirects back to the main page somply call redirect
		context.redirect("/");

	}
	
	public void session(Context context) {
		context.getSessionCookie().put("username", "kevin");
		
		context.renderHtml(context.getSessionCookie().getData());

	}


}
