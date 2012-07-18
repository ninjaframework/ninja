package ninja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.bodyparser.BodyParserEngineJson;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.ResponseStreams;

import org.apache.commons.fileupload.FileItemIterator;

public interface Context {

	enum HTTP_STATUS {
        notFound404(404), ok200(200), forbidden403(403), teapot418(418), badRequest400(400),
        noContent204(204), created201(201);
        public final int code;

        private HTTP_STATUS(int code) {
            this.code = code;
        }
    }
	
	/**
	 * Content type of the request we got.
	 * Important for content negotiation...
	 * 
	 * @return the content type of the incoming request.
	 * 
	 */
	String getRequestContentType();

	/**
	 * Returns the uri as seen by the server.
	 * 
	 * http://example.com/index would return
	 * "/index".
	 * 
	 * This is ambiguous.
	 * 
	 * Please use the new method getRequestPath. It will also take care
	 * of any prefixes and contexts set by your servlet container
	 * 
	 * @return the uri as seen by the server
	 */
	@Deprecated
    String getRequestUri();
	
	/**
	 * Returns the path as seen by the server.
	 * 
	 * http://example.com/index would return
	 * "/index".
	 * 
	 * It will also take care
	 * of any prefixes and contexts set by your servlet container
	 * 
	 * @return the path as seen by the server
	 */
    String getRequestPath();

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
     * Add the given cookie to the response
     *
     * @param cookie The cookie to add
     * @return This context
     */
    public Context addCookie(Cookie cookie);

    /**
     * Get the underlying HTTP servlet request
     * 
     * @Deprecated because it directly refers to the servlet api.
     * And usually we don't want that.
     * 
     * If you are missing something you cannot find in context please
     * suggest it. getHttpServletResponse will be removed at some point.
     *
     * @return The HTTP servlet request
     */
	@Deprecated
	HttpServletRequest getHttpServletRequest();

    /**
     * Get the underlying HTTP servlet response
     * 
     * @Deprecated because it directly refers to the servlet api.
     * And usually we don't want that.
     * 
     * If you are missing something you cannot find in context please
     * suggest it. getHttpServletResponse will be removed at some point.
     *
     * @return The HTTP servlet response
     *
     */
	@Deprecated
	HttpServletResponse getHttpServletResponse();

    /**
     * Get the parameter with the given key from the request.  The parameter may either be a query parameter, or in the
     * case of form submissions, may be a form parameter
     *
     * @param key The key of the parameter
     * @return The value, or null if no parameter was found
     */
    String getParameter(String key);

	/**
	 * Same like {@link #getParameter(String)}, but returns given defaultValue
	 * instead of null in case parameter cannot be found
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	String getParameter(String key, String defaultValue);
	
	/**
	 * Same like {@link #getParameter(String)}, but converts the
	 * parameter to Integer if found.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	Integer getParameterAsInteger(String key);
	
	/**
	 * Same like {@link #getParameter(String, String)}, but converts the
	 * parameter to Integer if found.
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	Integer getParameterAsInteger(String key, Integer defaultValue);
	
    /**
     * Get the path parameter for the given key
     *
     * @param key The key of the path parameter
     * @return The path parameter, or null if no such path parameter is defined
     */
	String getPathParameter(String key);

	/**
	 * Get the path parameter for the given key and convert it to Integer.
	 * 
	 * @param key
	 *            the key of the path parameter
	 * @return the numeric path parameter, or null of no such path parameter is
	 *         defined, or if it cannot be parsed to int
	 */
	Integer getPathParameterAsInteger(String key);

    /**
     * Get all the parameters from the request
     *
     * @return The parameters
     */
    Map<String, String[]> getParameters();

    /**
     * Get the request header with the given name
     *
     * @return The header value
     */
    String getHeader(String name);

    /**
     * Get all the headers from the request
     *
     * @return The headers
     */
    Map<String, String> getHeaders();

    /**
     * Get the cookie value from the request, if defined
     *
     * @param name The name of the cookie
     * @return The cookie value, or null if the cookie was not found
     */
    String getCookieValue(String name);

	///////////////////////////////////////////////////////////////////////////
	// Allows to get the nicely parsed content of the request.
	// For instance if the content is a json you could simply get the json
	// as Java object.
	///////////////////////////////////////////////////////////////////////////
	/**
	 * This will give you the request body nicely parsed. You can register your
	 * own parsers depending on the request type.
	 * 
	 * Have a look at {@link ninja.bodyparser.BodyParserEngine} {@link BodyParserEngineJson}
	 * and {@link BodyParserEngineManager}
	 * 
	 * @param classOfT The class of the result.
	 * @return The parsed request or null if something went wrong.
	 */
	<T> T parseBody(Class<T> classOfT);

    /**
     * Indicate that this request is going to be handled asynchronously
     */
    void handleAsync();

    /**
     * Indicate that request processing of an async request is complete.
     */
    void returnResultAsync(Result result);

    /**
     * Indicate that processing this request is complete.
     */
    void asyncRequestComplete();

    Result controllerReturned();
    
    /**
     * Finalizing the headers copies all stuff into the headers. 
     * 
     * After finalizing the headers you can access the responseStreams.
     * 
     * @param result
     * @return
     */
    ResponseStreams finalizeHeaders(Result result);

    /**
     * Get the input stream to read the request.
     *
     * Must not be used if getReader has been called.
     *
     * @return The input stream
     */
    InputStream getInputStream() throws IOException;

    /**
     * Get the reader to read the request.
     *
     * Must not be used if getInputStream has been called.
     *
     * @return The reader
     */
    BufferedReader getReader() throws IOException;

    /**
     * Get the route for this context
     *
     * @return The route
     */
    Route getRoute();
    
    /**
     * Check if request is of type multipart.
     * Important when you want to process uploads for instance.
     * 
     * Also check out: http://commons.apache.org/fileupload/streaming.html
     * 
     * @return true if request is of type multipart.
     */
    boolean isMultipart();
    
    /**
     * Gets the FileItemIterator of the input.
     * 
     * Can be used to process uploads in a streaming fashion.
     * Check out: http://commons.apache.org/fileupload/streaming.html
     * 
     * @return the FileItemIterator of the request or null if there was an error.
     */
    FileItemIterator getFileItemIterator();

    /**
     * Get the validation context
     *
     * @return The validation context
     */
    Validation getValidation();
}