/**
 * Copyright (C) 2012-2014 the original author or authors.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

/**
 * A route
 */
public class Route {

    //Matches: {id} AND {id: .*?}
    // group(1) extracts the name of the group (in that case "id").
    // group(3) extracts the regex if defined
    final static Pattern PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE 
        = Pattern.compile("\\{(.*?)(:\\s(.*?))?\\}");

    /**
     * This regex matches everything in between path slashes.
     */
    final static String VARIABLE_ROUTES_DEFAULT_REGEX = "([^/]*)";
    
    //private static final String PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE = "\\{.*?:\\s(.*?)\\}";
    private final String httpMethod;
    private final String uri;
    private final Class controllerClass;
    private final Method controllerMethod;
    private final FilterChain filterChain;

    private final List<String> parameterNames;
    private final Pattern regex;

    public Route(String httpMethod,
            String uri,
            Class controllerClass,
            Method controllerMethod,
            FilterChain filterChain) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.controllerClass = controllerClass;
        this.controllerMethod = controllerMethod;
        this.filterChain = filterChain;

        parameterNames = ImmutableList.copyOf(doParseParameters(uri));
        regex = Pattern.compile(convertRawUriToRegex(uri));
    }

    public String getUrl() {
        return uri;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

    public Method getControllerMethod() {
        return controllerMethod;
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
            for (int i = 1; i < m.groupCount() + 1; i++) {
                map.put(parameterNames.get(i - 1), m.group(i));
            }
        }

        return map;

    }

    /**
     *
     * Extracts the name of the parameters from a route
     *
     * /{my_id}/{my_name}
     *
     * would return a List with "my_id" and "my_name"
     *
     * @param rawRoute
     * @return a list with the names of all parameters in that route.
     */
    private static List<String> doParseParameters(String rawRoute) {
        List<String> list = new ArrayList<String>();

        Matcher m = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(rawRoute);

        while (m.find()) {
            // group(1) is the name of the group. Must be always there...
            // "/assets/{file}" and "/assets/{file: [a-zA-Z][a-zA-Z_0-9]}" 
            // will return file.
            list.add(m.group(1));
        }

        return list;
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

        Matcher matcher = PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(rawUri);

        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {

            // By convention group 3 is the regex if provided by the user.
            // If it is not provided by the user the group 3 is null.
            String namedVariablePartOfRoute = matcher.group(3);
            String namedVariablePartOfORouteReplacedWithRegex;
            
            if (namedVariablePartOfRoute != null) {
                // we convert that into a regex matcher group itself
                namedVariablePartOfORouteReplacedWithRegex 
                    = "(" + namedVariablePartOfRoute + ")";
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
