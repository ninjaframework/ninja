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

package ninja.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Route;
import ninja.servlet.async.AsyncStrategy;
import ninja.servlet.async.AsyncStrategyFactoryHolder;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.HttpHeaderUtils;
import ninja.utils.NinjaConstant;
import ninja.utils.ResponseStreams;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import com.google.inject.Inject;

public class ContextImpl implements Context.Impl {

    private HttpServletRequest httpServletRequest;

    private HttpServletResponse httpServletResponse;

    private Route route;

    private AsyncStrategy asyncStrategy;
    private final Object asyncLock = new Object();

    private final BodyParserEngineManager bodyParserEngineManager;

    private final FlashCookie flashCookie;

    private final SessionCookie sessionCookie;
    private final ResultHandler resultHandler;
    private final Validation validation;

    @Inject
    Logger logger;

    @Inject
    public ContextImpl(BodyParserEngineManager bodyParserEngineManager,
                       FlashCookie flashCookie,
                       SessionCookie sessionCookie,
                       ResultHandler resultHandler,
                       Validation validation) {

        this.bodyParserEngineManager = bodyParserEngineManager;
        this.flashCookie = flashCookie;
        this.sessionCookie = sessionCookie;
        this.resultHandler = resultHandler;
        this.validation = validation;
    }

    public void init(HttpServletRequest httpServletRequest,
                     HttpServletResponse httpServletResponse) {
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;

        // init flash scope:
        flashCookie.init(this);

        // init session scope:
        sessionCookie.init(this);

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
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getParameter(String key) {
        return httpServletRequest.getParameter(key);
    }

    @Override
    public List<String> getParameterValues(String name) {
        String[] params = httpServletRequest.getParameterValues(name);
        if (params == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(params);
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
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public Map<String, String[]> getParameters() {
        return httpServletRequest.getParameterMap();
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
            return null;
        }
        
        // If Content-type is application/json; charset=utf-8 we split away the charset
        // application/json
        String contentTypeOnly = HttpHeaderUtils.getContentTypeFromContentTypeAndCharacterSetting(
                rawContentType);
        
        BodyParserEngine bodyParserEngine = bodyParserEngineManager
                .getBodyParserEngineForContentType(contentTypeOnly);

        
        if (bodyParserEngine == null) {
            return null;
        }

        return bodyParserEngine.invoke(this, classOfT);

    }

    @Override
    public FlashCookie getFlashCookie() {
        return flashCookie;
    }

    @Override
    public SessionCookie getSessionCookie() {
        return sessionCookie;
    }
    
    @Override
    public Cookie getCookie(String cookieName) {
        
        javax.servlet.http.Cookie[] cookies = httpServletRequest.getCookies();
        javax.servlet.http.Cookie servletCookie = CookieHelper.getCookie(cookieName, cookies);
        
        if (servletCookie == null) {
            
            return null;
            
        } else {
            
            Cookie ninjaCookie = CookieHelper.convertServletCookieToNinjaCookie(servletCookie);           
            return ninjaCookie;
            
        }

    }
    
    @Override
    public boolean hasCookie(String cookieName) {
        return CookieHelper.getCookie(cookieName, httpServletRequest.getCookies()) != null;
    }

    @Override
    public List<Cookie> getCookies() {
        
        
        javax.servlet.http.Cookie[] servletCookies = httpServletRequest.getCookies();
        List<Cookie> ninjaCookies = new ArrayList<Cookie>();
        
        for (javax.servlet.http.Cookie cookie : servletCookies) {
            
            Cookie ninjaCookie = CookieHelper.convertServletCookieToNinjaCookie(cookie);
            ninjaCookies.add(ninjaCookie);
            
        }
        
        return ninjaCookies;
        
    }

    @Deprecated
    @Override
    public String getRequestUri() {
        return httpServletRequest.getRequestURI();
    }

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
    public Result controllerReturned() {
        if (asyncStrategy != null) {
            return asyncStrategy.controllerReturned();
        }
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {

        enforeCorrectEncodingOfRequest();

        return httpServletRequest.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {

        enforeCorrectEncodingOfRequest();

        return httpServletRequest.getReader();
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {

        httpServletResponse.setStatus(result.getStatusCode());

        // copy headers
        for (Entry<String, String> header : result.getHeaders().entrySet()) {
            httpServletResponse.addHeader(header.getKey(), header.getValue());
        }

        // copy ninja cookies / flash and session
        flashCookie.save(this, result);
        sessionCookie.save(this, result);

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

        return (ResponseStreams) responseStreamsServlet;

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
            return Result.APPLICATON_JSON;
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

        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator fileItemIterator = null;

        try {
            fileItemIterator = upload.getItemIterator(httpServletRequest);
        } catch (FileUploadException e) {
            logger.error("Error while trying to process mulitpart file upload",
                    e);
        } catch (IOException e) {
            logger.error("Error while trying to process mulitpart file upload",
                    e);
        }

        return fileItemIterator;
    }

    @Override
    public String getRequestPath() {
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
     * When a servlet engine gets a content type like:
     * "application/json" it assumes a default encoding of iso-xxxxx.
     * 
     * That is not what Ninja does (and is not consistent with default encodings
     * of application/json and application/xml).
     * 
     * Therefore we'll set utf-8 as request encoding if it is not set.
     */
    private void enforeCorrectEncodingOfRequest() {
        
        String charset = NinjaConstant.UTF_8;
        
        String contentType = getHeader("content-type");
        
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

        return httpServletRequest.getContextPath();
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

}
