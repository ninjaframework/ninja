package ninjaModuleDemo.controllers;

import ninja.Context;
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
	
	public void redirect(Context context) {
		
		context.redirect("/technique");
		
	}

	public void index(Context context) {
		
		String id = context.getPathParameter("id");		
		
		String name = context.getPathParameter("name");

		Tuple<String, String> tuple = new Tuple<String, String>("id", id);
		Tuple<String, String> tuple2 = new Tuple<String, String>("name", name);

		context.render(tuple, tuple2);

	}

	public void cathrin(Context context) {
		
		
		String nachricht = context.getPathParameter("nachricht");
		
		String leniort = context.getPathParameter("leniort");
		

		Tuple<String, String> tuple = new Tuple<String, String>("nachricht", nachricht);

		Tuple<String, String> tuple2 = new Tuple<String, String>("leniort", leniort);
		
		context.render(tuple, tuple2);

	}

	public void technique(Context context) {

		context.render();

	}

}
