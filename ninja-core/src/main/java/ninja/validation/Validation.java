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

package ninja.validation;

import com.google.inject.ImplementedBy;

import java.util.Locale;

/**
 * Validation context
 * 
 * @author James Roper
 */
@ImplementedBy(ValidationImpl.class)
public interface Validation {
    /**
     * Whether the validation context has violations
     * 
     * @return True if it does
     */
    boolean hasViolations();

    /**
     * Whether the validation context has a violation for the given field
     * 
     * @return True if it does
     */
    boolean hasFieldViolation(String field);

    /**
     * Add a violation to the given field
     * 
     * @param field
     *            The field to add the violation to
     * @param constraintViolation
     *            The constraint violation
     */
    void addFieldViolation(String field, ConstraintViolation constraintViolation);

    /**
     * Add a general violation
     * 
     * @param constraintViolation
     *            The constraint violation
     */
    void addViolation(ConstraintViolation constraintViolation);

    /**
     * Get the voilation for the given field
     * 
     * @param field
     *            The field
     * @return The constraint violation, or null if no constraint violation was
     *         found
     */
    ConstraintViolation getFieldViolation(String field);

    /**
     * Get the formatted violation message for the given field
     * 
     * @param field
     *            The field
     * @param locale
     *            The language to get the message
     * @return The message, or null if there was no violation
     */
    String getFieldViolationMessage(String field, String language);

}
