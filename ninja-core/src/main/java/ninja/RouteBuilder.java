package ninja;

public interface RouteBuilder {

	RouteBuilder route(String uri);

	void with(Class controller, String controllerMethod);
}