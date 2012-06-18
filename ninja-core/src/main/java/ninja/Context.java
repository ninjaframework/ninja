package ninja;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Context {

	enum HTTP_STATUS {
		notFound404, ok200, forbidden403, teapot418
	}

	// final methods:
	void html();
	
	void html(Map<String, String> map);

	void json(Object object);

	void redirect(String url);

	void setContentType(String contentType);

	Context template(String explicitTemplateName);

	void setHttpServletRequest(HttpServletRequest httServletRequest);

	void setHttpServletResponse(HttpServletResponse httpServletResponse);

	HttpServletRequest getHttpServletRequest();

	HttpServletResponse getHttpServletResponse();

	Context status(HTTP_STATUS httpStatus);

	String getPathParameter(String key);

	String getTemplateName();

}