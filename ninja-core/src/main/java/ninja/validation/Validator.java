/**
 * Copyright (C) 2012-2016 the original author or authors.
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

package ninja.validation;

import ninja.Context;

/**
 * A validator for validating parameters
 *
 * @author James Roper
 * @author Thibault Meyer
 */
public interface Validator<T> {

    /**
     * Validate the given value
     *
     * @param value   The value, may be null
     * @param field   The name of the field being validated, if applicable
     * @param context The Ninja request context
     */
    void validate(T value, String field, Context context);

    /**
     * Get the type that this validator validates
     *
     * @return The type that the validator validates
     */
    Class<T> getValidatedType();
}
