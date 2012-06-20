package controllers;

import java.util.HashMap;
import java.util.Map;

import ninja.Context;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import etc.GreetingService;

/**
 * This class demonstrates how easy it is to inject
 * any guice services into a controller.
 * 
 * In this case we are injecting a simple GreetingService that
 * can say hello...
 * 
 * @author ra
 *
 */
@Singleton
public class InjectionExampleController {

	private GreetingService greeter;

	@Inject
	public InjectionExampleController(GreetingService greeter) {
		this.greeter = greeter;

	}

	public void injection(Context context) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("greeting", greeter.hello());
		
		context.renderHtml(map);

	}

}
