package controllers;

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

	public void index(Context context) {
		
		String id = context.getPathParameter("id");
		
		
		String name = context.getPathParameter("name");

		Tuple<String, String> tuple = new Tuple<String, String>("id", id);
		Tuple<String, String> tuple2 = new Tuple<String, String>("name", name);

		context.render(tuple, tuple2);

	}

	public void main(Context context) {

		context.render();

	}

	public void technique(Context context) {

		context.render();

	}

}
