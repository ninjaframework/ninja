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

package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the field is a floating point number. Only needed if you want
 * to customise the validation messages.
 * 
 * @author James Roper
 */
@WithValidator(Validators.FloatValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IsFloat {
    /**
     * The key for the violation message
     * 
     * @return The key of the violation message
     */
    String key() default KEY;

    /**
     * Default message if the key isn't found
     * 
     * @return The default message
     */
    String message() default MESSAGE;

    /**
     * The key for formatting the field
     * 
     * @return The key
     */
    String fieldKey() default "";

    public static final String KEY = "validation.is.float.violation";
    public static final String MESSAGE = "{0} must be a decimal number";
}
