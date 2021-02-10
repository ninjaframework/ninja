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
    
    private final static String HIBERNATE_VALIDATION_PREFIX = "org.hibernate.";
    
    private final static String JAVAX_VALIDATION_PREFIX = "javax.validation.";

    private final Map<String, List<ConstraintViolation>> violations = Maps.newHashMap();

    @Override
    public boolean hasViolations() {
        return !this.violations.isEmpty();
    }
    
    @Override
    public boolean hasViolation(String paramName) {
        return this.violations.containsKey(paramName);
    }
    
    @Override
    public List<ConstraintViolation> getViolations() {
        List<ConstraintViolation> sumViolations = Lists.newArrayList();
        for(List<ConstraintViolation> fieldViolation : this.violations.values()) {
            sumViolations.addAll(fieldViolation);
        }
        return sumViolations;
    }
    
    @Override
    public List<ConstraintViolation> getViolations(String paramName) {
        if(this.violations.containsKey(paramName)) {
            return this.violations.get(paramName);
        } else {
            return Lists.newArrayList();
        }
    }
    
    @Override
    public void addViolation(ConstraintViolation violation) {
        if(!this.violations.containsKey(violation.getFieldKey())) {
            this.violations.put(violation.getFieldKey(), Lists.<ConstraintViolation>newArrayList());
        }
        this.violations.get(violation.getFieldKey()).add(violation);
    }

    @Override
    public void addFieldViolation(FieldViolation fieldViolation) {
        this.addViolation(fieldViolation.constraintViolation);
    }

    @Override
    public void addFieldViolation(String field, ConstraintViolation constraintViolation) {
        addFieldViolation(new FieldViolation(field, constraintViolation));
    }

    @Override
    public boolean hasFieldViolation(String field) {
        return !this.getFieldViolations(field).isEmpty();
    }

    @Override
    public List<FieldViolation> getFieldViolations() {
        List<FieldViolation> sumViolations = Lists.newArrayList();
        for(ConstraintViolation violation : this.getViolations()) {
            if(violation.getMessageKey() != null 
                    && !violation.getMessageKey().startsWith(JAVAX_VALIDATION_PREFIX)
                    && !violation.getMessageKey().startsWith(HIBERNATE_VALIDATION_PREFIX)) {
                sumViolations.add(new FieldViolation(violation.getFieldKey(), violation));
            }
        }
        return sumViolations;
    }

    @Override
    public List<FieldViolation> getFieldViolations(String field) {
        List<FieldViolation> sumViolations = Lists.newArrayList();
        for(ConstraintViolation violation : this.getViolations(field)) {
            if(violation.getMessageKey() != null 
                    && !violation.getMessageKey().startsWith(JAVAX_VALIDATION_PREFIX)
                    && !violation.getMessageKey().startsWith(HIBERNATE_VALIDATION_PREFIX)) {
                sumViolations.add(new FieldViolation(violation.getFieldKey(), violation));
            }
        }
        return sumViolations;
    }

    @Override
    public void addBeanViolation(FieldViolation fieldViolation) {
        this.addViolation(fieldViolation.constraintViolation);
    }

    @Override
    public boolean hasBeanViolation(String field) {
        return !this.getBeanViolations(field).isEmpty();
    }

    @Override
    public boolean hasBeanViolations() {
        return !this.getBeanViolations().isEmpty();
    }

    @Override
    public List<FieldViolation> getBeanViolations() {
        List<FieldViolation> sumViolations = Lists.newArrayList();
        for(ConstraintViolation violation : this.getViolations()) {
            if(violation.getMessageKey() != null 
                    && (violation.getMessageKey().startsWith(JAVAX_VALIDATION_PREFIX)
                    || violation.getMessageKey().startsWith(HIBERNATE_VALIDATION_PREFIX))) {
                sumViolations.add(new FieldViolation(violation.getFieldKey(), 
                        new ConstraintViolation(violation.getDefaultMessage(), null, null, violation.getMessageParams())));
            }
        }
        return sumViolations;
    }
    
    @Override
    public List<FieldViolation> getBeanViolations(String field) {
        List<FieldViolation> sumViolations = Lists.newArrayList();
        for(ConstraintViolation violation : this.getViolations(field)) {
            if(violation.getMessageKey() != null 
                    && (violation.getMessageKey().startsWith(JAVAX_VALIDATION_PREFIX)
                    || violation.getMessageKey().startsWith(HIBERNATE_VALIDATION_PREFIX))) {
                sumViolations.add(new FieldViolation(violation.getFieldKey(), 
                        new ConstraintViolation(violation.getDefaultMessage(), null, null, violation.getMessageParams())));
            }
        }
        return sumViolations;
    }

    @Override
    public List<ConstraintViolation> getGeneralViolations() {
        return Lists.newArrayList();
    }
}
