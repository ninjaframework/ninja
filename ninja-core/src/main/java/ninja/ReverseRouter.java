/*
 * Copyright 2016 ninjaframework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja;

import com.google.common.base.Optional;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import ninja.ControllerMethods.ControllerMethod;
import ninja.ReverseRouter.Builder;
import ninja.utils.LambdaRoute;
import ninja.utils.MethodReference;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        private Map<String,String> paths;
        private Map<String,String> queries;
        
        public Builder(String contextPath, Route route) {
            this.contextPath = contextPath;
            this.route = route;
        }

        public Route getRoute() {
            return route;
        }
        
        /**
         * Add a parameter as a path replacement. Will validate the path parameter
         * exists. This method will URL encode the values when building the final
         * result.
         * @param name The path parameter name
         * @param value The path parameter value
         * @return A reference to this builder
         * @see #rawPath(java.lang.String, java.lang.Object) 
         */
        public Builder path(String name, Object value) {
            return setPath(name, value, false);
        }
        
        /**
         * Identical to <code>path</code> except the path parameter value will
         * NOT be url encoded when building the final url.
         * @param name The path parameter name
         * @param value The path parameter value
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object) 
         */
        public Builder rawPath(String name, Object value) {
            return setPath(name, value, true);
        }
        
        private Builder setPath(String name, Object value, boolean raw) {
            Objects.requireNonNull(name, "name required");
            Objects.requireNonNull(value, "value required");

            if (route.getParameters() == null || !route.getParameters().containsKey(name)) {
                throw new IllegalArgumentException("Reverse route " + route.getUri()
                    + " does not have a path parameter '" + name + "'");
            }
            
            if (this.paths == null) {
                this.paths = new HashMap<>();
            }
            
            this.paths.put(name, safeValue(value, raw));
            
            return this;
        }
        
        /**
         * Add a parameter as a query string value. This method will URL encode
         * the values when building the final result.
         * @param name The query string parameter name
         * @param value The query string parameter value
         * @return A reference to this builder
         * @see #rawQuery(java.lang.String, java.lang.Object) 
         */
        public Builder query(String name, Object value) {
            return setQuery(name, value, false);
        }
        
        /**
         * Identical to <code>query</code> except the query string value will
         * NOT be url encoded when building the final url.
         * @param name The query string parameter name
         * @param value The query string parameter value
         * @return A reference to this builder
         * @see #query(java.lang.String, java.lang.Object) 
         */
        public Builder rawQuery(String name, Object value) {
            return setQuery(name, value, true);
        }
        
        private Builder setQuery(String name, Object value, boolean raw) {
            Objects.requireNonNull(name, "name required");

            if (this.queries == null) {
                // retain ordering
                this.queries = new LinkedHashMap<>();
            }
            
            this.queries.put(name, safeValue(value, raw));
            
            return this;
        }
        
        /**
         * Add a map containing pairs with replacements for either path or query
         * string parameters. This method will URL encode the values when building
         * the final result.  When replacing path parameters it's recommended
         * to use the <code>path</code> method since it will validate the parameter
         * exists, while this method would fallback to adding the value as a query
         * string parameter if the path parameter did not exist.
         * @param parameterMap The map containing pairs of name and values.
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object)
         * @see #query(java.lang.String, java.lang.Object) 
         */
        public Builder params(Map<String,Object> parameterMap) {
            return setParams(parameterMap, false);
        }
        
        /**
         * Identical to <code>params</code> except this method will NOT url
         * encode the values when building the final result.  When replacing path
         * parameters it's recommended to use the <code>path</code> method since
         * it will validate the parameter exists, while this method would fallback
         * to adding the value as a query string parameter if the path parameter
         * did not exist.
         * @param parameterMap The map containing pairs of name and values.
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object)
         * @see #query(java.lang.String, java.lang.Object) 
         */
        public Builder rawParams(Map<String,Object> parameterMap) {
            return setParams(parameterMap, true);
        }
        
        private Builder setParams(Map<String,Object> parameterMap, boolean raw) {
            if (parameterMap != null) {
                parameterMap.forEach((name, value) -> setParam(name, value, raw));
            }
            return this;
        }
        
        /**
         * Add a list containing pairs with replacements for either path or query
         * string parameters. This method will URL encode the values when building
         * the final result.  When replacing path parameters it's recommended
         * to use the <code>path</code> method since it will validate the parameter
         * exists, while this method would fallback to adding the value as a query
         * string parameter if the path parameter did not exist.
         * @param parameterMap The list of pairs of name and values.
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object)
         * @see #query(java.lang.String, java.lang.Object) 
         */
        public Builder params(Object... parameterMap) {
            return setParams(false, parameterMap);
        }
        
        /**
         * Identical to <code>params</code> except this method will NOT url
         * encode the values when building the final result.  When replacing path
         * parameters it's recommended to use the <code>path</code> method since
         * it will validate the parameter exists, while this method would fallback
         * to adding the value as a query string parameter if the path parameter
         * did not exist.
         * @param parameterMap The list of pairs of name and values.
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object)
         * @see #query(java.lang.String, java.lang.Object) 
         */
        public Builder rawParams(Object... parameterMap) {
            return setParams(true, parameterMap);
        }
        
        private Builder setParams(boolean raw, Object... parameterMap) {
            if (parameterMap != null) {
                if (parameterMap.length % 2 != 0) {
                    throw new IllegalArgumentException("Always provide key (as String) value (as Object) pairs in parameterMap. That means providing e.g. 2, 4, 6... objects.");
                }

                for (int i = 0; i < parameterMap.length; i += 2) {
                    setParam((String)parameterMap[i], parameterMap[i+1], raw);
                }
            }
            return this;
        }
        
        /**
         * Add a parameter as a path replacement if it exists or will fallback
         * to adding it a a query string value.  This method will URL encode the
         * values when building the final result.
         * @param name The parameter name
         * @param value The parameter value
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object)
         * @see #query(java.lang.String, java.lang.Object) 
         */
        public Builder param(String name, Object value) {
            return setParam(name, value, false);
        }
        
        /**
         * Identical to <code>param</code> except this method will NOT url
         * encode the values when building the final result.
         * @param name The parameter name
         * @param value The parameter value
         * @return A reference to this builder
         * @see #path(java.lang.String, java.lang.Object)
         * @see #query(java.lang.String, java.lang.Object)
         */
        public Builder rawParam(String name, Object value) {
            return setParam(name, value, true);
        }
        
        private Builder setParam(String name, Object value, boolean raw) {
            Objects.requireNonNull(name, "name required");
            
            if (this.route.getParameters().containsKey(name)) {
                return setPath(name, value, raw);
            } else {
                return setQuery(name, value, raw);
            }
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
            // number of params valid?
            int expectedParamSize = safeMapSize(this.route.getParameters());
            int actualParamSize = safeMapSize(this.paths);
            if (expectedParamSize != actualParamSize) {
                throw new IllegalArgumentException("Reverse route " + route.getUri()
                    + " requires " + expectedParamSize + " parameters but got "
                    + actualParamSize + " instead");
            }
            
            String rawUri = this.route.getUri();
            
            StringBuilder buffer = new StringBuilder(rawUri.length());

            // append contextPath
            if (this.contextPath != null && this.contextPath.length() > 0) {
                buffer.append(this.contextPath);
            }
            
            // replace path parameters
            int lastIndex = 0;
            
            if (this.paths != null) {
                for (RouteParameter rp : this.route.getParameters().values()) {
                    String value = this.paths.get(rp.getName());
                    
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
            
            // append query params
            if (this.queries != null) {
                int i = 0;
                for (Map.Entry<String,String> entry : this.queries.entrySet()) {
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
     *      query string parameters.
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
     *      query string parameters.
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
     *      query string parameters.
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
