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

import java.io.OutputStream;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.utils.DateUtil;
import ninja.utils.ResponseStreams;
import ninja.utils.SwissKnife;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Result {
    
    private final Logger logger = LoggerFactory.getLogger(Result.class);

    // /////////////////////////////////////////////////////////////////////////
    // HTTP Status codes (for convenience)
    // /////////////////////////////////////////////////////////////////////////
    public static int SC_200_OK = 200;
    public static int SC_204_NO_CONTENT = 204;

    // for redirects:
    public static int SC_300_MULTIPLE_CHOICES = 300;
    public static int SC_301_MOVED_PERMANENTLY = 301;
    public static int SC_302_FOUND = 302;
    public static int SC_303_SEE_OTHER = 303;
    public static int SC_304_NOT_MODIFIED = 304;
    public static int SC_307_TEMPORARY_REDIRECT = 307;

    public static int SC_400_BAD_REQUEST = 400;
    public static int SC_403_FORBIDDEN = 403;
    public static int SC_404_NOT_FOUND = 404;

    public static int SC_500_INTERNAL_SERVER_ERROR = 500;
    public static int SC_501_NOT_IMPLEMENTED = 501;

    // /////////////////////////////////////////////////////////////////////////
    // Some MIME types (for convenience)
    // /////////////////////////////////////////////////////////////////////////
    public static final String TEXT_HTML = "text/html";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String APPLICATON_JSON = "application/json";
    public static final String APPLICATON_JSONP = "application/javascript";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    
    // /////////////////////////////////////////////////////////////////////////
    // Finally we got to the core of this class...
    // /////////////////////////////////////////////////////////////////////////
    /* Used as redirection header */
    public static final String LOCATION = "Location";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CACHE_CONTROL_DEFAULT_NOCACHE_VALUE = "no-cache, no-store, max-age=0, must-revalidate";
    
    public static final String DATE = "Date";
    public static final String EXPIRES = "Expires";

    private int statusCode;

    /* The object that will be rendered. Could be a Java Pojo. Or a map. Or xyz. Will be
     * handled by the TemplateReneringEngine. */
    private Object renderable;

    /**
     * Something like: "text/html" or "application/json"
     */
    private String contentType;

    /**
     * Something like: "utf-8" => will be appended to the content-type. eg
     * "text/html; charset=utf-8"
     */
    private String charset;

    private Map<String, String> headers;

    private List<Cookie> cookies;

    private String template;

    /**
     * A result. Sets utf-8 as charset and status code by default. 
     * Refer to {@link Result#SC_200_OK}, {@link Result#SC_204_NO_CONTENT} and so on
     * for some short cuts to predefined results. 
     * 
     * @param statusCode The status code to set for the result. Shortcuts to the code at: {@link Result#SC_200_OK}
     */
    public Result(int statusCode) {

        this.statusCode = statusCode;
        this.charset = "utf-8";

        this.headers = Maps.newHashMap();
        this.cookies = Lists.newArrayList();

    }

    public Object getRenderable() {
        return renderable;
    }

    /**
     * This method handles two principal cases:
     * 1) If the this.renderable of this result is null, the object passed is simply set as renderable 
     *    for this Result
     * 2) If the this.renderable of this result is not null an new map is generated as
     *    object to render and both the former renderable and the new object added to the map. 
     *    The former object is gets the class name in camelCase as key.
     *    
     * If the converted camelCase key of this object already exists an {@link IllegalArgumentException}
     * is being thrown.
     * 
     * @param Object The object to add (either an arbitrary class or Renderable).
     * @return Result this result for chaining.
     * 
     */
    public Result render(Object object) {
        
        if (this.renderable == null) {
            
            this.renderable = object;
            
        } else {
            
            assertObjectNoRenderableOrThrowException(this.renderable);            
            
            Map<String, Object> map;
            
            // prepare the this.renderable as we now have at least two
            // objects to render
            if (this.renderable instanceof Map) {
               map = (Map) this.renderable;
                
            } else {
                map = Maps.newHashMap();                
                // add original this.renderable
                map.put(SwissKnife.getRealClassNameLowerCamelCase(this.renderable), this.renderable);
                this.renderable = map;
                
            }
            
            // add object of this method
            String key = SwissKnife.getRealClassNameLowerCamelCase(object);
            if (map.containsKey(key)) {
                throw new IllegalArgumentException(
                        String.format(
                              "Cannot store object with default name %s."
                              + "An object with the same name is already stored."
                              + "Consider using render(key, value) to name objects implicitly.",
                              key));
                
            } else {
                map.put(SwissKnife.getRealClassNameLowerCamelCase(object), object);
            }
            
            
        }
        
        
        return this;
    }
    
    /**
     * Replaces the object being passed by this result to the rendering engine
     * with this map. It will overwrite any previously set render(...) calls.
     * 
     * @param mapToRender The map being passed to the templating engine.
     * @return This Result for chaining.
     */
    public Result render(Map<String, Object> mapToRender) {
        this.renderable = mapToRender;        
        return this;        
    }
    
    /**
     * Handles two cases:
     * 1) If this.renderable is null a new HashMap is generated and this entry being added
     *    to the map.
     * 2) If this.renderable is a Map the entry is added
     * 3) If this.renderable is an object (not a renderable) a Map is generated and both
     *    the former object and the new entry are being added.
     * 3) If this.renderable is a Renderable an {@link IllegalArgumentException} is thrown.
     * 
     * If the entry key already exists in the map of this.renderable an {@link IllegalArgumentException}
     * is thrown.
     * 
     * @param entry The entry to add.
     * @return The result for further chaining.
     */
    public Result render(Entry<String, Object> entry) {
        
        if (this.renderable == null) {
        
            Map<String, Object> map = Maps.newHashMap();
            this.renderable = map;
            map.put(entry.getKey(), entry.getValue());
        } else {
            
            assertObjectNoRenderableOrThrowException(this.renderable);
            
            Map<String, Object> map;
            if (this.renderable instanceof Map) {
                
                map = (Map) this.renderable;
                if (map.containsKey(entry.getKey())) {
                    throw new IllegalArgumentException(
                           String.format(
                                 "Entry with key %s already stored inside this Result object. "
                                 + "This is currently not supported and does not make sense. "
                                 +  "Consider using your own map.",
                                         entry.getKey()));
                } else { 
                    map.put(entry.getKey(), entry.getValue());
                
                }
                
            } else {                
                map = Maps.newHashMap();
                map.put(
                        SwissKnife.getRealClassNameLowerCamelCase(this.renderable), 
                        this.renderable);
                
                this.renderable = map;
                              
            }
            
            map.put(entry.getKey(), entry.getValue());
            
        }
        
        
        return this;
        
        
    }
    
    /**
     * Sets this renderable as object to render. Usually this renderable
     * does rendering itself and will not call any templating engine. 
     * 
     * @param renderable The renderable that will handle everything after returing the result.
     * @return This result for chaining.
     */
    public Result render(Renderable renderable) {
        this.renderable = renderable;        
        return this;        
    }
    
    /**
     * Implicitly generates a hashmap as object being rendered and adds
     * this key, value pair. If the object being rendered is already a hashmap
     * it simply adds this key value pair to it.
     * 
     * @param key The key to use.
     * @param value The value to use.
     * @return The Result for chaining.
     */
    public Result render(String key, Object value) {
        
        render(new AbstractMap.SimpleEntry<String, Object>(key, value));
            
        return this;
        
    }
    

    /**
     * This method directly renders the String to the output. It
     * completely bypasses any rendering engine.
     * 
     * Thus you can render anything you want.
     * 
     * Chaining of resultRaw is NOT supported. Mixing with render()
     * is NOT supported.
     * 
     * It is always recommended to implement your own RenderingEngine OR
     * use existing rendering engines.
     * 
     * Example:
     * <code>
     * public Result controllerMethod() {
     *    String customJson = "{\"user\" : \"john@woo.com\"}";
     * 
     *    return Results.json().renderRaw(customJson);
     * }
     * </code>
     * 
     * @param string The string to render.
     * @return A result that will render the string directly to the output stream.
     */
    public Result renderRaw(final String string) {
 
        Renderable renderable = new Renderable() {
 
            @Override
            public void render(Context context, Result result) throws Exception {
 
                ResponseStreams resultJsonCustom = context
                        .finalizeHeaders(result);
                
                try (Writer writer = resultJsonCustom.getWriter()) {
                
                    writer.write(string);
                    
                } catch (IOException ioException) {
                
                    logger.error(
                            "Error rendering raw String via renderRaw(...)", 
                            ioException);
                }
 
            }
        };
 
        render(renderable);
 
        return this;
        
    }

    public String getContentType() {
        return contentType;
    }

    /**
     * @return Charset of the current result that will be used. Will be "utf-8"
     *         by default.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @return Set the charset of the result. Is "utf-8" by default.
     */
    public void charset(String charset) {
        this.charset = charset;
    }

    /**
     * Sets the content type
     * 
     * @param contentType
     * @Deprecated => please use shortcut contentType(...)
     */
    @Deprecated
    public Result setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Sets the content type. Must not contain any charset WRONG:
     * "text/html; charset=utf8".
     * 
     * If you want to set the charset use method {@link Result#charset(String)};
     * 
     * @param contentType
     *            (without encoding) something like "text/html" or
     *            "application/json"
     */
    public Result contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Result addHeader(String headerName, String headerContent) {

        headers.put(headerName, headerContent);
        return this;
    }
    
    /**
     * Returns cookie with that name or null.
     * 
     * @param cookieName Name of the cookie
     * @return The cookie or null if not found.
     */
    public Cookie getCookie(String cookieName) {
        
        for (Cookie cookie : getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        
        return null;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public Result addCookie(Cookie cookie) {
        cookies.add(cookie);
        return this;
    }

    public Result unsetCookie(String name) {
        cookies.add(Cookie.builder(name, "").setMaxAge(0).build());
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Set the status of this result.
     * Refer to {@link Result#SC_200_OK}, {@link Result#SC_204_NO_CONTENT} and so on
     * for some short cuts to predefined results. 
     * 
     * @param statusCode The status code. Result ({@link Result#SC_200_OK}) provides some helpers.
     * @return The result you executed the method on for method chaining.
     */
    public Result status(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public String getTemplate() {
        return template;
    }

    /**
     * Set the template to render. For instance 
     * template("views/AnotherController/anotherview.ftl.html");
     * 
     * @param template The view to render. Eg. views/AnotherController/anotherview.ftl.html
     * @return The result that you executed the method on for chaining.
     */
    public Result template(String template) {
        this.template = template;
        return this;
    }

    /**
     * A redirect that uses 303 see other.
     * 
     * @param url
     *            The url used as redirect target.
     * @return A nicely configured result with status code 303 and the url set
     *         as Location header.
     */
    public Result redirect(String url) {
        
        status(Result.SC_303_SEE_OTHER);
        addHeader(Result.LOCATION, url);
        
        return this;
    }

    /**
     * A redirect that uses 307 see other.
     * 
     * @param url
     *            The url used as redirect target.
     * @return A nicely configured result with status code 307 and the url set
     *         as Location header.
     */
    public Result redirectTemporary(String url) {

        status(Result.SC_307_TEMPORARY_REDIRECT);
        addHeader(Result.LOCATION, url);

        return this;
    }
    /**
     * Set the content type of this result to {@link Result#TEXT_HTML}.
     * 
     * @return the same result where you executed this method on. But the content type is now {@link Result#TEXT_HTML}.
     */
    public Result html() {
        contentType = TEXT_HTML;
        return this;
    }

    /**
     * Set the content type of this result to {@link Result#APPLICATON_JSON}.
     * 
     * @return the same result where you executed this method on. But the content type is now {@link Result#APPLICATON_JSON}.
     */
    public Result json() {
        contentType = APPLICATON_JSON;
        return this;
    }

    /**
     * Set the content type of this result to {@link Result#APPLICATON_JSONP}.
     *
     * @return the same result where you executed this method on. But the content type is now {@link Result#APPLICATON_JSONP}.
     */
    public Result jsonp() {
        contentType = APPLICATON_JSONP;
        return this;
    }
    
    /**
     * Set the content type of this result to {@link Result#APPLICATON_XML}.
     * 
     * @return the same result where you executed this method on. But the content type is now {@link Result#APPLICATON_XML}.
     */
    public Result xml() {
        contentType = APPLICATION_XML;
        return this;
    }
    
    /**
     * This function sets
     * 
     * Cache-Control: no-cache, no-store
     * Date: (current date)
     * Expires: 1970
     * 
     * => it therefore effectively forces the browser and every proxy in between
     * not to cache content.
     * 
     * See also https://devcenter.heroku.com/articles/increasing-application-performance-with-http-cache-headers
     * 
     * @return this result for chaining.
     */
    public Result doNotCacheContent() {
        
        addHeader(CACHE_CONTROL, CACHE_CONTROL_DEFAULT_NOCACHE_VALUE);
        addHeader(DATE, DateUtil.formatForHttpHeader(System.currentTimeMillis()));
        addHeader(EXPIRES, DateUtil.formatForHttpHeader(0L));
        
        return this;
        
    } 
    
    
    private void assertObjectNoRenderableOrThrowException(Object object) {
        if (object instanceof Renderable) {
            throw new IllegalArgumentException(
                    "You already want to render a Renderable class. " +
                "Adding more items to render is not supported.");
            
        }
    }
    
  

}
