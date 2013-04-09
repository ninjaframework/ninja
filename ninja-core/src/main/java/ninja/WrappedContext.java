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

import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.ResponseStreams;

import ninja.validation.Validation;
import org.apache.commons.fileupload.FileItemIterator;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * A wrapped context. Useful if filters want to modify the context before
 * sending it on.
 * 
 * @author James Roper
 */
public class WrappedContext implements Context {
    private final Context wrapped;

    public WrappedContext(Context wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getRequestUri() {
        return wrapped.getRequestUri();
    }

    @Override
    public FlashCookie getFlashCookie() {
        return wrapped.getFlashCookie();
    }

    @Override
    public SessionCookie getSessionCookie() {
        return wrapped.getSessionCookie();
    }

    @Override
    public String getParameter(String key) {
        return wrapped.getParameter(key);
    }

    @Override
    public List<String> getParameterValues(String name) {
        return wrapped.getParameterValues(name);
    }

    @Override
    public String getParameter(String key, String defaultValue) {
        return wrapped.getParameter(key, defaultValue);
    }

    @Override
    public Integer getParameterAsInteger(String key) {
        return wrapped.getParameterAsInteger(key);
    }

    @Override
    public Integer getParameterAsInteger(String key, Integer defaultValue) {
        return wrapped.getParameterAsInteger(key, defaultValue);
    }

    @Override
    public String getPathParameter(String key) {
        return wrapped.getPathParameter(key);
    }

    @Override
    public Integer getPathParameterAsInteger(String key) {
        return wrapped.getPathParameterAsInteger(key);
    }

    @Override
    public Map<String, String[]> getParameters() {
        return wrapped.getParameters();
    }

    @Override
    public String getHeader(String name) {
        return wrapped.getHeader(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return wrapped.getHeaders(name);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return wrapped.getHeaders();
    }

    @Override
    public String getCookieValue(String name) {
        return wrapped.getCookieValue(name);
    }

    @Override
    public <T> T parseBody(Class<T> classOfT) {
        return wrapped.parseBody(classOfT);
    }

    @Override
    public void handleAsync() {
        wrapped.handleAsync();
    }

    @Override
    public void returnResultAsync(Result result) {
        wrapped.returnResultAsync(result);
    }

    @Override
    public void asyncRequestComplete() {
        wrapped.asyncRequestComplete();
    }

    @Override
    public Result controllerReturned() {
        return wrapped.controllerReturned();
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        return wrapped.finalizeHeaders(result);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return wrapped.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return wrapped.getReader();
    }

    @Override
    public String getRequestContentType() {
        return wrapped.getRequestContentType();
    }

    @Override
    public Route getRoute() {
        return wrapped.getRoute();
    }

    @Override
    public boolean isMultipart() {
        return wrapped.isMultipart();
    }

    @Override
    public FileItemIterator getFileItemIterator() {
        return wrapped.getFileItemIterator();
    }

    @Override
    public String getRequestPath() {
        return wrapped.getRequestPath();
    }

    @Override
    public Validation getValidation() {
        return wrapped.getValidation();
    }

    @Override
    public String getPathParameterEncoded(String key) {
        return wrapped.getPathParameterEncoded(key);
    }

    @Override
    public String getAcceptContentType() {
        return wrapped.getAcceptContentType();
    }

    @Override
    public String getAcceptEncoding() {
        return wrapped.getAcceptEncoding();
    }

    @Override
    public String getAcceptLanguage() {
        return wrapped.getAcceptLanguage();
    }

    @Override
    public String getAcceptCharset() {
        return wrapped.getAcceptCharset();
    }

    @Override
    public Cookie getCookie(String cookieName) {
        return wrapped.getCookie(cookieName);
    }

    @Override
    public boolean hasCookie(String cookieName) {
        return wrapped.hasCookie(cookieName);
    }

    @Override
    public List<Cookie> getCookies() {
        return wrapped.getCookies();
    }

    @Override
    public String getMethod() {
        return wrapped.getMethod();
    }
}
