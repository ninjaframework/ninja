/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.utils;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.params.ParamParsers;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.validation.Validation;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import ninja.ContentTypes;
import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Route;

import javax.print.attribute.standard.Media;

/**
 * Abstract Context.Impl that implements features that are not reliant
 * on the concrete Context implementation.  For example, a concrete implementation
 * may have to provide a <code>getHeader()</code> method, but this class could
 * supply a default implementation of <code>getAcceptContentType()</code> since
 * it only needs to fetch a value from <code>getHeader()</code>.
 * 
 * When adding features to a <code>Context</code> please think about whether
 * it should be fully or partially implemented here or in the concrete implementation.
 */
abstract public class AbstractContext implements Context.Impl {

    private static final Splitter splitter = Splitter.on(',')
            .trimResults()
            .omitEmptyStrings();
    static final private Logger logger = LoggerFactory.getLogger(AbstractContext.class);

    // subclasses need to access these
    final protected BodyParserEngineManager bodyParserEngineManager;
    final protected FlashScope flashScope;
    final protected NinjaProperties ninjaProperties;
    final protected Session session;
    final protected Validation validation;
    final protected Injector injector;

    final protected ParamParsers paramParsers;

    protected Route route;
    // in async mode these values will be set to null so its critical they
    // are saved when a context is initialized
    private String requestPath;
    private String contextPath;

    @Inject
    public AbstractContext(
            BodyParserEngineManager bodyParserEngineManager,
            FlashScope flashScope,
            NinjaProperties ninjaProperties,
            Session session,
            Validation validation,
            Injector injector,
            ParamParsers paramParsers) {
        this.bodyParserEngineManager = bodyParserEngineManager;
        this.flashScope = flashScope;
        this.ninjaProperties = ninjaProperties;
        this.session = session;
        this.validation = validation;
        this.injector = injector;
        this.paramParsers = paramParsers;
    }

    protected void init(String contextPath, String requestPath) {
        this.contextPath = contextPath;
        this.requestPath = requestPath;
        
        // init flash scope:
        flashScope.init(this);

        // init session scope:
        session.init(this);
    }

    @Override
    public void setRoute(Route route) {
        this.route = route;
    }
    
    @Override
    public Route getRoute() {
        return route;
    }
    
    @Override
    public Validation getValidation() {
        return validation;
    }
    
    @Deprecated
    @Override
    public FlashScope getFlashCookie() {
        return flashScope;
    }
    
    @Override
    public FlashScope getFlashScope() {
        return flashScope;
    }

    @Deprecated
    @Override
    public Session getSessionCookie() {
        return session;
    }
    
    @Override
    public Session getSession() {
        return session;
    }
    
    @Override
    public String getContextPath() {
        return contextPath;
    }
    
    @Override
    public String getRequestPath() {
        return requestPath;
    }
    
    /**
     * Get the "real" address of the client connection.  Does not take any
     * header (e.g. X-Forwarded-For) into account.
     * @return The real address of the client connection
     */
    abstract protected String getRealRemoteAddr();

    @Override
    public String getRemoteAddr() {
        boolean isUsageOfXForwardedHeaderEnabled 
                = ninjaProperties.getBooleanWithDefault(
                        Context.NINJA_PROPERTIES_X_FORWARDED_FOR, false);
        
        if (isUsageOfXForwardedHeaderEnabled) {
            
            String forwardHeader = getHeader(X_FORWARD_HEADER);

            if (forwardHeader != null) {
                if (forwardHeader.contains(",")) {
                    // sometimes the header is of form client ip,proxy 1 ip,proxy 2 ip,...,proxy n ip,
                    // we just want the client
                    forwardHeader = StringUtils.split(forwardHeader, ',')[0].trim();
                }
                try {
                    // If ip4/6 address string handed over, simply does pattern validation.
                    InetAddress.getByName(forwardHeader);
                    return forwardHeader;
                } catch (UnknownHostException e) {
                    // give up
                }
            }
        }
        
        // fallback to the real remote address
        return getRealRemoteAddr();
    }
    
    @Override
    public <T> T getAttribute(String name, Class<T> clazz) {
        return clazz.cast(getAttribute(name));
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

        T value = (T) paramParsers.getParamParser(clazz).parseParameter(key, parameter, validation);
        return validation.hasViolation(key) ? defaultValue : value;
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
            logger.debug("No BodyParserEngine found for Content-Type {} at route {}", contentTypeOnly, getRequestPath());
            return null;
        }

        return bodyParserEngine.invoke(this, classOfT);

    }

    @Override
    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        
        if (cookie == null) {
            return null;
        }
        
        return cookie.getValue();
    }
    
    @Override
    public void unsetCookie(Cookie cookie) {
        addCookie(Cookie.builder(cookie).setMaxAge(0).build());
    }
    
    @Override
    public void asyncRequestComplete() {
        returnResultAsync(null);
    }

    protected ResponseStreams finalizeHeaders(Result result, Boolean handleFlashAndSessionCookie) {
        // copy ninja flash and session data directory to this context
        if (handleFlashAndSessionCookie) {
            flashScope.save(this);
            session.save(this);
        }
        
        // copy any cookies from result
        for (ninja.Cookie cookie : result.getCookies()) {
            addCookie(cookie);
        }
        
        // subclasses responsible for creating the ResponseStreams instance
        return null;
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
    public String getAcceptContentType() {
        String contentType = getHeader("accept");

        if (Strings.isNullOrEmpty(contentType)) {
            return Result.TEXT_HTML;
        }

        String bestMatch = splitter.splitToList(contentType).stream()
                .map(MediaRange::new)
                .filter(mediaRange -> mediaRange.weight != 0.0)
                .sorted()
                .findFirst()
                .flatMap(mediaRange -> Optional.of(mediaRange.mediaRange))
                .orElse("text/html"); // fall back to text/html, if there are no good media ranges

        if (bestMatch.contains("application/xhtml")
                || bestMatch.contains("text/html")
                || bestMatch.startsWith("*/*")) {
            return Result.TEXT_HTML;
        }

        if (bestMatch.contains("application/xml")
                || bestMatch.contains("text/xml")) {
            return Result.APPLICATION_XML;
        }

        if (bestMatch.contains("application/json")
                || bestMatch.contains("text/javascript")) {
            return Result.APPLICATION_JSON;
        }

        if (bestMatch.contains("text/plain")) {
            return Result.TEXT_PLAIN;
        }

        if (bestMatch.contains("application/octet-stream")) {
            return Result.APPLICATION_OCTET_STREAM;
        }

        if (bestMatch.endsWith("*/*")) {
            return Result.TEXT_HTML;
        }

        return bestMatch;
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

    private static final class MediaRange implements Comparable {
        @Override
        public int compareTo(Object o) {
            MediaRange other = (MediaRange) o;

            // Compare the ranges for specificity
            if (this.extensions.size() < other.extensions.size()) {
                // this media range is more specific and should be assigned higher priority
                return 1;
            } else if (this.extensions.size() > other.extensions.size()) {
                // the other range is more specific and should be used
                return -1;
            }

            if (this.extensions.size() == other.extensions.size()) {
                // these two ranges are equally specific and the weights are the deciding factor
                return Double.compare(other.weight, this.weight);
            }
            return 0;
        }

        private static final Splitter splitter = Splitter.on(';').trimResults();

        private String mediaRange;

        private double weight;
        private List<String> extensions;

        public MediaRange(String value) {
            List<String> parts = splitter.splitToList(value);
            this.mediaRange = parts.get(0);
            Optional<String> weight = parts.stream().filter(part -> part.startsWith("q=")).findFirst();
            this.weight = Double.parseDouble(weight.orElse("q=1").replace("q=", ""));
            this.extensions = parts.stream().filter(part -> !part.startsWith("q=")).filter(part -> part.contains("=")).collect(Collectors.toList());
        }
    }
}
