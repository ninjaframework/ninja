package ninja;

import java.util.Map;

public interface Router {

	public Route route(String uri);
	
	public Route getRouteFor(String uri);

	/**
	 * If route contains parameters like so:
	 * /{user}/dashboard
	 * parameterMap must contain user.
	 *
	 * @param uri
	 * @param parameterMap
	 * @return
	 */
	public Route getRouteFor(String uri, Map<String, String> parameterMap);

}