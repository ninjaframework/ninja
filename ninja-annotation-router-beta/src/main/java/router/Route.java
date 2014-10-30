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

package router;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Route annotations allow for automatic configuration of routes.
 * 
 * For example,
 * 
 * <pre>
 * @Route(httpMethod = Route.GET, path = "/index")
 * </pre>
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface Route {

    final static String DELETE = "DELETE";
    final static String GET = "GET";
    final static String HEAD = "HEAD";
    final static String OPTIONS = "OPTIONS"; 
    final static String POST = "POST"; 
    final static String PUT = "PUT";

    String httpMethod() default Route.GET;

    String path() default "";
}
