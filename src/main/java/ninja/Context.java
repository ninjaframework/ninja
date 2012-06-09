package ninja;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Context {

	/**
	 * Default render method.
	 * 
	 * when execute by: /controller/ApplicationController.index(Context context)
	 * displays the following template: /views/ApplicationController/index
	 */
	void render(Tuple<String, String>... tuples);

	void setHttpServletRequest(HttpServletRequest httServletRequest);

	void setHttpServletResponse(HttpServletResponse httpServletResponse);

	HttpServletRequest getHttpServletRequest();

	HttpServletResponse getHttpServletResponse();

	void renderJson(Object object);
	
	String getPathParameter(String key);

}