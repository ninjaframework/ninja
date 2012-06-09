package controllers;

import models.Person;
import ninja.Context;

import com.google.inject.Singleton;

@Singleton
public class SecondController {


	public void second(Context context) {
				
		Person person = new Person();
		person.name = "zeeess name";
		
		//render
		context.renderJson(person);
	}

}
