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

package ninja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.uploads.FileItem;
import ninja.utils.ResponseStreams;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemIterator;

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
    public String getHostname() {
        return wrapped.getHostname();
    }

    @Override
    public String getScheme() {
        return wrapped.getScheme();
    }

    @Override
    public String getRemoteAddr() {
        return wrapped.getRemoteAddr();
    }

    @Override
    public FlashScope getFlashCookie() {
        return wrapped.getFlashCookie();
    }

    @Override
    public Session getSessionCookie() {
        return wrapped.getSessionCookie();
    }
    
    @Override
    public FlashScope getFlashScope() {
        return wrapped.getFlashScope();
    }

    @Override
    public Session getSession() {
        return wrapped.getSession();
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
    public FileItem getParameterAsFileItem(String key) {
        return wrapped.getParameterAsFileItem(key);
    }

    @Override
    public List<FileItem> getParameterAsFileItems(String name) {
        return wrapped.getParameterAsFileItems(name);
    }

    @Override
    public Map<String, List<FileItem>> getParameterFileItems() {
        return wrapped.getParameterFileItems();
    }

    @Override
    public <T> T getParameterAs(String key, Class<T> clazz) {
        return wrapped.getParameterAs(key, clazz);
    }

    @Override
    public <T> T getParameterAs(String key, Class<T> clazz, T defaultValue) {
        return wrapped.getParameterAs(key, clazz, defaultValue);
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
    public boolean isAsync() {
        return wrapped.isAsync();
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
    public ResponseStreams finalizeHeadersWithoutFlashAndSessionCookie(Result result) {
        return wrapped.finalizeHeadersWithoutFlashAndSessionCookie(result);
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

    @Override
    public Object getAttribute(String name) {
        return wrapped.getAttribute(name);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> clazz) {
        return wrapped.getAttribute(name, clazz);
    }

    @Override
    public void setAttribute(String name, Object value) {
        wrapped.setAttribute(name, value);
    }
    
    @Override
    public Map<String,Object> getAttributes() {
        return wrapped.getAttributes();
    }

    @Override
    public String getContextPath() {
        return wrapped.getContextPath();
    }

    @Override
    public boolean isRequestJson() {
        return wrapped.isRequestJson();
    }

    @Override
    public boolean isRequestXml() {
        return wrapped.isRequestXml();
    }

    @Override
    public void addCookie(Cookie cookie) {
        wrapped.addCookie(cookie);
    }

    @Override
    public void unsetCookie(Cookie cookie) {
        wrapped.unsetCookie(cookie);
    }

    @Override
    public void cleanup() {
        wrapped.cleanup();
    }
}