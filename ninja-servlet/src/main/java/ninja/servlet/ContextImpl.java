/**
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ninja.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.ContentTypes;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.servlet.async.AsyncStrategy;
import ninja.servlet.async.AsyncStrategyFactoryHolder;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.uploads.FileItem;
import ninja.uploads.FileItemProvider;
import ninja.uploads.FileProvider;
import ninja.uploads.NoFileItemProvider;
import ninja.utils.HttpHeaderUtils;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResponseStreams;
import ninja.utils.ResultHandler;
import ninja.utils.SwissKnife;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ContextImpl implements Context.Impl {

    private ServletContext servletContext;

    private HttpServletRequest httpServletRequest;

    private HttpServletResponse httpServletResponse;

    private Route route;

    private AsyncStrategy asyncStrategy;
    private final Object asyncLock = new Object();

    private final BodyParserEngineManager bodyParserEngineManager;

    private final FlashScope flashScope;
    
    private final NinjaProperties ninjaProperties;

    private final Session session;
    private final ResultHandler resultHandler;
    private final Validation validation;
    private final Injector injector;
    
    private boolean formFieldsProcessed = false;
    private Map<String, List<String>> formFieldsMap;
    private Map<String, List<FileItem>> fileFieldsMap;

    // In Async mode, these values will be set to null, so save them
    private String requestPath;
    private String contextPath;

    private Logger logger = LoggerFactory.getLogger(ContextImpl.class);

    @Inject
    public ContextImpl (
            BodyParserEngineManager bodyParserEngineManager,
            FlashScope flashCookie,
            NinjaProperties ninjaProperties,
            ResultHandler resultHandler,
            Session sessionCookie,
            Validation validation,
            Injector injector) {

        this.bodyParserEngineManager = bodyParserEngineManager;
        this.flashScope = flashCookie;
        this.ninjaProperties = ninjaProperties;
        this.session = sessionCookie;
        this.resultHandler = resultHandler;
        this.validation = validation;
        this.injector = injector;
    }

    public void init(ServletContext servletContext,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        this.servletContext = servletContext;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;

        enforceCorrectEncodingOfRequest();

        // init flash scope:
        flashScope.init(this);

        // init session scope:
        session.init(this);
        
        contextPath = httpServletRequest.getContextPath();
        requestPath = performGetRequestPath();
    }

    @Override
    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public String getPathParameter(String key) {
        String encodedParameter = route.getPathParametersEncoded(
                getRequestPath()).get(key);

        if (encodedParameter == null) {
            return null;
        } else {
            return URI.create(encodedParameter).getPath();
        }

    }

    @Override
    public String getPathParameterEncoded(String key) {
        return route.getPathParametersEncoded(getRequestPath()).get(key);
    }

    @Override
    public Integer getPathParameterAsInteger(String key) {
        String parameter = getPathParameter(key);

        try {
            return Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getParameter(String key) {
        if (!formFieldsProcessed) processFormFields();
        if (formFieldsMap == null) {
            return httpServletRequest.getParameter(key);
        } else {
            List<String> values = formFieldsMap.get(key);
            if (values == null || values.isEmpty())
                return null;
            return values.get(0);
        }
    }

    @Override
    public List<String> getParameterValues(String name) {
        if (!formFieldsProcessed) processFormFields();
        if (formFieldsMap == null) {
            String[] params = httpServletRequest.getParameterValues(name);
            if (params == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(params);
        } else {
            return formFieldsMap.get(name);
        }
    }

    @Override
    public String getParameter(String key, String defaultValue) {
        String parameter = getParameter(key);

        if (parameter == null) {
            parameter = defaultValue;
        }

        return parameter;
    }

    @Override
    public Integer getParameterAsInteger(String key) {
        return getParameterAsInteger(key, null);
    }

    @Override
    public Integer getParameterAsInteger(String key, Integer defaultValue) {
        String parameter = getParameter(key);

        try {
            return Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    @Override
    public FileItem getParameterAsFileItem(String key) {
        if (!formFieldsProcessed) processFormFields();
        if (fileFieldsMap == null) return null;
        List<FileItem> fileItems = fileFieldsMap.get(key);
        if (fileItems == null || fileItems.isEmpty()) return null;
        return fileItems.get(0);
    }
    
    @Override
    public List<FileItem> getParameterAsFileItems(String key) {
        if (!formFieldsProcessed) processFormFields();
        if (fileFieldsMap == null) return Collections.emptyList();
        List<FileItem> fileItems = fileFieldsMap.get(key);
        if (fileItems == null) return Collections.emptyList();
        return fileItems;
    }

    @Override
    public Map<String, List<FileItem>> getParameterFileItems() {
        if (!formFieldsProcessed) processFormFields();
        return fileFieldsMap;
    }

    
    @Override
    public <T> T getParameterAs(String key, Class<T> clazz) {
        return getParameterAs(key, clazz, null);
    }

    @Override
    public <T> T getParameterAs(String key, Class<T> clazz, T defaultValue) {
        String parameter = getParameter(key);

        try {
            return SwissKnife.convert(parameter, clazz);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Map<String, String[]> getParameters() {
        if (!formFieldsProcessed) processFormFields();
        if (formFieldsMap == null) {
            return httpServletRequest.getParameterMap();
        } else {
            // convert List<String> value to String[] value
            String[] type = new String[0];
            Map<String, String[]> map = new HashMap<>(formFieldsMap.size());
            for (Entry<String, List<String>> entry: formFieldsMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue().toArray(type));
            }
            return map;
        }
    }

    @Override
    public String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return Collections.list(httpServletRequest.getHeaders(name));
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        Enumeration<String> names = httpServletRequest.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, Collections.list(httpServletRequest.getHeaders(name)));
        }
        return headers;
    }

    @Override
    public String getCookieValue(String name) {
        return CookieHelper.getCookieValue(name,
                httpServletRequest.getCookies());
    }

    @Override
    public <T> T parseBody(Class<T> classOfT) {

        String rawContentType = getRequestContentType();

        // If the Content-type: xxx header is not set we return null.
        // we cannot parse that request.
        if (rawContentType == null) {
            logger.debug("Not able to parse body because request did not send content type header at: {}", getRequestPath());
            return null;
        }

        // If Content-type is application/json; charset=utf-8 we split away the charset
        // application/json
        String contentTypeOnly = HttpHeaderUtils.getContentTypeFromContentTypeAndCharacterSetting(
                rawContentType);

        BodyParserEngine bodyParserEngine = bodyParserEngineManager
                .getBodyParserEngineForContentType(contentTypeOnly);

        if (bodyParserEngine == null) {
            logger.debug("No BodyParserEngine found for Content-Type {} at route {}", CONTENT_TYPE, getRequestPath());
            return null;
        }

        return bodyParserEngine.invoke(this, classOfT);

    }

    @Override
    public FlashScope getFlashCookie() {
        return flashScope;
    }

    @Override
    public Session getSessionCookie() {
        return session;
    }

    @Override
    public FlashScope getFlashScope() {
        return flashScope;
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public Cookie getCookie(String cookieName) {

        javax.servlet.http.Cookie[] cookies = httpServletRequest.getCookies();
        javax.servlet.http.Cookie servletCookie = CookieHelper.getCookie(cookieName, cookies);

        if (servletCookie == null) {
            return null;
        } else {
            return CookieHelper.convertServletCookieToNinjaCookie(servletCookie);
        }

    }

    @Override
    public boolean hasCookie(String cookieName) {
        return CookieHelper.getCookie(cookieName, httpServletRequest.getCookies()) != null;
    }

    @Override
    public List<Cookie> getCookies() {
        javax.servlet.http.Cookie[] servletCookies = httpServletRequest.getCookies();
        
        if (servletCookies == null) {
            return Collections.EMPTY_LIST;
        }
        
        List<Cookie> ninjaCookies = new ArrayList<>(servletCookies.length);

        for (javax.servlet.http.Cookie cookie : servletCookies) {
            Cookie ninjaCookie = CookieHelper.convertServletCookieToNinjaCookie(cookie);
            ninjaCookies.add(ninjaCookie);
        }

        return ninjaCookies;
    }
    
    @Override
    public void addCookie(Cookie cookie) {
        httpServletResponse.addCookie(CookieHelper.convertNinjaCookieToServletCookie(cookie));
    }
    
    @Override
    public void unsetCookie(Cookie cookie) {
        httpServletResponse.addCookie(CookieHelper
                .convertNinjaCookieToServletCookie(Cookie.builder(cookie).setMaxAge(0).build()));
    }

    @Deprecated
    @Override
    public String getRequestUri() {
        return httpServletRequest.getRequestURI();
    }

    @Override
    public String getHostname() {
        return httpServletRequest.getHeader("host");
    }

    @Override
    public String getScheme() {
        return httpServletRequest.getScheme();
    }

    @Override
    public String getRemoteAddr() {
        
        boolean isUsageOfXForwardedHeaderEnabled 
                = ninjaProperties.getBooleanWithDefault(
                        Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false);
        
        String remoteAddr;
        
        if (!isUsageOfXForwardedHeaderEnabled) {
            remoteAddr = httpServletRequest.getRemoteAddr();
        } else {
            remoteAddr = calculateRemoteAddrAndTakeIntoAccountXForwardHeader();
        }
        
        return remoteAddr;
    }
    
    private String calculateRemoteAddrAndTakeIntoAccountXForwardHeader() {
        
        String remoteAddr = getHeader(X_FORWARD_HEADER);

        if (remoteAddr != null) {
            if (remoteAddr.contains(",")) {
                // sometimes the header is of form client ip,proxy 1 ip,proxy 2 ip,...,proxy n ip,
                // we just want the client
                remoteAddr = StringUtils.split(remoteAddr, ',')[0].trim();
            }
            try {
                // If ip4/6 address string handed over, simply does pattern validation.
                InetAddress.getByName(remoteAddr);
            } catch (UnknownHostException e) {
                remoteAddr = httpServletRequest.getRemoteAddr();
            }
        } else {
            remoteAddr = httpServletRequest.getRemoteAddr();
        }
        
        return remoteAddr;
    }

    @Override
    public boolean isAsync() {
        return asyncStrategy != null;
    }

    @Override
    public void handleAsync() {
        synchronized (asyncLock) {
            if (asyncStrategy == null) {
                asyncStrategy = AsyncStrategyFactoryHolder.getInstance(
                        httpServletRequest).createStrategy(httpServletRequest,
                                resultHandler);
                asyncStrategy.handleAsync();
            }
        }
    }

    @Override
    public void returnResultAsync(Result result) {
        synchronized (asyncLock) {
            handleAsync();
            asyncStrategy.returnResultAsync(result, this);
        }
    }

    @Override
    public void asyncRequestComplete() {
        returnResultAsync(null);
    }

    /**
     * Used to indicate that the controller has finished executing
     */
    @Override
    public Result controllerReturned() {
        if (asyncStrategy != null) {
            return asyncStrategy.controllerReturned();
        }
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return httpServletRequest.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return httpServletRequest.getReader();
    }

    private ResponseStreams finalizeHeaders(Result result, Boolean handleFlashAndSessionCookie) {

        httpServletResponse.setStatus(result.getStatusCode());

        // copy headers
        for (Entry<String, String> header : result.getHeaders().entrySet()) {
            httpServletResponse.addHeader(header.getKey(), header.getValue());
        }

        // copy ninja cookies / flash and session
        if (handleFlashAndSessionCookie) {
            flashScope.save(this, result);
            session.save(this, result);
        }

        // copy cookies
        for (ninja.Cookie cookie : result.getCookies()) {
            httpServletResponse.addCookie(CookieHelper
                    .convertNinjaCookieToServletCookie(cookie));

        }

        // set content type
        if (result.getContentType() != null) {

            httpServletResponse.setContentType(result.getContentType());
        }

        // Set charset => use utf-8 if not set
        // Sets correct encoding for Content-Type. But also for the output
        // writers.
        if (result.getCharset() != null) {
            httpServletResponse.setCharacterEncoding(result.getCharset());
        } else {
            httpServletResponse.setCharacterEncoding(NinjaConstant.UTF_8);
        }

        // possibly
        ResponseStreamsServlet responseStreamsServlet = new ResponseStreamsServlet();
        responseStreamsServlet.init(httpServletResponse);

        return responseStreamsServlet;

    }

    @Override
    public ResponseStreams finalizeHeadersWithoutFlashAndSessionCookie(Result result) {
        return finalizeHeaders(result, false);
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        return finalizeHeaders(result, true);
    }

    @Override
    public String getRequestContentType() {
        return httpServletRequest.getContentType();
    }

    @Override
    public String getAcceptContentType() {
        String contentType = httpServletRequest.getHeader("accept");

        if (contentType == null) {
            return Result.TEXT_HTML;
        }

        if (contentType.indexOf("application/xhtml") != -1
                || contentType.indexOf("text/html") != -1
                || contentType.startsWith("*/*")) {
            return Result.TEXT_HTML;
        }

        if (contentType.indexOf("application/xml") != -1
                || contentType.indexOf("text/xml") != -1) {
            return Result.APPLICATION_XML;
        }

        if (contentType.indexOf("application/json") != -1
                || contentType.indexOf("text/javascript") != -1) {
            return Result.APPLICATION_JSON;
        }

        if (contentType.indexOf("text/plain") != -1) {
            return Result.TEXT_PLAIN;
        }

        if (contentType.indexOf("application/octet-stream") != -1) {
            return Result.APPLICATION_OCTET_STREAM;
        }

        if (contentType.endsWith("*/*")) {
            return Result.TEXT_HTML;
        }

        return Result.TEXT_HTML;
    }

    @Override
    public String getAcceptEncoding() {
        return httpServletRequest.getHeader("accept-encoding");
    }

    @Override
    public String getAcceptLanguage() {
        return httpServletRequest.getHeader("accept-language");
    }

    @Override
    public String getAcceptCharset() {
        return httpServletRequest.getHeader("accept-charset");
    }

    @Override
    public Route getRoute() {
        return route;
    }

    @Override
    public boolean isMultipart() {
        return ServletFileUpload.isMultipartContent(httpServletRequest);
    }

    @Override
    public FileItemIterator getFileItemIterator() {

        long maxFileSize = ninjaProperties.getIntegerWithDefault(NinjaConstant.UPLOADS_MAX_FILE_SIZE, -1);
        long maxTotalSize = ninjaProperties.getIntegerWithDefault(NinjaConstant.UPLOADS_MAX_TOTAL_SIZE, -1);
        
        ServletFileUpload upload = new ServletFileUpload();
        upload.setFileSizeMax(maxFileSize);
        upload.setSizeMax(maxTotalSize);
        
        FileItemIterator fileItemIterator = null;

        try {
            fileItemIterator = upload.getItemIterator(httpServletRequest);
        } catch (FileUploadException | IOException e) {
            logger.error("Error while trying to process mulitpart file upload",
                    e);
        }

        return fileItemIterator;
    }

    @Override
    public String getRequestPath() {
        return requestPath;
    }

    private String performGetRequestPath() {
        // http://stackoverflow.com/questions/966077/java-reading-undecoded-url-from-servlet

        // this one is unencoded:
        String unencodedContextPath = httpServletRequest.getContextPath();
        // this one is unencoded, too, but may containt the context:
        String fullUnencodedUri = httpServletRequest.getRequestURI();

        String result = fullUnencodedUri.substring(unencodedContextPath
                .length());

        return result;
    }

    @Override
    public Validation getValidation() {
        return validation;
    }

    @Override
    public String getMethod() {
        return httpServletRequest.getMethod();
    }

    @Override
    public Object getAttribute(String name) {
        return httpServletRequest.getAttribute(name);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(getAttribute(name));
    }

    @Override
    public void setAttribute(String name, Object value) {
        httpServletRequest.setAttribute(name, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        // build map of attributes
        Map<String,Object> attributes = new HashMap<String,Object>();
        
        Enumeration<String> en = httpServletRequest.getAttributeNames();
        
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            Object value = httpServletRequest.getAttribute(name);
            attributes.put(name, value);
        }
        
        return attributes;
    }

    /**
     * When a servlet engine gets a content type like: "application/json" it
     * assumes a default encoding of iso-xxxxx.
     *
     * That is not what Ninja does (and is not consistent with default encodings
     * of application/json and application/xml).
     *
     * Therefore we'll set utf-8 as request encoding if it is not set.
     */
    private void enforceCorrectEncodingOfRequest() {

        String charset = NinjaConstant.UTF_8;

        String contentType = getHeader(CONTENT_TYPE);

        if (contentType != null) {
            charset = HttpHeaderUtils.getCharsetOfContentTypeOrUtf8(contentType);
        }

        try {
            httpServletRequest.setCharacterEncoding(charset);
        } catch (UnsupportedEncodingException e) {
            logger.error("Server does not support charset of content type: " + contentType);
        }

    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    /**
     * Convenience method to access ServletContext of this context.
     *
     * @return ServletContext of this Context
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * Convenience method to access HttpServletRequest of this context.
     *
     * @return HttpServletRequest of this Context
     */
    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * Convenience method to access HttpServletResponse of this context.
     *
     * Usually you don't want to do that.
     *
     * @return HttpServletResponse of this Context.
     */
    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
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
    
    private void processFormFields() {
        if (formFieldsProcessed) return;
        formFieldsProcessed = true;

        // return if not multipart
        if (!ServletFileUpload.isMultipartContent(httpServletRequest))
            return;

        // get fileProvider from route method/class, or defaults to an injected one
        // if none injected, then we do not process form fields this way and let the user
        // call classic getFileItemIterator() by himself
        FileProvider fileProvider = null;
        if (route != null) {
            if (fileProvider == null) {
                fileProvider = route.getControllerMethod().getAnnotation(FileProvider.class);
            }
            if (fileProvider == null) {
                fileProvider = route.getControllerClass().getAnnotation(FileProvider.class);
            }
        }
        
        // get file item provider from file provider or default one
        FileItemProvider fileItemProvider = null;
        if (fileProvider == null) {
            fileItemProvider = injector.getInstance(FileItemProvider.class);
        } else {
            fileItemProvider = injector.getInstance(fileProvider.value());
        }
        
        if (fileItemProvider instanceof NoFileItemProvider) return;
        
        // Initialize maps and other constants
        ArrayListMultimap<String, String> formMap = ArrayListMultimap.create();
        ArrayListMultimap<String, FileItem> fileMap = ArrayListMultimap.create();
        
        
        // This is the iterator we can use to iterate over the contents of the request.
        try {
            
            FileItemIterator fileItemIterator = getFileItemIterator();
            
            while (fileItemIterator.hasNext()) {

                FileItemStream item = fileItemIterator.next();

                if (item.isFormField()) {
                    
                    // save the form field for later use from getParameter
                    String value = Streams.asString(item.openStream());
                    formMap.put(item.getFieldName(), value);

                } else {
                    
                    // process file as input stream and save for later use in getParameterAsFile or getParameterAsInputStream
                    FileItem fileItem = fileItemProvider.create(item);
                    fileMap.put(item.getFieldName(), fileItem);
                }
            }
        } catch (FileUploadException | IOException e) {
            throw new RuntimeException("Failed to parse multipart request data", e);
        }

        // convert both multimap<K,V> to map<K,List<V>>
        formFieldsMap = toUnmodifiableMap(formMap);
        fileFieldsMap = toUnmodifiableMap(fileMap);
    }
    
    /**
     * Utility method to convert a Guava Multimap to an unmodifiable Map that
     * uses a List<T> as a value. Optimized for the case where values are already
     * internally stored as a List<T> (e.g. ArrayListMultimap).
     * @param <T> The value type
     * @param multimap The multimap to convert from
     * @return The unmodifiable converted map
     */
    private <T> Map<String, List<T>> toUnmodifiableMap(Multimap<String, T> multimap) {
        Map<String, List<T>> map = new HashMap<>(multimap.size());
        
        for (Entry<String, Collection<T>> entry: multimap.asMap().entrySet()) {
            Collection<T> value = entry.getValue();
            if (value == null) {
                Collections.emptyList();
            } else if (value instanceof List) {
                map.put(entry.getKey(), (List<T>)value);
            } else {
                map.put(entry.getKey(), new ArrayList<>(value));
            }
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    @Override
    public void cleanup() {
        // call cleanup on all file items
        if (fileFieldsMap != null) {
            for (List<FileItem> files: fileFieldsMap.values()) {
                for (FileItem file: files) {
                    file.cleanup();
                }
            }
        }
    }
}
