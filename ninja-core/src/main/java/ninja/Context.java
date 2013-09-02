/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import ninja.bodyparser.BodyParserEngineJson;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.bodyparser.BodyParserEngineXml;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.ResponseStreams;

import ninja.validation.Validation;
import org.apache.commons.fileupload.FileItemIterator;

public interface Context {

    interface Impl extends Context {
        void setRoute(Route route);
    }

    /**
     * Content-Type: ... parameter for response.
     */
    public String CONTENT_TYPE = "Content-Type";

    /**
     * please use Result.SC_*
     * 
     * @author rbauer
     * 
     */
    @Deprecated
    enum HTTP_STATUS {
        notFound404(404), ok200(200), forbidden403(403), teapot418(418), badRequest400(
                400), noContent204(204), created201(201);
        public final int code;

        private HTTP_STATUS(int code) {
            this.code = code;
        }
    }

    /**
     * The Content-Type header field indicates the media type of the request
     * body sent to the recipient. E.g. {@code Content-Type: text/html;
     * charset=ISO-8859-4}
     *
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"
     *      >http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html</a>
     *
     * @return the content type of the incoming request.
     */
    String getRequestContentType();

    /**
     * Returns the uri as seen by the server.
     * 
     * http://example.com/index would return "/index".
     * 
     * This is ambiguous.
     * 
     * Please use the new method getRequestPath. It will also take care of any
     * prefixes and contexts set by your servlet container
     * 
     * @return the uri as seen by the server
     */
    @Deprecated
    String getRequestUri();

    /**
     * Returns the path that Ninja should act upon.
     * 
     * For instance in servlets you could have soemthing like a context prefix.
     * /myContext/app
     * 
     * If your route only defines /app it will work as the requestpath will
     * return only "/app". A context path is not returned.
     * 
     * It does NOT decode any parts of the url.
     * 
     * Interesting reads: -
     * http://www.lunatech-research.com/archives/2009/02/03/
     * what-every-web-developer-must-know-about-url-encoding -
     * http://stackoverflow
     * .com/questions/966077/java-reading-undecoded-url-from-servlet
     * 
     * @return The the path as seen by the server. Does exclude any container
     *         set context prefixes. Not decoded.
     */
    String getRequestPath();

    /**
     * Returns the flash cookie. Flash cookies only live for one request. Good
     * uses are error messages to display. Almost everything else is bad use of
     * Flash Cookies.
     * 
     * A FlashCookie is usually not signed. Don't trust the content.
     * 
     * @return the flash cookie of that request.
     */
    FlashCookie getFlashCookie();

    /**
     * Returns the client side session. It is a cookie. Therefore you cannot
     * store a lot of information inside the cookie. This is by intention.
     * 
     * If you have the feeling that the session cookie is too small for what you
     * want to achieve thing again. Most likely your design is wrong.
     * 
     * @return the Session of that request / response cycle.
     */
    SessionCookie getSessionCookie();
    
    /**
     * Get cookie from context.
     * 
     * @param cookieName
     *            Name of the cookie to retrieve
     * @return the cookie with that name or null.
     */
    Cookie getCookie(String cookieName);
    
    /**
     * Checks whether the context contains a given cookie.
     *
     * @param cookieName
     *            Name of the cookie to check for
     * @return {@code true} if the context has a cookie with that name.
     */
    boolean hasCookie(String cookieName);

    /**
     * Get all cookies from the context.
     * 
     * @return the cookie with that name or null.
     */
    List<Cookie> getCookies();
    
    /**
     * Get the context path on which the application is running
     * @return the context-path with a leading "/" or "" if running on root
     */
    String getContextPath();

    /**
     * Get the parameter with the given key from the request. The parameter may
     * either be a query parameter, or in the case of form submissions, may be a
     * form parameter.
     * <p>
     * When the parameter is multivalued, returns the first value.
     * <p>
     * The parameter is decoded by default.
     * 
     * @param name
     *            The key of the parameter
     * @return The value, or null if no parameter was found.
     * @see #getParameterValues
     */
    String getParameter(String name);

    /**
     * Get the parameter with the given key from the request. The parameter may
     * either be a query parameter, or in the case of form submissions, may be a
     * form parameter.
     * <p>
     * The parameter is decoded by default.
     *
     * @param name
     *            The key of the parameter
     * @return The values, possibly an empty list.
     */
    List<String> getParameterValues(String name);

    /**
     * Same like {@link #getParameter(String)}, but returns given defaultValue
     * instead of null in case parameter cannot be found.
     * 
     * The parameter is decoded by default.
     * 
     * @param name
     *            The name of the post or query parameter
     * @param defaultValue
     *            A default value if parameter not found.
     * @return The value of the parameter of the defaultValue if not found.
     */
    String getParameter(String name, String defaultValue);

    /**
     * Same like {@link #getParameter(String)}, but converts the parameter to
     * Integer if found.
     * 
     * The parameter is decoded by default.
     * 
     * @param name
     *            The name of the post or query parameter
     * @return The value of the parameter or null if not found.
     */
    Integer getParameterAsInteger(String name);

    /**
     * Same like {@link #getParameter(String, String)}, but converts the
     * parameter to Integer if found.
     * 
     * The parameter is decoded by default.
     * 
     * @param name
     *            The name of the post or query parameter
     * @param defaultValue
     *            A default value if parameter not found.
     * @return The value of the parameter of the defaultValue if not found.
     */
    Integer getParameterAsInteger(String name, Integer defaultValue);

    /**
     * Get the path parameter for the given key.
     * 
     * The parameter will be decoded based on the RFCs.
     * 
     * Check out http://docs.oracle.com/javase/6/docs/api/java/net/URI.html for
     * more information.
     * 
     * @param name
     *            The name of the path parameter in a route. Eg
     *            /{myName}/rest/of/url
     * @return The decoded path parameter, or null if no such path parameter was
     *         found.
     */
    String getPathParameter(String name);

    /**
     * Get the path parameter for the given key.
     * 
     * Returns the raw path part. That means you can get stuff like:
     * blue%2Fred%3Fand+green
     * 
     * @param name
     *            The name of the path parameter in a route. Eg
     *            /{myName}/rest/of/url
     * @return The encoded (!) path parameter, or null if no such path parameter
     *         was found.
     */
    String getPathParameterEncoded(String name);

    /**
     * Get the path parameter for the given key and convert it to Integer.
     * 
     * The parameter will be decoded based on the RFCs.
     * 
     * Check out http://docs.oracle.com/javase/6/docs/api/java/net/URI.html for
     * more information.
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
     * Get the (first) request header with the given name
     * 
     * @return The header value
     */
    String getHeader(String name);

    /**
     * Get all the request headers with the given name.
     *
     * @return the header values
     */
    List<String> getHeaders(String name);

    /**
     * Get all the headers from the request
     * 
     * @return The headers
     */
    Map<String, List<String>> getHeaders();

    /**
     * Get the cookie value from the request, if defined
     * 
     * @param name
     *            The name of the cookie
     * @return The cookie value, or null if the cookie was not found
     */
    String getCookieValue(String name);

    // /////////////////////////////////////////////////////////////////////////
    // Allows to get the nicely parsed content of the request.
    // For instance if the content is a json you could simply get the json
    // as Java object.
    // /////////////////////////////////////////////////////////////////////////
    /**
     * This will give you the request body nicely parsed. You can register your
     * own parsers depending on the request type.
     * 
     * Have a look at {@link ninja.bodyparser.BodyParserEngine}
     * {@link BodyParserEngineJson} {@link BodyParserEngineXml} 
     * and {@link BodyParserEngineManager}
     * 
     * @param classOfT
     *            The class of the result.
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
     * Finalizing the headers copies all stuff into the headers. It
     * of course also handles Ninja session and Flash information.
     * 
     * After finalizing the headers you can access the responseStreams.
     */
    ResponseStreams finalizeHeaders(Result result);
    
    /**
     * Finalizing the headers copies all stuff into the headers.
     * 
     * After finalizing the headers you can access the responseStreams.
     * 
     * This method does not set any Ninja session of flash information.
     * Eg. When serving static assets this is the method you may want to 
     * use. Otherwise you'd get a race condition with a lot of requests
     * setting scopes and deleting them immediately.
     */
    ResponseStreams finalizeHeadersWithoutFlashAndSessionCookie(Result result);

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
     * Check if request is of type multipart. Important when you want to process
     * uploads for instance.
     * 
     * Also check out: http://commons.apache.org/fileupload/streaming.html
     * 
     * @return true if request is of type multipart.
     */
    boolean isMultipart();

    /**
     * Gets the FileItemIterator of the input.
     * 
     * Can be used to process uploads in a streaming fashion. Check out:
     * http://commons.apache.org/fileupload/streaming.html
     * 
     * @return the FileItemIterator of the request or null if there was an
     *         error.
     */
    FileItemIterator getFileItemIterator();

    /**
     * Get the validation context
     * 
     * @return The validation context
     */
    Validation getValidation();

    /**
     * Get the content type that is acceptable for the client. (in this order :
     * {@see Result.TEXT_HTML} > {@see Result.APPLICATION_XML} > {@see
     * Result.APPLICATION_JSON} > {@see Result.TEXT_PLAIN} > {@see
     * Result.APPLICATION_OCTET_STREAM} level- or quality-parameter are ignored
     * with this method.) E.g. Accept: text/*;q=0.3, text/html;q=0.7,
     * text/html;level=1,text/html;level=2;q=0.4
     * 
     * The Accept request-header field can be used to specify certain media
     * types which are acceptable for the response. Accept headers can be used
     * to indicate that the request is specifically limited to a small set of
     * desired types, as in the case of a request for an in-line image.
     * 
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"
     *      >http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html</a>
     *
     * @return one of the {@see Result} mime types that is acceptable for the
     *         client or {@see Result.TEXT_HTML} if not set
     */
    String getAcceptContentType();

    /**
     * Get the encoding that is acceptable for the client. E.g. Accept-Encoding:
     * compress, gzip
     * 
     * The Accept-Encoding request-header field is similar to Accept, but
     * restricts the content-codings that are acceptable in the response.
     * 
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"
     *      >http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html</a>
     *
     * @return the encoding that is acceptable for the client
     */
    String getAcceptEncoding();

    /**
     * Get the language that is acceptable for the client. E.g. Accept-Language:
     * da, en-gb;q=0.8, en;q=0.7
     * 
     * The Accept-Language request-header field is similar to Accept, but
     * restricts the set of natural languages that are preferred as a response
     * to the request.
     * 
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"
     *      >http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html</a>
     *
     * @return the language that is acceptable for the client
     */
    String getAcceptLanguage();

    /**
     * Get the charset that is acceptable for the client. E.g. Accept-Charset:
     * iso-8859-5, unicode-1-1;q=0.8
     * 
     * The Accept-Charset request-header field can be used to indicate what
     * character sets are acceptable for the response. This field allows clients
     * capable of understanding more comprehensive or special- purpose character
     * sets to signal that capability to a server which is capable of
     * representing documents in those character sets.
     * 
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html"
     *      >http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html</a>
     *
     * @return the charset that is acceptable for the client
     */
    String getAcceptCharset();
    
   /**
    *
    * Returns the name of the HTTP method with which this 
    * request was made, for example, GET, POST, or PUT.
    * Same as the value of the CGI variable REQUEST_METHOD.
    *
    * @return a <code>String</code> 
    *        specifying the name
    *        of the method with which
    *        this request was made (eg GET, POST, PUT...)
    *
    */
    String getMethod();

    /**
     * Gets an attribute value previously set by {@link #setAttribute}.
     * <p>
     * Attributes are shared state for the duration of the request;
     * useful to pass values between {@link Filter filters} and
     * controllers.
     *
     * @return the attribute value, or {@code null} if the attribute does
     *         not exist
     */
    Object getAttribute(String name);

    /**
     * Gets an attribute value previously set by {@link #setAttribute}.
     * <p>
     * This is a convenience method, equivalent to:
     * <pre><code>
     *     return clazz.cast(getAttribute(name));
     * </code></pre>
     * <p>
     * Attributes are shared state for the duration of the request;
     * useful to pass values between {@link Filter filters} and
     * controllers.
     *
     * @return the attribute value, or {@code null} if the attribute does
     *         not exist
     */
    <T> T getAttribute(String name, Class<T> clazz);

    /**
     * Sets an attribute value.
     * <p>
     * Attributes are shared state for the duration of the request;
     * useful to pass values between {@link Filter filters} and
     * controllers.
     *
     * @see #getAttribute(String)
     * @see #getAttribute(String, Class)
     */
    void setAttribute(String name, Object value);
}