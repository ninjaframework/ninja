/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.jaxy;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author James Moger
 *
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpMethod {

    /**
     * HTTP GET method.
     */
    public static final String GET = "GET";
    /**
     * HTTP PATCH method.
     */
    public static final String PATCH = "PATCH";
    /**
     * HTTP POST method.
     */
    public static final String POST = "POST";
    /**
     * HTTP PUT method.
     */
    public static final String PUT = "PUT";
    /**
     * HTTP DELETE method.
     */
    public static final String DELETE = "DELETE";
    /**
     * HTTP HEAD method.
     */
    public static final String HEAD = "HEAD";
    /**
     * HTTP OPTIONS method.
     */
    public static final String OPTIONS = "OPTIONS";

    /**
     * Specifies the name of a HTTP method. E.g. "GET".
     */
    String value();
}