package ninjaModuleDemo.controllers;

import ninja.Context;

public class ApplicationController {
	
	public void registration(Context context) {
		System.out.println("render json!");
		context.renderHtml();
		
	}

}
