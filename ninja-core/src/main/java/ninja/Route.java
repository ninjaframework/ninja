package ninja;

import java.util.Map;

public interface Route {

	public Route GET();

	public Route POST();

	public Route PUT();

	public Route DELETE();

	public Route OPTION();
	
	public Route route(String uri);

	public void with(Class controller, String controllerMethod);

	public String getUrl();

	public void invoke(Context context);

	Class getController();

	String getControllerMethod();

	public boolean matches(String uri);

	Map<String, String> getParameters(String uri);

}