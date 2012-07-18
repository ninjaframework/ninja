package controllers;

import models.Person;
import ninja.Context;
import ninja.Result;
import ninja.Results;

import com.google.inject.Singleton;

@Singleton
public class PersonController {

	public Result getPerson() {

		//simply render a json as result:
		Person person = new Person();
		person.name = "zeeess name";

		// render
		return Results.json(person);
	}

	public Result postPerson(Person person) {
		// okay... we simply render the parsed object again as json
		// usually we would save something into a db or so..
		return Results.json(person);
	}

}
