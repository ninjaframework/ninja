package controllers;

import models.Person;
import ninja.Context;

import com.google.inject.Singleton;

@Singleton
public class PersonController {

	public void getPerson(Context context) {

		//simply render a json as result:
		Person person = new Person();
		person.name = "zeeess name";

		// render
		context.renderJson(person);
	}

	public void postPerson(Context context) {

		//parsing a request body into a java value is simple:
		Person person = context.parseBody(Person.class);
		
		//parseBody always uses a registered parser for the request content type...

		// okay... we simply render the parsed object again as json
		// usually we would save something into a db or so..
		context.renderJson(person);
	}

}
