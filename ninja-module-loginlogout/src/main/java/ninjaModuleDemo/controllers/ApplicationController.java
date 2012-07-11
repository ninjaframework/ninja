package ninjaModuleDemo.controllers;

import ninja.Context;
import ninja.Result;
import ninja.Results;

public class ApplicationController {
	
	public Result registration(Context context) {
		System.out.println("render json!");
		
		return Results.html();
		
	}

}
