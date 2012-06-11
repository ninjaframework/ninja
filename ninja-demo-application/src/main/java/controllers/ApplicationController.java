package controllers;

import ninja.Context;
import ninja.FilterWith;
import ninja.SecureFilter;
import ninja.Tuple;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import etc.GreetingService;

@Singleton
public class ApplicationController {

	public void index(Context context) {
		// Default rendering is simple by convention
		// This renders the page in views/ApplicationController/index.ftl.html
		context.html();

	}
	
	public void userDashboard(Context context) {
		
		//get parameters from context:
		String id = context.getPathParameter("id");				
		String name = context.getPathParameter("name");

		//generate tuples
		Tuple<String, String> tuple = new Tuple<String, String>("id", id);
		Tuple<String, String> tuple2 = new Tuple<String, String>("name", name);

		//and render page with both parameters:
		context.html(tuple, tuple2);

	}

	public void redirect(Context context) {
		// Redirects back to the main page somply call redirect
		context.redirect("/");

	}


}
