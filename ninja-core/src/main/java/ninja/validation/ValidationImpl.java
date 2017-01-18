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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Validation object
 *
 * @author James Roper
 * @author Philip Sommer
 * @author Jonathan Lannoy
 */
public class ValidationImpl implements Validation {

    private final Map<String, List<FieldViolation>> fieldViolations = Maps.newHashMap();
    private final List<ConstraintViolation> generalViolations = Lists.newArrayList();
    private final Map<String, List<FieldViolation>> beanViolations = Maps.newHashMap();

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
            if(!this.fieldViolations.containsKey(fieldViolation.field)) {
                this.fieldViolations.put(fieldViolation.field, Lists.<FieldViolation>newArrayList());
            }
            this.fieldViolations.get(fieldViolation.field).add(fieldViolation);
        }
    }

    @Override
    public void addFieldViolation(String field, ConstraintViolation constraintViolation) {
        addFieldViolation(new FieldViolation(field, constraintViolation));
    }

    @Override
    public boolean hasFieldViolation(String field) {
        return this.fieldViolations.containsKey(field);
    }

    @Override
    public List<FieldViolation> getFieldViolations() {
        List<FieldViolation> sumViolations = Lists.newArrayList();
        for(List<FieldViolation> fieldViolation : this.fieldViolations.values()) {
            sumViolations.addAll(fieldViolation);
        }
        return sumViolations;
    }

    @Override
    public List<FieldViolation> getFieldViolations(String field) {
        return this.fieldViolations.get(field);
    }

    @Override
    public void addBeanViolation(FieldViolation fieldViolation) {
        if(!this.beanViolations.containsKey(fieldViolation.field)) {
            this.beanViolations.put(fieldViolation.field, Lists.<FieldViolation>newArrayList());
        }
        this.beanViolations.get(fieldViolation.field).add(fieldViolation);
    }

    @Override
    public boolean hasBeanViolation(String field) {
        return this.beanViolations.containsKey(field);
    }

    @Override
    public boolean hasBeanViolations() {
        return !this.beanViolations.isEmpty();
    }

    @Override
    public List<FieldViolation> getBeanViolations() {
        List<FieldViolation> sumViolations = Lists.newArrayList();
        for(List<FieldViolation> fieldViolation : this.beanViolations.values()) {
            sumViolations.addAll(fieldViolation);
        }
        return sumViolations;
    }
    
    @Override
    public List<FieldViolation> getBeanViolations(String field) {
        return this.beanViolations.get(field);
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
