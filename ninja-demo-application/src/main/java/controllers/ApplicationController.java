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

	private GreetingService greeter;

	@Inject
	public ApplicationController(GreetingService greeter) {
		this.greeter = greeter;

	}
	

	public void index(Context context) {
		
		String id = context.getPathParameter("id");		
		
		String name = context.getPathParameter("name");

		Tuple<String, String> tuple = new Tuple<String, String>("id", id);
		Tuple<String, String> tuple2 = new Tuple<String, String>("name", name);

		context.html(tuple, tuple2);

	}

	public void cathrin(Context context) {
		
		
		String nachricht = context.getPathParameter("nachricht");
		
		String leniort = context.getPathParameter("leniort");
		

		Tuple<String, String> tuple = new Tuple<String, String>("nachricht", nachricht);

		Tuple<String, String> tuple2 = new Tuple<String, String>("leniort", leniort);
		
		context.html(tuple, tuple2);

	}

	@FilterWith(SecureFilter.class)
	public void technique(Context context) {

		context.html();

	}

}
