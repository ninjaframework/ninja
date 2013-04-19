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

package ninja.netty;

import java.io.*;
import java.net.URI;
import java.util.*;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import ninja.*;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashCookie;
import ninja.session.SessionCookie;
import ninja.utils.NinjaConstant;
import ninja.utils.ResponseStreams;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;

import com.google.inject.Inject;

/**
 * @author Thomas Broyer
 */
public class ContextImpl extends AbstractContextImpl {

    private ChannelHandlerContext ctx;
    // TODO: chunked requests
    private FullHttpRequest request;
    private FullHttpResponse response;
    private QueryStringDecoder decoder;
    private Map<String, Object> attributes;

    private Set<Cookie> cookies;
    private InputStream inputStream;
    private BufferedReader reader;

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

    public void init(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response) {
        this.ctx = ctx;
        this.request = request;
        this.response = response;

        this.decoder = new QueryStringDecoder(request.getUri());
        this.attributes = new LinkedHashMap<String, Object>();

        init();
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    public String getRequestUri() {
        return request.getUri();
    }

    @Override
    public String getRequestPath() {
        return decoder.path();
    }

    @Override
    public ninja.Cookie getCookie(String cookieName) {
        Cookie cookie = CookieHelper.getCookie(cookieName, ensureCookies());
        if (cookie == null) {
            return null;
        }
        return CookieHelper.convertNettyCookieToNinjaCookie(cookie);
    }

    private Set<Cookie> ensureCookies() {
        if (cookies == null) {
            cookies = new LinkedHashSet<Cookie>();
            for (String setCookieHeader : request.headers().getAll(HttpHeaders.Names.COOKIE)) {
                cookies.addAll(CookieDecoder.decode(setCookieHeader));
            }
        }
        return cookies;
    }

    @Override
    public boolean hasCookie(String cookieName) {
        return CookieHelper.getCookie(cookieName, ensureCookies()) != null;
    }

    @Override
    public List<ninja.Cookie> getCookies() {
        ensureCookies();
        ArrayList<ninja.Cookie> result = new ArrayList<ninja.Cookie>(cookies.size());
        for (Cookie cookie : cookies) {
            result.add(CookieHelper.convertNettyCookieToNinjaCookie(cookie));
        }
        return result;
    }

    @Override
    public Map<String, String[]> getParameters() {
        // XXX: lazy transform using Maps.transformValues?
        Map<String, List<String>> parameters = decoder.parameters();
        Map<String, String[]> result = new LinkedHashMap<String, String[]>(parameters.size());
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            result.put(entry.getKey(), Iterables.toArray(entry.getValue(), String.class));
        }
        return result;
    }

    @Override
    public List<String> getParameterValues(String name) {
        List<String> values = decoder.parameters().get(name);
        return values == null ? Collections.<String>emptyList() : values;
    }

    @Override
    public String getHeader(String name) {
        return request.headers().get(name);
    }

    @Override
    public List<String> getHeaders(String name) {
        return request.headers().getAll(name);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        // XXX: people should write APIs when they don't know the protocols, sigh!
        Set<String> headerNames = request.headers().names();
        Map<String, List<String>> result = new LinkedHashMap<String, List<String>>(headerNames.size());
        for (String headerName : headerNames) {
            result.put(headerName, request.headers().getAll(headerName));
        }
        return result;
    }

    @Override
    public String getCookieValue(String name) {
        return CookieHelper.getCookieValue(name, ensureCookies());
    }

    @Override
    public void handleAsync() {
        // no-op: Netty is async by default
    }

    @Override
    public void returnResultAsync(Result result) {
        resultHandler.handleResult(result, this);
    }

    @Override
    public Result controllerReturned() {
        return null;
    }

    @Override
    public ResponseStreams finalizeHeaders(Result result) {
        response.setStatus(HttpResponseStatus.valueOf(result.getStatusCode()));

        // copy headers
        for (Map.Entry<String, String> header : result.getHeaders().entrySet()) {
            response.headers().add(header.getKey(), header.getValue());
        }

        // copy ninja cookies / flash and session
        flashCookie.save(this, result);
        sessionCookie.save(this, result);

        // copy cookies
        for (ninja.Cookie cookie : result.getCookies()) {
            response.headers().add(HttpHeaders.Names.SET_COOKIE,
                    ServerCookieEncoder.encode(CookieHelper.convertNinjaCookieToNettyCookie(cookie)));
        }

        // Set charset => use utf-8 if not set
        String charset = result.getCharset() != null ? result.getCharset() : NinjaConstant.UTF_8;

        // set content type
        if (result.getContentType() != null) {
            // Sets correct encoding for Content-Type.
            String contentType = result.getContentType() + ";" + HttpHeaders.Values.CHARSET + "=" + result.getCharset();
            response.headers().add(HttpHeaders.Names.CONTENT_TYPE, contentType);
        }


        // possibly
        ResponseStreamsNetty responseStreamsNetty = new ResponseStreamsNetty();
        responseStreamsNetty.init(ctx, response, charset);

        return responseStreamsNetty;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (reader != null) {
            throw new IllegalStateException("Illegal attempt to call getInputStream() after getReader() has already been called.");
        }

        if (inputStream == null) {
            inputStream = new ByteBufInputStream(request.data());
        }
        return inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (inputStream != null) {
            throw new IllegalStateException("Illegal attempt to call getReader() after getInputStream() has already been called.");
        }

        if (reader == null) {
            String charset = getRequestCharacterEncoding();
            if (charset == null) {
                charset = NinjaConstant.UTF_8;
            }
            reader = new BufferedReader(new InputStreamReader(new ByteBufInputStream(request.data()), charset));
        }
        return reader;
    }

    @Override
    public FileItemIterator getFileItemIterator() {
        try {
            final HttpPostRequestDecoder multipart = new HttpPostRequestDecoder(request);

            return new FileItemIterator() {
                @Override
                public boolean hasNext() throws FileUploadException, IOException {
                    try {
                        return multipart.hasNext();
                    } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
                        return false;
                    }
                }

                @Override
                public FileItemStream next() throws FileUploadException, IOException {
                    final InterfaceHttpData item;
                    try {
                        item = multipart.next();
                    } catch (HttpPostRequestDecoder.EndOfDataDecoderException e) {
                        throw new NoSuchElementException();
                    }
                    return new FileItemStream() {
                        @Override
                        public InputStream openStream() throws IOException {
                            if (item.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                                return new ByteBufInputStream(((FileUpload) item).data());
                            }
                            return null; // XXX: what to do here exactly?
                        }

                        @Override
                        public String getContentType() {
                            if (item.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                                return ((FileUpload) item).getContentType();
                            }
                            return null; // XXX: what to do here exactly?
                        }

                        @Override
                        public String getName() {
                            if (item.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                                return ((FileUpload) item).getFilename();
                            }
                            return null; // XXX: what to do here exactly?
                        }

                        @Override
                        public String getFieldName() {
                            if (item.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                                return ((FileUpload) item).getName();
                            }
                            return null; // XXX: what to do here exactly?
                        }

                        @Override
                        public boolean isFormField() {
                            return item.getHttpDataType() != InterfaceHttpData.HttpDataType.FileUpload;
                        }

                        @Override
                        public FileItemHeaders getHeaders() {
                            if (item.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                                return new FileItemHeaders() {
                                    @Override
                                    public String getHeader(String name) {
                                        if (HttpHeaders.Names.CONTENT_TYPE.equalsIgnoreCase(name)) {
                                            return ((FileUpload) item).getContentType();
                                        }
                                        if (HttpHeaders.Names.CONTENT_TYPE.equalsIgnoreCase(name)) {
                                            return ((FileUpload) item).getContentType();
                                        }
                                        return null;
                                    }

                                    @Override
                                    public Iterator getHeaders(String name) {
                                        if (HttpHeaders.Names.CONTENT_TYPE.equalsIgnoreCase(name)) {
                                            return Iterators.singletonIterator(((FileUpload) item).getContentType());
                                        }
                                        if (HttpHeaders.Names.CONTENT_TYPE.equalsIgnoreCase(name)) {
                                            return Iterators.singletonIterator(((FileUpload) item).getContentType());
                                        }
                                        return Iterators.emptyIterator();
                                    }

                                    @Override
                                    public Iterator getHeaderNames() {
                                        return Iterators.forArray(
                                                HttpHeaders.Names.CONTENT_TYPE,
                                                HttpHeaders.Names.CONTENT_TRANSFER_ENCODING
                                        );
                                    }
                                };
                            }
                            return null; // XXX: what to do here exactly?
                        }

                        @Override
                        public void setHeaders(FileItemHeaders headers) {
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            };
        } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            logger.error("Error while trying to process multipart file upload", e);
            return null;
        } catch (HttpPostRequestDecoder.IncompatibleDataDecoderException e) {
            logger.error("Error while trying to process multipart file upload", e);
            return null;
        }
    }

    @Override
    public String getMethod() {
        return request.getMethod().name();
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
}
