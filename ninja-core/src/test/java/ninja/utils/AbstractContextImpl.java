/**
 * Copyright (C) 2012-2016 the original author or authors.
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
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItemIterator;

import ninja.Cookie;
import ninja.Result;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.params.ParamParsers;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.uploads.FileItem;
import ninja.validation.Validation;

/**
 * Used for mocking an AbstractContext in unit tests.
 */
public class AbstractContextImpl extends AbstractContext {

    public AbstractContextImpl(BodyParserEngineManager bodyParserEngineManager, FlashScope flashScope, NinjaProperties ninjaProperties, Session session, Validation validation, ParamParsers paramParsers) {
        super(bodyParserEngineManager, flashScope, ninjaProperties, session, validation, paramParsers);
    }

    @Override
    protected String getRealRemoteAddr() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRequestContentType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getRequestUri() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getHostname() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Cookie getCookie(String cookieName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasCookie(String cookieName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Cookie> getCookies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getParameter(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getParameterValues(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String[]> getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getHeaders(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleAsync() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void returnResultAsync(Result result) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Result controllerReturned() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMultipart() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileItemIterator getFileItemIterator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getMethod() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getAttribute(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttribute(String name, Object value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, Object> getAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FileItem getParameterAsFileItem(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FileItem> getParameterAsFileItems(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, List<FileItem>> getParameterFileItems() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void cleanup() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
