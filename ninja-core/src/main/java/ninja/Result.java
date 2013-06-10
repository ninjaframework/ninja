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

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.utils.DateUtil;
import ninja.utils.SwissKnife;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Result {

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
     * This method can be chained and called multiple times.
     * 
     * Please make sure you respect the following:
     * 1. If your object implements the {@link Renderable} interface you can only add one object.
     * 2. If you add an object for the first time it will only add this object in a plain way
     *    => The templating engine will take care.
     * 3. If you add more than one object a map will be generated and the templating engine
     *    will get a map<String, Object>. The key names of the map are lower camel case names
     *    of the classnames.
     *    
     * IMPORTANT! If you add more than one object of the same type via render() the object you
     * will get a {@link IllegalArgumentException}. Don't do this. If you want to add more than
     * object of the same type use {@link Entry} or add them as {@link Map}.
     * 
     * @param object The object to render / or add to the map being rendered
     * @return this result for chaining method calls.
     */
    public Result render(Object object) {
         
        // if renderable is empty we just add it.
        // But if it is an Entry of a map we skip that and generate a map straight away...
        if (this.renderable == null
                && !(object instanceof Entry)) {
            
            this.renderable = object; 
            
        } else {   
            
            Map<String, Object> renderableMap;
            
            if (this.renderable instanceof Renderable) {
                /**
                 * Objects implementing interface Renderable can do a lot of funny stuff.
                 * But they 100% don't want to be used together with other
                 * objects. We break here.
                 */
                throw new IllegalArgumentException(
                        "Renderable interfaces cannot be mixed with other objects " +
                        "when calling render().");
            
            } else if (this.renderable instanceof Map) {
                // there is already a map => we simply add the renderable
                renderableMap = (Map) this.renderable;
                
            } else {
                // only one object => we 
                renderableMap = new HashMap<String, Object>();
                // put in former single object:
                if (this.renderable != null) {
                    renderableMap.put(
                            SwissKnife.getRealClassNameLowerCamelCase(this.renderable),
                            this.renderable);                    
                }
 
                this.renderable = renderableMap;
                
                        
            }
            
            // check the input element:
            // first case => an entry
            if (object instanceof Map.Entry<?, ?>) {
                Map.Entry<String, Object> objectEntry = (Map.Entry) object;
                renderableMap.put(objectEntry.getKey(), objectEntry.getValue());   
            // second case => a map => we add it
            } else if (object instanceof Map<?, ?>) {
                Map<String, Object> map = (Map) object;
                
                for (Entry<String, Object> entry : map.entrySet()) {
                    renderableMap.put(entry.getKey(), entry.getValue());
                }
                
            } else {
                //third case an arbitrary object => we transform the name and add it.
                String name = SwissKnife.getRealClassNameLowerCamelCase(object);
                
                if (renderableMap.containsKey(name)) {
                    throw new IllegalArgumentException(
                        "Cannot add objects with same names and no specifier. " +
                    		"Try using Entry<String, Object> and add render that to specify exact name");
                }
                
                renderableMap.put(name, object);
                
            }
             

            
        }
        
        
        
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
        cookies.add(Cookie.builder(name, null).setMaxAge(0).build());
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

}
