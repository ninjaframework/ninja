package ninja;

import java.util.Map;

public interface Route {

	Route GET();

	Route POST();

	Route PUT();

	Route DELETE();

	Route OPTION();

	Route route(String uri);

	void with(Class controller, String controllerMethod);

	String getUrl();
  
	void invoke(Context context);

	Class getController();

	String getControllerMethod();

	boolean matches(String uri);

	Map<String, String> getParameters(String uri);

}