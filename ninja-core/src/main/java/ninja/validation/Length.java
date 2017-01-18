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
 * Validates that the length of the field meets the given length constraints
 * 
 * @author James Roper
 */
@WithValidator(Validators.LengthValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Length {
    /**
     * The maximum length of the field
     */
    int max() default -1;

    /**
     * The maximum length of the field
     */
    int min() default -1;

    /**
     * The key max violation message
     * 
     * @return The key of the max violation message
     */
    String maxKey() default "validation.length.max.violation";

    /**
     * Default message if max violation message isn't found
     * 
     * @return The default message
     */
    String maxMessage() default "{0} exceeds maximum length of {1}";

    /**
     * The key min violation message
     * 
     * @return The key of the min violation message
     */
    String minKey() default "validation.length.min.violation";

    /**
     * Default message if min violation message isn't found
     * 
     * @return The default message
     */
    String minMessage() default "{0} is less than minimum length of {1}";

    /**
     * The key for formatting the field
     * 
     * @return The key
     */
    String fieldKey() default "";
}
