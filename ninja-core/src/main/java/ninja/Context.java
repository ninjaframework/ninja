package ninja;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Context {
	
	public enum HTTP_STATUS {
		notFound_404, ok200
	}
	
	//final methods:
	void html(Tuple<String, String>... tuples);	
	void json(Object object);	
	void redirect(String url);
	
	
	Context template(String explicitTemplateName);

	void setHttpServletRequest(HttpServletRequest httServletRequest);

	void setHttpServletResponse(HttpServletResponse httpServletResponse);

	HttpServletRequest getHttpServletRequest();

	HttpServletResponse getHttpServletResponse();

	Context status(HTTP_STATUS httpStatus);
	
	String getPathParameter(String key);
	


}