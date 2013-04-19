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

package ninja.grizzly;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import ninja.AbstractContextImpl;
import ninja.Cookie;
import ninja.Result;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.NinjaConstant;
import ninja.utils.ResponseStreams;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Thomas Broyer
 */
public class ContextImpl extends AbstractContextImpl {

    private Request request;
    private Response response;

    @Inject
    Logger logger;

    @Inject
    public ContextImpl(BodyParserEngineManager bodyParserEngineManager,
                       FlashCookie flashCookie,
                       SessionCookie sessionCookie,
                       ResultHandler resultHandler,
                       Validation validation) {
        super(bodyParserEngineManager, flashCookie, sessionCookie, resultHandler, validation);
    }

    public void init(Request request, Response response) {
        this.request = request;
        this.response = response;

        init();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public String getRequestContentType() {
        return request.getContentType();
    }

    @Override
    protected int getRequestContentLength() {
        return request.getContentLength();
    }

    @Override
    protected String getRequestCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public String getRequestUri() {
        return request.getRequestURI();
    }

    @Override
    public String getRequestPath() {
        return request.getHttpHandlerPath() + Strings.nullToEmpty(request.getPathInfo());
    }

    @Override
    public Cookie getCookie(String cookieName) {
        org.glassfish.grizzly.http.Cookie cookie = CookieHelper.getCookie(cookieName, request.getCookies());
        if (cookie == null) {
            return null;
        }
        return CookieHelper.convertGrizzlyCookieToNinjaCookie(cookie);
    }

    @Override
    public boolean hasCookie(String cookieName) {
        return CookieHelper.getCookie(cookieName, request.getCookies()) != null;
    }

    @Override
    public List<Cookie> getCookies() {
        org.glassfish.grizzly.http.Cookie[] grizzlyCookies = request.getCookies();
        List<Cookie> ninjaCookies = new ArrayList<Cookie>(grizzlyCookies.length);
        for (org.glassfish.grizzly.http.Cookie cookie : grizzlyCookies) {
            Cookie ninjaCookie = CookieHelper.convertGrizzlyCookieToNinjaCookie(cookie);
            ninjaCookies.add(ninjaCookie);
        }
        return ninjaCookies;
    }

    @Override
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public List<String> getParameterValues(String name) {
        String[] values = request.getParameterValues(name);
        if (values == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(values);
    }

    @Override
    public Map<String, String[]> getParameters() {
        return request.getParameterMap();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return Lists.newArrayList(request.getHeaders(name));
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        Iterable<String> names = request.getHeaderNames();
        for (String name : names) {
            headers.put(name, Lists.newArrayList(request.getHeaders(name)));
        }
        return headers;
    }

    @Override
    public String getCookieValue(String name) {
        return CookieHelper.getCookieValue(name, request.getCookies());
    }

    @Override
    public void handleAsync() {
        response.suspend();
    }

    @Override
    public void returnResultAsync(Result result) {
        resultHandler.handleResult(result, this);
        response.resume();
    }

    @Override
    public Result controllerReturned() {
        return null;
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        response.setStatus(result.getStatusCode());

        // copy headers
        for (Map.Entry<String, String> header : result.getHeaders().entrySet()) {
            response.addHeader(header.getKey(), header.getValue());
        }

        // copy ninja cookies / flash and session
        flashCookie.save(this, result);
        sessionCookie.save(this, result);

        // copy cookies
        for (Cookie cookie : result.getCookies()) {
            response.addCookie(CookieHelper
                    .convertNinjaCookieToGrizzlyCookie(cookie));

        }

        // set content type
        if (result.getContentType() != null) {

            response.setContentType(result.getContentType());
        }

        // Set charset => use utf-8 if not set
        // Sets correct encoding for Content-Type. But also for the output
        // writers.
        if (result.getCharset() != null) {
            response.setCharacterEncoding(result.getCharset());
        } else {
            response.setCharacterEncoding(NinjaConstant.UTF_8);
        }

        // possibly
        ResponseStreamsGrizzly responseStreamsGrizzly = new ResponseStreamsGrizzly();
        responseStreamsGrizzly.init(response);

        return responseStreamsGrizzly;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return request.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(request.getReader());
    }

    @Override
    public String getMethod() {
        return request.getMethod().getMethodString();
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        request.setAttribute(name, value);
    }
}
