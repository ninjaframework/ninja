package ninja;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Context {

	enum HTTP_STATUS {
		notFound404, ok200, forbidden403, teapot418
	}



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

	///////////////////////////////////////////////////////////////////////////
	// Generic methods to finish a request (rendering any content)
	// either the content has been set manually using setContentType or
	// the result is generated using content negotiation
	///////////////////////////////////////////////////////////////////////////
	void render();
	
	void render(Object object);	
	
	///////////////////////////////////////////////////////////////////////////
	// Convenience Methods to render a specific type. Html, Json and maybe Xml
	// Uses no content negotation whatsoever
	///////////////////////////////////////////////////////////////////////////
	void renderHtml();
	
	void renderHtml(Object object);

	void renderJson(Object object);
	
	///////////////////////////////////////////////////////////////////////////
	// Allows to get the nicely parsed content of the request.
	// For instance if the content is a json you could simply get the json
	// as Java object.
	///////////////////////////////////////////////////////////////////////////
	<T> T parseBody(Class<T> classOfT);	

}