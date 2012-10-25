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

import java.util.List;

import com.google.inject.ImplementedBy;

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
     * Get a complete list of all field violations. This list DOES NOT contain general violations
     * (use getGeneralViolations() instead).
     * 
     * @return A List of FieldViolation-objects
     */
    List<FieldViolation> getFieldViolations();

    /**
     * Get a complete list of field violations for a specified field. This list DOES NOT contain
     * general violations
     * (use getGeneralViolations() instead).
     * 
     * @return A List of FieldViolation-objects
     */
    List<FieldViolation> getFieldViolations(String fieldName);

    /**
     * Get all general constraint violations. This list does not contain any specific field
     * violation (use getFieldViolations() instead).
     * 
     * @return The list of general violations.
     */
    List<ConstraintViolation> getGeneralViolations();

    /**
     * Add a field violation to the list of filed violations.
     * 
     * @param fieldViolation
     */
    void addFieldViolation(FieldViolation fieldViolation);

    void addBeanViolation(String beanName, FieldViolation fieldViolation);

    boolean hasBeanViolations(String beanName);

    boolean hasBeanViolations();

    List<FieldViolation> getBeanViolations(String beanName);

}
