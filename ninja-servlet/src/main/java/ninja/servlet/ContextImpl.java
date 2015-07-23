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
import ninja.NinjaFileItemStream;
import ninja.Result;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.servlet.async.AsyncStrategy;
import ninja.servlet.async.AsyncStrategyFactoryHolder;
import ninja.servlet.file.InMemoryFileItemFactory;
import ninja.servlet.file.NinjaFileItemStreamConverter;
import ninja.servlet.file.NinjaFileItemStreamWrapper;
import ninja.session.FlashScope;
import ninja.session.Session;
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

    private final Map<String, List<NinjaFileItemStream>> fileItems = new HashMap<>();
    private final Map<String, List<String>> multipartParams = new HashMap<>();

    // In Async mode, these values will be set to null, so save them
    private String requestPath;
    private String contextPath;

    @Inject
    Injector injector;

    private Logger logger = LoggerFactory.getLogger(ContextImpl.class);

    @Inject
    public ContextImpl(
            BodyParserEngineManager bodyParserEngineManager,
            FlashScope flashCookie,
            NinjaProperties ninjaProperties,
            ResultHandler resultHandler,
            Session sessionCookie,
            Validation validation) {

        this.bodyParserEngineManager = bodyParserEngineManager;
        this.flashScope = flashCookie;
        this.ninjaProperties = ninjaProperties;
        this.session = sessionCookie;
        this.resultHandler = resultHandler;
        this.validation = validation;
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

        if (isMultipart()) {
            parseParts(getRequestItemsIterator());
        }
    }

    @Override
    public void setRoute(Route route) {
        this.route = route;
    }

    @Override
    public void cleanup() {
        // no op for non-multipart request context
        for (List<NinjaFileItemStream> items : fileItems.values()) {
            for (NinjaFileItemStream item : items) {
                item.purge();
            }
        }
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
        List<String> params = multipartParams.get(key);
        if (params != null && !params.isEmpty()) {
            return params.get(0);
        }
        return httpServletRequest.getParameter(key);
    }

    @Override
    public List<String> getParameterValues(String name) {
        List<String> result = new ArrayList<>();

        String[] params = httpServletRequest.getParameterValues(name);
        if (params != null) {
            result.addAll(Arrays.asList(params));
        }

        List<String> multipartParamsList = multipartParams.get(name);
        if (multipartParamsList != null) {
            result.addAll(multipartParamsList);
        }

        return result;
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
        // create new map with query params
        Map<String, String[]> map = new HashMap<>(httpServletRequest.getParameterMap());

        // add params of multipart request
        for (Entry<String, List<String>> e : multipartParams.entrySet()) {

            List<String> ls = new ArrayList<>(e.getValue());

            String[] values = map.get(e.getKey());
            if (values != null) {
                ls.addAll(Arrays.asList(values));
            }

            map.put(e.getKey(), ls.toArray(new String[ls.size()]));
        }
        return map;
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
        // streaming API of commons-upload allows us to iterate items of a
        // multipart request only once. Item iterator is used in init() method,
        // so we simulate iterator here by custom iterator implementation

        final ArrayList<FileItemStream> items = new ArrayList<>();

        // add file item wrappers to the list
        for (Entry<String, List<NinjaFileItemStream>> e : fileItems.entrySet()) {
            for (NinjaFileItemStream item : e.getValue()) {
                NinjaFileItemStreamWrapper wrapper = new NinjaFileItemStreamWrapper(e.getKey());
                wrapper.setItemStream(item);
                items.add(wrapper);
            }
        }
        // add form field params to the list
        for (Map.Entry<String, List<String>> e : multipartParams.entrySet()) {
            for (String value : e.getValue()) {
                NinjaFileItemStreamWrapper wrapper = new NinjaFileItemStreamWrapper(e.getKey());
                wrapper.setValue(value);
                items.add(wrapper);
            }
        }

        return new FileItemIterator() {

            private int current = -1;

            @Override
            public boolean hasNext() throws FileUploadException, IOException {
                return current < items.size() - 1;
            }

            @Override
            public FileItemStream next() throws FileUploadException, IOException {
                return items.get(++current);
            }
        };
    }

    @Override
    public NinjaFileItemStream getUploadedFileStream(String name) {
        List<NinjaFileItemStream> ls = fileItems.get(name);
        if (ls != null && ls.size() > 0) {
            return ls.get(0);
        }
        return null;
    }

    @Override
    public List<NinjaFileItemStream> getUploadedFileStreams(String name) {
        List<NinjaFileItemStream> ls = fileItems.get(name);
        if (ls != null) {
            return new ArrayList<>(ls);
        }
        return Collections.emptyList();
    }

    @Override
    public List<NinjaFileItemStream> getFileItems() {
        List<NinjaFileItemStream> all = new ArrayList<>();
        for (List<NinjaFileItemStream> items : fileItems.values()) {
            all.addAll(items);
        }
        return all;
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

    /**
     * Multipart request payload parser. This method accepts file item iterator
     * and is not private to make testing easier by passing custom mock
     * iterator.
     *
     * @param fileItemIterator
     */
    void parseParts(FileItemIterator fileItemIterator) {

        NinjaFileItemStreamConverter fileItemStreamConverter
                = injector.getInstance(NinjaFileItemStreamConverter.class);
        try {
            while (fileItemIterator.hasNext()) {
                FileItemStream fileItemStream = fileItemIterator.next();
                String fieldName = fileItemStream.getFieldName();

                if (fileItemStream.isFormField()) {

                    // simple key/value item
                    String value = Streams.asString(fileItemStream.openStream());
                    List<String> ls = multipartParams.get(fieldName);
                    if (ls == null) {
                        ls = new ArrayList<>();
                        multipartParams.put(fieldName, ls);
                    }
                    ls.add(value);

                } else {
                    // an attached file
                    List<NinjaFileItemStream> items = fileItems.get(fieldName);
                    if (items == null) {
                        items = new ArrayList<>();
                        fileItems.put(fieldName, items);
                    }
                    items.add(fileItemStreamConverter.convert(fileItemStream));
                }
            }
        } catch (FileUploadException | IOException ex) {
            throw new RuntimeException("Failed to parse multipart request data", ex);
        }
    }

    private FileItemIterator getRequestItemsIterator() {

        ServletFileUpload upload = new ServletFileUpload();

        Boolean inMemory = ninjaProperties.getBooleanWithDefault(
                NinjaConstant.FILE_UPLOADS_IN_MEMORY, false);
        Integer maxSize = ninjaProperties.getInteger(
                NinjaConstant.FILE_UPLOADS_MAX_REQUEST_SIZE);
        Integer maxFileSize = ninjaProperties.getInteger(
                NinjaConstant.FILE_UPLOADS_MAX_FILE_SIZE);

        if (inMemory) {
            // inject in-memory file item factory to upload instance
            InMemoryFileItemFactory inMemoryFileItemFactory
                    = injector.getInstance(InMemoryFileItemFactory.class);
            upload.setFileItemFactory(inMemoryFileItemFactory);

            // when uploaded files are handled in-memory, do not leave max file size without limit
            upload.setFileSizeMax(inMemoryFileItemFactory.getMaxFileSize());
        }
        if (maxSize != null) {
            upload.setSizeMax(maxSize);
        }
        if (maxFileSize != null) {
            upload.setFileSizeMax(maxFileSize);
        }

        FileItemIterator fileItemIterator = null;

        try {
            fileItemIterator = upload.getItemIterator(httpServletRequest);
        } catch (FileUploadException | IOException e) {
            logger.error("Error while trying to process mulitpart file upload",
                    e);
        }

        return fileItemIterator;
    }

}
