package ninja;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Context {
	
	public enum HTTP_STATUS {
		notFound_404
	}

	/**
	 * Default render method.
	 * 
	 * when execute by: /controller/ApplicationController.index(Context context)
	 * displays the following template: /views/ApplicationController/index
	 */
	void render(Tuple<String, String>... tuples);
	
	void render(HTTP_STATUS httpStatus);

	void setHttpServletRequest(HttpServletRequest httServletRequest);

	void setHttpServletResponse(HttpServletResponse httpServletResponse);

	HttpServletRequest getHttpServletRequest();

	HttpServletResponse getHttpServletResponse();

	void renderJson(Object object);
	
	String getPathParameter(String key);
	
	void redirect(String url);

}