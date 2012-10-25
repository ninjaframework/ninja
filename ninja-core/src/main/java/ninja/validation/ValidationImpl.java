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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validation object
 * 
 * @author James Roper, Philip Sommer
 */
public class ValidationImpl implements Validation {

    private final List<FieldViolation> fieldViolations = new ArrayList<FieldViolation>();
    private final List<ConstraintViolation> generalViolations = new ArrayList<ConstraintViolation>();
    private final Map<String, List<FieldViolation>> beanViolations =
            new HashMap<String, List<FieldViolation>>();

    @Override
    public boolean hasViolations() {
        return !fieldViolations.isEmpty() || !generalViolations.isEmpty()
                || !beanViolations.isEmpty();
    }

    @Override
    public void addFieldViolation(FieldViolation fieldViolation) {
        if (fieldViolation.field == null) {
            generalViolations.add(fieldViolation.constraintViolation);
        } else {
            fieldViolations.add(fieldViolation);
        }
    }

    @Override
    public void addFieldViolation(String field, ConstraintViolation constraintViolation) {
        addFieldViolation(new FieldViolation(field, constraintViolation));
    }

    @Override
    public boolean hasFieldViolation(String field) {
        for (FieldViolation fieldViolation : fieldViolations) {
            if (fieldViolation.field.contentEquals(field)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<FieldViolation> getFieldViolations() {
        return fieldViolations;
    }

    @Override
    public List<FieldViolation> getFieldViolations(String field) {
        List<FieldViolation> violationsForThisField = new ArrayList<FieldViolation>();
        for (FieldViolation fieldViolation : fieldViolations) {
            if (fieldViolation.field.contentEquals(field)) {
                violationsForThisField.add(fieldViolation);
            }
        }
        return violationsForThisField;
    }

    @Override
    public void addBeanViolation(String beanName, FieldViolation fieldViolation) {
        if (beanViolations.containsKey(beanName)) {
            beanViolations.get(beanName).add(fieldViolation);
        } else {
            List<FieldViolation> temp = new ArrayList<FieldViolation>();
            temp.add(fieldViolation);
            beanViolations.put(beanName, temp);
        }
    }

    @Override
    public boolean hasBeanViolations(String beanName) {
        return beanViolations.containsKey(beanName);
    }

    @Override
    public boolean hasBeanViolations() {
        return beanViolations.isEmpty();
    }

    @Override
    public List<FieldViolation> getBeanViolations(String beanName) {
        return beanViolations.get(beanName);
    }

    @Override
    public void addViolation(ConstraintViolation constraintViolation) {
        generalViolations.add(constraintViolation);
    }

    @Override
    public List<ConstraintViolation> getGeneralViolations() {
        return generalViolations;
    }

}
