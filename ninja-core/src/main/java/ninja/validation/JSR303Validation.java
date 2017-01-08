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

package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the annotated element is conform to its JSR303-Annotations
 *
 * @author psommer
 */
@WithValidator(Validators.JSRValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface JSR303Validation {

    public static final String KEY = "validation.is.JSR303.violation";
    public static final String MESSAGE = "{0} cannot be validated with JSR303 annotations";

    /**
     * The key for the violation message
     *
     * @return The key of the violation message
     */
    String key() default KEY;

    /**
     * Default message if the field isn't found
     *
     * @return The default message
     */
    String message() default MESSAGE;
}
