/**
 * Copyright (C) 2012-2015 the original author or authors.
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
 * This interface means the validation context (implemented by {@link ValidationImpl}) and can be injected in your
 * controller method.
 * There are several types of violations that can occur: field violations (on controller method fields), bean violations
 * (on an injected beans field) or general violations (deprecated). A controller using this validation can have
 * violations on his
 * parameters or, if you use a injected data container like a DTO or bean, you may have violations inside this object.
 * Each violation on a field (parameter or in an annotated bean) results in a {@link FieldViolation}. This makes it
 * possible to validate all controller parameters at once. If an error appears while validating the controller
 * method parameters, it results in a violation which you can get using getFieldViolations().
 * If your injected bean contains violations, you should use getBeanViolations().
 * 
 * @author James Roper, Philip Sommer
 */
@ImplementedBy(ValidationImpl.class)
public interface Validation {
    /**
     * Whether the validation context has violations (including field and bean violations)
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
    @Deprecated
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
    @Deprecated
    List<ConstraintViolation> getGeneralViolations();

    /**
     * Add a field violation to the list of filed violations.
     * 
     * @param fieldViolation
     */
    void addFieldViolation(FieldViolation fieldViolation);

    /**
     * Add a bean violation. A bean, like a DTO consists of several fields which are validated. Each
     * validation error of a dto-field results in a field-violation for that bean.
     * Note: For now, you can only have one bean in your controller method signature, so this is explicit.
     * 
     * @param beanName maybe the name of your dto
     * @param fieldViolation the FieldViolation consisting of a cinstraintViolation and the fields
     *            name
     */
    void addBeanViolation(FieldViolation fieldViolation);

    /**
     * Checks if the validation has bean violation.
     * 
     * @param name Name of the bean.
     * @return Whether the named bean has violation.
     */
    boolean hasBeanViolation(String name);

    /**
     * Whether any violation occured while validating your beans
     * Note: For now, you can only have one bean in your controller method signature, so this is explicit.
     * 
     * @return true if there are any, false if none
     */
    boolean hasBeanViolations();

    /**
     * Get all bean validations for that bean.
     * Note: For now, you can only have one bean in your controller method signature, so this is explicit.
     * 
     * @param beanName
     * @return A list of field violations
     */
    List<FieldViolation> getBeanViolations();

}
