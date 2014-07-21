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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ninja.Route.HttpMethod;

/**
 * An annotation for specifying a Ninja route.
 *
 * @author James Moger
 *
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouteDef {

    /**
     * The URI(s) for the route.
     *
     * e.g. @RouteDef(uri="/users/{username}") or
     *
     * @RouteDef(uri={"/users/{username}", "/accounts/{username}"})
     *
     * @return the uris
     */
    String[] uri();

    /**
     * The HTTP method for the route. e.g. "GET"
     *
     * @return the http method
     */
    String method() default HttpMethod.GET;

    /**
     * The registration order of the route; a lower number will be registered earlier.
     *
     * @return the order of the route
     */
    int order() default 1;
}
