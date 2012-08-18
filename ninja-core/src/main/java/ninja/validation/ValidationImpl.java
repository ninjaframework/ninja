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

import ninja.i18n.Lang;

import javax.inject.Inject;
import java.util.*;

/**
 * Validation object
 * 
 * @author James Roper
 */
public class ValidationImpl implements Validation {
    private final Lang lang;

    private final Map<String, ConstraintViolation> fieldViolations = new HashMap<String, ConstraintViolation>();
    private final List<ConstraintViolation> generalViolations = new ArrayList<ConstraintViolation>();

    @Inject
    public ValidationImpl(Lang lang) {
        this.lang = lang;
    }

    @Override
    public boolean hasViolations() {
        return !fieldViolations.isEmpty() || !generalViolations.isEmpty();
    }

    @Override
    public boolean hasFieldViolation(String field) {
        return fieldViolations.get(field) != null;
    }

    @Override
    public void addFieldViolation(String field,
                                  ConstraintViolation constraintViolation) {
        if (field == null) {
            generalViolations.add(constraintViolation);
        } else {
            fieldViolations.put(field, constraintViolation);
        }
    }

    @Override
    public void addViolation(ConstraintViolation constraintViolation) {
        generalViolations.add(constraintViolation);
    }

    @Override
    public ConstraintViolation getFieldViolation(String field) {
        return fieldViolations.get(field);
    }

    @Override
    public String getFieldViolationMessage(String field, String language) {
        ConstraintViolation violation = fieldViolations.get(field);
        if (violation == null) {
            return null;
        }
        // First, format field
        String formattedField = lang.getWithDefault(violation.getFieldKey(),
                field, language);
        // Create parameters
        Object[] params = new Object[violation.getMessageParams().length + 1];
        params[0] = formattedField;
        if (params.length > 1) {
            System.arraycopy(violation.getMessageParams(), 0, params, 1,
                    violation.getMessageParams().length);
        }
        // Format field
        return lang.getWithDefault(violation.getMessageKey(),
                violation.getDefaultMessage(), language, params);
    }
}
