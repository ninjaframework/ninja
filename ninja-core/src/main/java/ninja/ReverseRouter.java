/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import ninja.ControllerMethods.ControllerMethod;
import ninja.ReverseRouter.Builder;
import ninja.utils.LambdaRoute;
import ninja.utils.MethodReference;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Reverse routing. Lookup the uri associated with a controller method.
 * 
 * @author Joe Lauer (jjlauer)
 */
public class ReverseRouter implements WithControllerMethod<Builder> {
    static private final Logger log = LoggerFactory.getLogger(ReverseRouter.class);
    
    static public class Builder {
        
        private final String contextPath;
        private final Route route;
        private String scheme;
        private String hostname;
        private Map<String,String> pathParams;
        private Map<String,String> queryParams;
        
        public Builder(String contextPath, Route route) {
            this.contextPath = contextPath;
            this.route = route;
        }

        public Builder scheme(String scheme) {
            this.scheme = scheme;
            return this;
        }
        
        /**
         * Make this an absolute route by including the current scheme (e.g. http)
         * and hostname (e.g. www.example.com).
         * @param scheme The scheme such as "http" or "https"
         * @param hostname The hostname such as "www.example.com" or "www.example.com:8080"
         * @return This builder
         */
        public Builder absolute(String scheme, String hostname) {
            this.scheme = scheme;
            this.hostname = hostname;
            return this;
        }
        
        /**
         * Make this an absolute route by including the current scheme (e.g. http)
         * and hostname (e.g. www.example.com).  If the route is to a websocket
         * then this will then return "ws" or "wss" if TLS is detected.
         * @param context The current context
         * @return This builder
         */
        public Builder absolute(Context context) {
            String s = context.getScheme();
            String h = context.getHostname();
            
            if (this.route.isHttpMethodWebSocket()) {
                if ("https".equalsIgnoreCase(s)) {
                    s = "wss";
                } else {
                    s = "ws";
                }
            }
            
            return this.absolute(s, h);
        }
        
        public Route getRoute() {
            return route;
        }

        public Map<String, String> getPathParams() {
            return pathParams;
        }

        public Map<String, String> getQueryParams() {
            return queryParams;
        }
        
        /**
         * Add a parameter as a path replacement. Will validate the path parameter
         * exists. This method will URL encode the values when building the final
         * result.
         * @param name The path parameter name
         * @param value The path parameter value
         * @return A reference to this builder
         * @see #rawPathParam(java.lang.String, java.lang.Object) 
         */
        public Builder pathParam(String name, Object value) {
            return setPathParam(name, value, false);
        }
        
        /**
         * Identical to <code>path</code> except the path parameter value will
         * NOT be url encoded when building the final url.
         * @param name The path parameter name
         * @param value The path parameter value
         * @return A reference to this builder
         * @see #pathParam(java.lang.String, java.lang.Object) 
         */
        public Builder rawPathParam(String name, Object value) {
            return setPathParam(name, value, true);
        }
        
        private Builder setPathParam(String name, Object value, boolean raw) {
            Objects.requireNonNull(name, "name required");
            Objects.requireNonNull(value, "value required");

            if (route.getParameters() == null || !route.getParameters().containsKey(name)) {
                throw new IllegalArgumentException("Reverse route " + route.getUri()
                    + " does not have a path parameter '" + name + "'");
            }
            
            if (this.pathParams == null) {
                this.pathParams = new LinkedHashMap<>();
            }
            
            this.pathParams.put(name, safeValue(value, raw));
            
            return this;
        }
        
        /**
         * Add a parameter as a queryParam string value. This method will URL encode
         * the values when building the final result.
         * @param name The queryParam string parameter name
         * @param value The queryParam string parameter value
         * @return A reference to this builder
         * @see #rawQueryParam(java.lang.String, java.lang.Object) 
         */
        public Builder queryParam(String name, Object value) {
            return setQueryParam(name, value, false);
        }
        
        /**
         * Identical to <code>queryParam</code> except the queryParam string value will
         * NOT be url encoded when building the final url.
         * @param name The queryParam string parameter name
         * @param value The queryParam string parameter value
         * @return A reference to this builder
         * @see #queryParam(java.lang.String, java.lang.Object) 
         */
        public Builder rawQueryParam(String name, Object value) {
            return setQueryParam(name, value, true);
        }
        
        private Builder setQueryParam(String name, Object value, boolean raw) {
            Objects.requireNonNull(name, "name required");

            if (this.queryParams == null) {
                // retain ordering
                this.queryParams = new LinkedHashMap<>();
            }
            
            this.queryParams.put(name, safeValue(value, raw));
            
            return this;
        }
        
        private String safeValue(Object value, boolean raw) {
            String s = (value == null ? null : value.toString());
            if (!raw && s != null) {
                try {
                    s = URLEncoder.encode(s, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(e);
                }
            }
            return s;
        }
        
        private int safeMapSize(Map map) {
            return (map != null ? map.size() : 0);
        }
        
        /**
         * Builds the final url. Will validate expected parameters match actual.
         * @return The final resulting url
         */
        public String build() {
            // number of pathOrQueryParams valid?
            int expectedParamSize = safeMapSize(this.route.getParameters());
            int actualParamSize = safeMapSize(this.pathParams);
            if (expectedParamSize != actualParamSize) {
                throw new IllegalArgumentException("Reverse route " + route.getUri()
                    + " requires " + expectedParamSize + " parameters but got "
                    + actualParamSize + " instead");
            }
            
            String rawUri = this.route.getUri();
            
            StringBuilder buffer = new StringBuilder(rawUri.length());

            // append scheme + hostname?
            if (this.scheme != null && this.hostname != null) {
                buffer.append(this.scheme);
                buffer.append("://");
                buffer.append(this.hostname);
            }
            
            // append contextPath
            if (this.contextPath != null && this.contextPath.length() > 0) {
                buffer.append(this.contextPath);
            }
            
            // replace path parameters
            int lastIndex = 0;
            
            if (this.pathParams != null) {
                for (RouteParameter rp : this.route.getParameters().values()) {
                    String value = this.pathParams.get(rp.getName());
                    
                    if (value == null) {
                        throw new IllegalArgumentException("Reverse route " + route.getUri()
                            + " missing value for path parameter '" + rp.getName() + "'");
                    }
                    
                    // append any text before this token
                    buffer.append(rawUri.substring(lastIndex, rp.getIndex()));
                    // append value
                    buffer.append(value);
                    // the next index to start from
                    lastIndex = rp.getIndex() + rp.getToken().length();
                }
            }
            
            // append whatever remains
            if (lastIndex < rawUri.length()) {
                buffer.append(rawUri.substring(lastIndex));
            }
            
            // append queryParam pathOrQueryParams
            if (this.queryParams != null) {
                int i = 0;
                for (Map.Entry<String,String> entry : this.queryParams.entrySet()) {
                    buffer.append((i == 0 ? '?' : '&'));
                    buffer.append(entry.getKey());
                    if (entry.getValue() != null) {
                        buffer.append('=');
                        buffer.append(entry.getValue());
                    }
                    i++;
                }
            }
            
            return buffer.toString();
        }
        
        /**
         * Builds the result as a <code>ninja.Result</code> redirect.
         * @return A Ninja redirect result
         */
        public Result redirect() {
            return Results.redirect(build());
        }

        @Override
        public String toString() {
            return this.build();
        }
    }
    
    private final NinjaProperties ninjaProperties;
    private final Router router;
    
    @Inject
    public ReverseRouter(NinjaProperties ninjaProperties,
                         Router router) {
        this.ninjaProperties = ninjaProperties;
        this.router = router;
    }
    
    /**
     * Retrieves a the reverse route for this controllerClass and method.
     * 
     * @param controllerClass The controllerClass e.g. ApplicationController.class
     * @param methodName the methodName of the class e.g. "index"
     * @return A <code>Builder</code> allowing setting path placeholders and
      queryParam string parameters.
     */
    public Builder with(Class<?> controllerClass, String methodName) {
        return builder(controllerClass, methodName);
    }
    
    /**
     * Retrieves a the reverse route for the method reference (e.g. controller
     * class and method name).
     * 
     * @param methodRef The reference to a method
     * @return A <code>Builder</code> allowing setting path placeholders and
      queryParam string parameters.
     */
    public Builder with(MethodReference methodRef) {
        return builder(methodRef.getDeclaringClass(), methodRef.getMethodName());
    }
    
    /**
     * Retrieves a the reverse route for a method referenced with Java-8
     * lambdas (functional method references).
     * 
     * @param controllerMethod The Java-8 style method reference such as
     *      <code>ApplicationController::index</code>.
     * @return A <code>Builder</code> allowing setting path placeholders and
      queryParam string parameters.
     */
    @Override
    public Builder with(ControllerMethod controllerMethod) {
        LambdaRoute lambdaRoute = LambdaRoute.resolve(controllerMethod);
        
        // only need the functional method for the reverse lookup
        Method method = lambdaRoute.getFunctionalMethod();
        
        return builder(method.getDeclaringClass(), method.getName());
    }
    
    private Builder builder(Class<?> controllerClass, String methodName) {
        Optional<Route> route = this.router.getRouteForControllerClassAndMethod(
            controllerClass, methodName);
        
        if (route.isPresent()) {
            return new Builder(this.ninjaProperties.getContextPath(), route.get());
        }
        
        throw new IllegalArgumentException("Reverse route not found for " +
            controllerClass.getCanonicalName() + "." + methodName);
    }
    
}
