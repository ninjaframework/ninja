package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;

import com.google.inject.Singleton;

@Singleton
public class ApplicationController {

	public void examples(Context context) {
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
