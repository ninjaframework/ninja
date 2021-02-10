/**
 * Copyright (C) the original author or authors.
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Parameter in a Route.
 * 
 * @author Joe Lauer
 */
public class RouteParameter {
    
    // eg. {id: [0-9]+}
    private final int index;    // index of where token starts
    private final String token; // "{id: [0-9]+}"
    private final String name;  // "id"
    private final String regex; // "[0-9]+"

    public RouteParameter(int index, String token, String name, String regex) {
        this.index = index;
        this.token = token;
        this.name = name;
        this.regex = regex;
    }

    /**
     * Gets the index of where the token starts in the original uri.
     * @return An index of where the token is
     */
    public int getIndex() {
        return index;
    }

    /**
     * The exact string of the parameter such as "{id: [0-9]+}" in "{id: [0-9]+}"
     * @return The parameter token
     */
    public String getToken() {
        return token;
    }
    
    /**
     * The name of the parameter such as "id" in "{id: [0-9]+}"
     * @return The name of the parameter
     */
    public String getName() {
        return name;
    }

    /**
     * The regex of the parameter such as "[0-9]+" in "{id: [0-9]+}"
     * @return The regex of the parameter or null if no regex was included
     *      for the parameter.
     */
    public String getRegex() {
        return regex;
    }
    
    /**
     * Parse a path such as "/user/{id: [0-9]+}/email/{addr}" for the named
     * parameters.
     * @param path The path to parse
     * @return A map containing the named parameters in the order they were
     *      parsed or null if no parameters were parsed.
     */
    static public Map<String, RouteParameter> parse(String path) {
        Map<String,RouteParameter> params = new LinkedHashMap<>();
        
        // extract any named parameters
        Matcher matcher = Route.PATTERN_FOR_VARIABLE_PARTS_OF_ROUTE.matcher(path);
        while (matcher.find()) {
            RouteParameter param = new RouteParameter(
                matcher.start(0), matcher.group(0), matcher.group(1), matcher.group(3));
            params.put(param.getName(), param);
        }
        
        return params;
    }
    
}
