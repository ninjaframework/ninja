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

import java.util.ArrayList;
import java.util.List;

/**
 * Validation object
 *
 * @author James Roper
 * @author Philip Sommer
 */
public class ValidationImpl implements Validation {

    private final List<FieldViolation> fieldViolations = new ArrayList<FieldViolation>();
    private final List<ConstraintViolation> generalViolations = new ArrayList<ConstraintViolation>();
    private final List<FieldViolation> beanViolations = new ArrayList<FieldViolation>();

    @Override
    public boolean hasViolations() {
        return !this.fieldViolations.isEmpty() || !this.generalViolations.isEmpty()
                || !this.beanViolations.isEmpty();
    }

    @Override
    public void addFieldViolation(FieldViolation fieldViolation) {
        if (fieldViolation.field == null) {
            this.generalViolations.add(fieldViolation.constraintViolation);
        } else {
            this.fieldViolations.add(fieldViolation);
        }
    }

    @Override
    public void addFieldViolation(String field, ConstraintViolation constraintViolation) {
        addFieldViolation(new FieldViolation(field, constraintViolation));
    }

    @Override
    public boolean hasFieldViolation(String field) {
        for (FieldViolation fieldViolation : this.fieldViolations) {
            if (fieldViolation.field.contentEquals(field)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<FieldViolation> getFieldViolations() {
        return this.fieldViolations;
    }

    @Override
    public List<FieldViolation> getFieldViolations(String field) {
        List<FieldViolation> violationsForThisField = new ArrayList<FieldViolation>();
        for (FieldViolation fieldViolation : this.fieldViolations) {
            if (fieldViolation.field.contentEquals(field)) {
                violationsForThisField.add(fieldViolation);
            }
        }
        return violationsForThisField;
    }

    @Override
    public void addBeanViolation(FieldViolation fieldViolation) {
        this.beanViolations.add(fieldViolation);
    }

    @Override
    public boolean hasBeanViolation(String field) {
        for (FieldViolation beanViolation : this.beanViolations) {
            if (beanViolation.field.contentEquals(field)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasBeanViolations() {
        return !this.beanViolations.isEmpty();
    }

    @Override
    public List<FieldViolation> getBeanViolations() {
        return this.beanViolations;
    }

    @Override
    public void addViolation(ConstraintViolation constraintViolation) {
        this.generalViolations.add(constraintViolation);
    }

    @Override
    public List<ConstraintViolation> getGeneralViolations() {
        return this.generalViolations;
    }
}
