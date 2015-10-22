/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ninja.ContentTypes;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Route;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.uploads.FileItem;
import ninja.validation.Validation;
import ninja.validation.ValidationImpl;

import org.apache.commons.fileupload.FileItemIterator;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

/**
 * A fake context
 */
public class FakeContext implements Context {
    private String requestContentType;

    private String requestPath;
    
    private String contextPath;

    /** please use the requestPath stuff */
    @Deprecated
    private String requestUri;
    private String hostname;
    private String remoteAddr;
    private String scheme;
    private FlashScope flashScope;
    private Session session;
    private List<Cookie> addedCookies = new ArrayList<Cookie>();
    private Map<String, String> cookieValues = new HashMap<String, String>();
    private ListMultimap<String, String> params = ArrayListMultimap.create();
    private Map<String, String> pathParams = new HashMap<String, String>();
    private ListMultimap<String, String> headers = ArrayListMultimap.create();
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Object body;
    private Validation validation = new ValidationImpl();

    private String acceptContentType;

    private String acceptEncoding;

    private String acceptLanguage;

    private String acceptCharset;

    public FakeContext setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
        return this;
    }

    @Override
    public String getRequestContentType() {
        return requestContentType;
    }

    /**
     * Please use the getServletPath and setServletPath facilities.
     * 
     * @param requestUri
     * @return
     */
    @Deprecated
    public FakeContext setRequestUri(String requestUri) {
        this.requestUri = requestUri;
        return this;
    }


    /**
     * Please use the getServletPath and setServletPath facilities.
     * 
     * @param requestUri
     * @return
     */
    @Override
    @Deprecated
    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) { this.scheme = scheme;}

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public FakeContext setFlashCookie(FlashScope flashCookie) {
        this.flashScope = flashCookie;
        return this;
    }

    public FakeContext setSessionCookie(Session sessionCookie) {
        this.session = sessionCookie;
        return this;
    }

    @Override
    public Session getSessionCookie() {
        return session;
    }

    @Override
    public FlashScope getFlashCookie() {
        return flashScope;
    }
    
    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public FlashScope getFlashScope() {
        return flashScope;
    }
    
    public FakeContext addParameter(String key, String value) {
        params.put(key, value);
        return this;
    }

    @Override
    public String getParameter(String key) {
        return Iterables.getFirst(params.get(key), null);
    }

    @Override
    public List<String> getParameterValues(String name) {
        return params.get(name);
    }

    @Override
    public String getParameter(String key, String defaultValue) {
        String value = getParameter(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    @Override
    public Integer getParameterAsInteger(String key) {
        String value = getParameter(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Integer getParameterAsInteger(String key, Integer defaultValue) {
        String value = getParameter(key);
        if (value == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public <T> T getParameterAs(String key, Class<T> clazz) {
        return getParameterAs(key, clazz, null);
    }

    @Override
    public <T> T getParameterAs(String key, Class<T> clazz, T defaultValue) {
        try {
            return SwissKnife.convert(key, clazz);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public FakeContext addPathParameter(String key, String value) {
        pathParams.put(key, value);
        return this;
    }

    @Override
    public String getPathParameter(String key) {
        return pathParams.get(key);
    }

    @Override
    public Integer getPathParameterAsInteger(String key) {
        String value = getParameter(key);
        if (value == null) {
            return null;
        } else {
            return Integer.parseInt(value);
        }
    }

    @Override
    public Map<String, String[]> getParameters() {
        return Maps.transformValues(params.asMap(), new Function<Collection<String>, String[]>() {
            @Override
            public String[] apply(Collection<String> s) {
                return s.toArray(new String[s.size()]);
            }
        });
    }

    public FakeContext addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public String getHeader(String name) {
        return Iterables.getFirst(headers.get(name), null);
    }

    @Override
    public List<String> getHeaders(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        @SuppressWarnings("rawtypes")
        Map rawHeaders = headers.asMap();
        return rawHeaders;
    }

    public FakeContext addCookieValue(String name, String value) {
        cookieValues.put(name, value);
        return this;
    }

    @Override
    public String getCookieValue(String name) {
        return cookieValues.get(name);
    }

    public FakeContext setBody(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public <T> T parseBody(Class<T> classOfT) {
        return classOfT.cast(body);
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public FileItemIterator getFileItemIterator() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public boolean isAsync() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }
    
    @Override
    public void handleAsync() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public void returnResultAsync(Result result) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public void asyncRequestComplete() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public Result controllerReturned() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }
    
    @Override
    public ResponseStreams finalizeHeadersWithoutFlashAndSessionCookie(Result result) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public Route getRoute() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

	public FakeContext setRequestPath(String path) {
		this.requestPath = path;
		return this;
	}

	@Override
	public String getRequestPath() {
		return this.requestPath;
	}

    @Override
    public Validation getValidation() {
        return validation;
    }

    @Override
    public String getPathParameterEncoded(String name) {
        return this.getPathParameterEncoded(name);
    }

    public void setAcceptContentType(String acceptContentType) {
        this.acceptContentType = acceptContentType;
    }

    @Override
    public String getAcceptContentType() {
        return acceptContentType;
    }

    public void setAcceptEncoding(String acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
    }

    @Override
    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public void setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
    }

    @Override
    public String getAcceptLanguage() {
        return acceptLanguage;
    }

    public void setAcceptCharset(String acceptCharset) {
        this.acceptCharset = acceptCharset;
    }

    @Override
    public String getAcceptCharset() {
        return acceptCharset;
    }

    @Override
    public Cookie getCookie(String cookieName) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public boolean hasCookie(String cookieName) {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public List<Cookie> getCookies() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException("Not supported in fake context");
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(getAttribute(name));
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
	@Override
	public String getContextPath() {
		return contextPath;
	}
	
	public void setContextPath(String contextPath){
		this.contextPath = contextPath;
	}

    @Override
    public boolean isRequestJson() {
        String contentType = getRequestContentType();
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }

        return contentType.startsWith(ContentTypes.APPLICATION_JSON);
    }

    @Override
    public boolean isRequestXml() {
        String contentType = getRequestContentType();
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }

        return contentType.startsWith(ContentTypes.APPLICATION_XML);
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Not supported in fake context");        
    }

    @Override
    public void unsetCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Not supported in fake context");        
    }

    @Override
    public FileItem getParameterAsFileItem(String name) {
        throw new UnsupportedOperationException("Not supported in fake context");        
    }

    @Override
    public List<FileItem> getParameterAsFileItems(String name) {
        throw new UnsupportedOperationException("Not supported in fake context");        
    }

    @Override
    public Map<String, List<FileItem>> getParameterFileItems() {
        throw new UnsupportedOperationException("Not supported in fake context");        
    }

    @Override
    public void cleanup() {
    }
}