package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.i18n.Lang;
import ninja.params.PathParam;

import org.slf4j.Logger;

import com.google.common.collect.Maps;
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


	public Result examples(Context context) {
	    logger.info("In example ");
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		return Results.html();

	}
	
	
	public Result index(Context context) {
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		return Results.html();

	}
	
	public Result userDashboard(
			@PathParam("name") String name,
			@PathParam("id") String id,
			Context context) {

		Map<String, String> map = new HashMap<String, String>();
		//generate tuples
		map.put("id", id);
		map.put("name", name);

		//and render page with both parameters:
		return Results.html().render(map);

	}

	public Result redirect(Context context) {
		// Redirects back to the main page simply call redirect
		return Results.redirect("/");

	}
	
	public Result session(Context context) {
		context.getSessionCookie().put("username", "kevin");
		
		return Results.html().render(context.getSessionCookie().getData());

	}
	
	public Result htmlEscaping(Context context) {
		
		//just an example of html escaping in action.
		//just visit /htmlEscaping and check out the source
		//all problematic characters will be escaped 
		String maliciousJavascript = "<script>alert('Hello');</script>";
		
		Map<String, String> renderMap = Maps.newHashMap();
		renderMap.put("maliciousJavascript", maliciousJavascript);
		
		return Results.html(renderMap);
		
		
	}


}
