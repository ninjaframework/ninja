package ninja;

public interface Router {

	public Route route(String uri);

	public Route getRouteFor(String uri);

}