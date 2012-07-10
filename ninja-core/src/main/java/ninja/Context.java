package ninja;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineJson;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;

public interface Context {

	enum HTTP_STATUS {
		notFound404, ok200, forbidden403, teapot418
	}

	/**
	 * Returns the uri as seen by the server.
	 * 
	 * http://example.com/index would return
	 * "/index".
	 * 
	 * @return the uri as seen by the server
	 */
    String getRequestUri();

    /**
     * Returns the flash cookie. Flash cookies only live for one request.
     * Good uses are error messages to display. Almost everything else
     * is bad use of Flash Cookies.
     * 
     * A FlashCookie is usually not signed. Don't trust the content.
     * 
     * @return the flash cookie of that request.
     */
    FlashCookie getFlashCookie();
	
    /**
     * Returns the client side session. It is a cookie. Therefore you
     * cannot store a lot of information inside the cookie. This is by intention.
     * 
     * If you have the feeling that the session cookie is too small for what you want
     * to achieve thing again. Most likely your design is wrong.
     * 
     * @return the Session of that request / response cycle.
     */
	SessionCookie getSessionCookie();

	/**
	 * Simply redirect to another url.
	 * 
	 * @param url
	 */
	void redirect(String url);

	/**
	 * Set the response content type.
	 * 
	 * The content type is usually also important because the rendering engine
	 * uses that contentType to determein which engine to chose.
	 * 
	 * call render() on application/json and the json render engine will be used
	 * call render() on text/html and the html render engine will be used.
	 * 
	 * @param contentType examples are "application/json" or "text/html".
	 */
	void setContentType(String contentType);

	/**
	 * Th explicitTemplateName is a fully qualified name of a template
	 * from the root of the package. It includes the suffix of
	 * a template (eg ftl.html).
	 * 
	 * An example is:
	 * "/views/forbidden403.ftl.html"
	 * or
	 * "/views/ApplicationController/index.ftl.html"
	 * 
	 * @param explicitTemplateName is something like "/views/ApplicationController/index.ftl.html"
	 * @return the very same Context for chaining.
	 */
	Context template(String explicitTemplateName);

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
	/**
	 * Renders something as text/html. Either uses a predefined template or
	 * the template determined by auto configuration.
	 */
	void renderHtml();
	
	/**
	 * Renders this object. Most likely the render engine will take a Map.
	 * The default rendering engine is Freemarker. And Fremarker
	 * will take a map of objects.
	 * 
	 * @param object The object to render as Html
	 */
	void renderHtml(Object object);

	/**
	 * Render the object as json. Uses the underlying Json implementation.
	 * By default we are using Gson. And Gson is accepting any Object and will
	 * transform it into a Json response.
	 * 
	 * @param object The object to render as Json
	 */
	void renderJson(Object object);

    /**
     * This will give you the request body nicely parsed. You can register your
     * own parsers depending on the request type.
     *
     * Have a look at {@link BodyParserEngine} {@link BodyParserEngineJson}
     * and {@link BodyParserEngineManager}
     *
     * @param classOfT The class of the result.
     * @return The parsed request or null if something went wrong.
     */
    <T> T parseBody(Class<T> classOfT);

    /**
     * Indicate that this request will be handled asynchronously
     */
    void handleAsync();

    /**
     * Indicate that request processing of an async request is complete
     */
    void requestComplete();

}