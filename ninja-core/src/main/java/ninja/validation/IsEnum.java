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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the field is a valid enum constant. Only needed if you want
 * to customise the validation messages.
 *
 * @author James Moger
 */
@WithValidator(Validators.EnumValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IsEnum {
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

    /**
     * The enum class.
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * The flag to determine if validation is case-sensitive.
     *
     * @return The case-sensitivity flag.
     */
    boolean caseSensitive() default true;

    public static final String KEY = "validation.is.enum.violation";
    public static final String MESSAGE = "{0} is not a valid enum constant";
}
