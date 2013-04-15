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

import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.HttpHeaderUtils;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractContextImpl implements Context.Impl {
    protected final BodyParserEngineManager bodyParserEngineManager;
    protected final FlashCookie flashCookie;
    protected final SessionCookie sessionCookie;
    protected final ResultHandler resultHandler;
    protected final Validation validation;

    private Route route;

    protected AbstractContextImpl(BodyParserEngineManager bodyParserEngineManager,
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

    /**
     * Subclasses will generally have an {@code init} method with arguments for
     * their request and response, and should thus call this method from there.
     */
    protected void init() {
        // init flash scope:
        flashCookie.init(this);

        // init session scope:
        sessionCookie.init(this);
    }

    @Override
    public void setRoute(Route route) {
        this.route = route;
    }

    protected abstract Logger getLogger();

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation is expressed in terms of {@link #getHeader(String)}.
     *
     * @return {@inheritDoc}
     */
    @Override
    public String getRequestContentType() {
        return getHeader("content-type");
    }

    /**
     * The default implementation is expressed in terms of {@link #getHeader(String)}.
     */
    protected int getRequestContentLength() {
        String rawContentLength = getHeader("content-length");
        if (rawContentLength == null) {
            return -1;
        }
        try {
            return Integer.parseInt(rawContentLength);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    /**
     * The default implementation is expressed in terms of {@link #getRequestContentType()}.
     */
    protected String getRequestCharacterEncoding() {
        String rawContentType = getRequestContentType();
        if (rawContentType == null) {
            return null;
        }
        return HttpHeaderUtils.getCharsetOfContentTypeOrUtf8(rawContentType);
    }

    @Override
    public FlashCookie getFlashCookie() {
        return flashCookie;
    }

    @Override
    public SessionCookie getSessionCookie() {
        return sessionCookie;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation is expressed in terms of {@link #getCookies()}.
     *
     * @param cookieName {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Cookie getCookie(String cookieName) {
        for (Cookie cookie : getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation is expressed in terms of {@link #getCookie(String)}.
     *
     * @param cookieName {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean hasCookie(String cookieName) {
        return getCookie(cookieName) != null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementatino is expressed in terms of {@link #getCookie(String)}.
     *
     * @param name {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return cookie == null ? null : cookie.getValue();
    }

    /**
     * {@inheritDoc}.
     * <p>
     * The default implementation is expressed in terms of {@link #getParameterValues(String)}.
     *
     * @param name {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getParameter(String name) {
        List<String> values = getParameterValues(name);
        return values.isEmpty() ? null : values.get(0);
    }

    /**
     * {@inheritDoc}.
     * <p>
     * The default implementation is expressed in terms of {@link #getParameter(String)}.
     *
     * @param name {@inheritDoc}
     * @param defaultValue {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String getParameter(String name, String defaultValue) {
        String parameter = getParameter(name);
        if (parameter == null) {
            return defaultValue;
        }
        return parameter;
    }

    /**
     * {@inheritDoc}.
     * <p>
     * The default implementation is expressed in terms of {@link #getParameterAsInteger(String, Integer)}.
     *
     * @param name {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Integer getParameterAsInteger(String name) {
        return getParameterAsInteger(name, null);
    }

    /**
     * {@inheritDoc}.
     * <p>
     * The default implementation is expressed in terms of {@link #getParameter(String)}.
     *
     * @param name {@inheritDoc}
     * @param defaultValue {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Integer getParameterAsInteger(String name, Integer defaultValue) {
        String parameter = getParameter(name);

        try {
            return Integer.parseInt(parameter);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation is expressed in terms of {@link #getParameters()}.
     *
     * @param name {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public List<String> getParameterValues(String name) {
        String[] values = getParameters().get(name);
        return values == null ? Collections.<String>emptyList() : Arrays.asList(values);
    }

    @Override
    public String getPathParameter(String name) {
        String encodedParameter = route.getPathParametersEncoded(
                getRequestPath()).get(name);

        if (encodedParameter == null) {
            return null;
        } else {
            return URI.create(encodedParameter).getPath();
        }
    }

    @Override
    public String getPathParameterEncoded(String name) {
        return route.getPathParametersEncoded(getRequestPath()).get(name);
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
    public boolean isMultipart() {
        String contentType = getRequestContentType();
        if (contentType == null) {
            return false;
        }
        int pos = contentType.indexOf('/');
        if (pos < 0) {
            // illformed content-type
            return false;
        }
        return contentType.substring(0, pos).equalsIgnoreCase("multipart");
    }

    @Override
    public FileItemIterator getFileItemIterator() {

        FileUpload upload = new FileUpload();
        FileItemIterator fileItemIterator = null;

        try {
            fileItemIterator = upload.getItemIterator(new RequestContext() {
                @Override
                public String getCharacterEncoding() {
                    return getRequestCharacterEncoding();
                }

                @Override
                public String getContentType() {
                    return getRequestContentType();
                }

                @Override
                public int getContentLength() {
                    return getRequestContentLength();
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return AbstractContextImpl.this.getInputStream();
                }
            });
        } catch (FileUploadException e) {
            getLogger().error("Error while trying to process multipart file upload", e);
        } catch (IOException e) {
            getLogger().error("Error while trying to process multipart file upload", e);
        }

        return fileItemIterator;
    }

    @Override
    public void asyncRequestComplete() {
        returnResultAsync(null);
    }

    @Override
    public Route getRoute() {
        return route;
    }

    @Override
    public Validation getValidation() {
        return validation;
    }

    @Override
    public String getAcceptContentType() {
        String contentType = getHeader("accept");

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
        return getHeader("accept-encoding");
    }

    @Override
    public String getAcceptLanguage() {
        return getHeader("accept-language");
    }

    @Override
    public String getAcceptCharset() {
        return getHeader("accept-charset");
    }

    @Override
    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(getAttribute(name));
    }
}
