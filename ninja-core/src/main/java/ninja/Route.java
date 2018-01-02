/**
 * Copyright (C) 2012- the original author or authors.
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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.collect.Maps;
import java.util.Iterator;
import org.apache.commons.lang.StringUtils;

/**
 * A route
 */
public class Route {
    
    static public final String HTTP_METHOD_GET = "GET";
    static public final String HTTP_METHOD_POST = "POST";
    static public final String HTTP_METHOD_PUT = "PUT";
    static public final String HTTP_METHOD_HEAD = "HEAD";
    static public final String HTTP_METHOD_DELETE = "DELETE";
    static public final String HTTP_METHOD_OPTIONS = "OPTIONS";
    static public final String HTTP_METHOD_WEBSOCKET = "WS";

    //Matches: {id} AND {id: .*?}
    // group(1) extracts the name of the group (in that case "id").
    // group(3) extracts the regex if defined
    final static Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE 
        = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

    /**
     * This regex matches everything in between path slashes.
     */
    final static String VARIABLE_ROUTES_DEFAULT_REGEX = "([^/]*)";
    
    private final String httpMethod;
    private final String uri;
    private final Method controllerMethod;
    private final FilterChain filterChain;
    private final Map<String,RouteParameter> parameters;
    private final Pattern regex;

    public Route(String httpMethod,
            String uri,
            Method controllerMethod,
            FilterChain filterChain) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.controllerMethod = controllerMethod;
        this.filterChain = filterChain;
        this.parameters = RouteParameter.parse(uri);
        this.regex = Pattern.compile(convertRawUriToRegex(uri));
    }

    /**
     * @deprecated Use getUri()
     */
    public String getUrl() {
        return uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public boolean isHttpMethod(String method) {
        return StringUtils.equalsIgnoreCase(method, this.httpMethod);
    }
    
    public boolean isHttpMethodGet() {
        return this.isHttpMethod(Route.HTTP_METHOD_GET);
    }
    
    public boolean isHttpMethodPost() {
        return this.isHttpMethod(Route.HTTP_METHOD_POST);
    }
    
    public boolean isHttpMethodPut() {
        return this.isHttpMethod(Route.HTTP_METHOD_PUT);
    }
    
    public boolean isHttpMethodDelete() {
        return this.isHttpMethod(Route.HTTP_METHOD_DELETE);
    }
    
    public boolean isHttpMethodHead() {
        return this.isHttpMethod(Route.HTTP_METHOD_HEAD);
    }
    
    public boolean isHttpMethodOptions() {
        return this.isHttpMethod(Route.HTTP_METHOD_OPTIONS);
    }
    
    public boolean isHttpMethodWebSocket() {
        return this.isHttpMethod(Route.HTTP_METHOD_WEBSOCKET);
    }
    
    public String getUri() {
        return uri;
    }

    public Class<?> getControllerClass() {
        return controllerMethod != null ? controllerMethod.getDeclaringClass() : null;
    }

    public Method getControllerMethod() {
        return controllerMethod;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }
    
    public Map<String,RouteParameter> getParameters() {
        return parameters;
    }
    
    /**
     * Matches /index to /index or /me/1 to /person/{id}
     *
     * @return True if the actual route matches a raw rout. False if not.
     *
     */
    public boolean matches(String httpMethod, String uri) {
        if (this.httpMethod.equalsIgnoreCase(httpMethod)) {
            Matcher matcher = regex.matcher(uri);
            return matcher.matches();
        } else {
            return false;
        }
    }

    /**
     * This method does not do any decoding / encoding.
     *
     * If you want to decode you have to do it yourself.
     *
     * Most likely with:
     * http://docs.oracle.com/javase/6/docs/api/java/net/URI.html
     *
     * @param uri The whole encoded uri.
     * @return A map with all parameters of that uri. Encoded in => encoded out.
     */
    public Map<String, String> getPathParametersEncoded(String uri) {
        Map<String, String> map = Maps.newHashMap();

        Matcher m = regex.matcher(uri);

        if (m.matches()) {
            Iterator<String> it = this.parameters.keySet().iterator();
            for (int i = 1; i < m.groupCount() + 1; i++) {
                String parameterName = it.next();
                map.put(parameterName, m.group(i));
            }
        }
        
        return map;
    }

    /**
     * Gets a raw uri like "/{name}/id/*" and returns "/([^/]*)/id/*."
     *
     * Also handles regular expressions if defined inside routes:
     * For instance "/users/{username: [a-zA-Z][a-zA-Z_0-9]}" becomes
     * "/users/([a-zA-Z][a-zA-Z_0-9])"
     *
     * @return The converted regex with default matching regex - or the regex
     *          specified by the user.
     */
    protected static String convertRawUriToRegex(String rawUri) {

        // convert capturing groups in route regex to non-capturing groups
        // this is to avoid count mismatch of path params and groups in uri regex
        Matcher groupMatcher = Pattern.compile("\\(([^?].*)\\)").matcher(rawUri);
        String converted = groupMatcher.replaceAll("\\(?:$1\\)");

        Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(converted);

        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {

            // By convention group 3 is the regex if provided by the user.
            // If it is not provided by the user the group 3 is null.
            String namedVariablePartOfRoute = matcher.group(3);
            String namedVariablePartOfORouteReplacedWithRegex;
            
            if (namedVariablePartOfRoute != null) {
                // we convert that into a regex matcher group itself
                namedVariablePartOfORouteReplacedWithRegex 
                    = "(" + Matcher.quoteReplacement(namedVariablePartOfRoute) + ")";
            } else {
                // we convert that into the default namedVariablePartOfRoute regex group
                namedVariablePartOfORouteReplacedWithRegex 
                    = VARIABLE_ROUTES_DEFAULT_REGEX;
            }
            // we replace the current namedVariablePartOfRoute group
            matcher.appendReplacement(stringBuffer, namedVariablePartOfORouteReplacedWithRegex);

        }

        // .. and we append the tail to complete the stringBuffer
        matcher.appendTail(stringBuffer);

        return stringBuffer.toString();
    }
}
