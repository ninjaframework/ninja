/**
 * Copyright (C) 2012 the original author or authors.
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

package ninja.params;

import ninja.validation.Validation;

/**
 * Parses a String parameter
 */
public interface ParamParser<T> {
    /**
     * Parse the given parameter value
     *
     * @param field The field that is being parsed
     * @param parameterValue The value to parse.  May be null.
     * @param validation The validation context.
     * @return The parsed parameter value.  May be null.
     */
    T parseParameter(String field, String parameterValue, Validation validation);

    /**
     * Get the type that this parser parses to
     *
     * @return The type
     */
    Class<T> getParsedType();
}
