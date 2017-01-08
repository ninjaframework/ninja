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

package ninja.params;

import ninja.Context;
import ninja.validation.Validator;

import java.util.List;

/**
 * Argument extractor that wraps another argument extractor and validates
 * its argument
 *
 * @author James Roper
 */
public class ValidatingArgumentExtractor<T> implements ArgumentExtractor<T> {

    private final ArgumentExtractor<T> wrapped;
    private final List<Validator<T>> validators;

    public ValidatingArgumentExtractor(ArgumentExtractor<T> wrapped, List<Validator<T>> validators) {
        this.wrapped = wrapped;
        this.validators = validators;
    }

    @Override
    public T extract(Context context) {
        final T value = this.wrapped.extract(context);
        // Check if we already have a validation error from a previous stage
        if (context.getValidation().hasFieldViolation(this.wrapped.getFieldName())) {
            return value;
        }
        // Apply validators
        for (Validator<T> validator : this.validators) {
            validator.validate(value, this.wrapped.getFieldName(), context);
            if (context.getValidation().hasFieldViolation(this.wrapped.getFieldName())) {
                // Break if validation failed
                break;
            }
        }
        // Note that it is important that we return the value regardless of the outcome
        // of validation, because in the case of primitive types, if we return null,
        // the method won't be executed.
        return value;
    }

    @Override
    public Class<T> getExtractedType() {
        return this.wrapped.getExtractedType();
    }

    @Override
    public String getFieldName() {
        return this.wrapped.getFieldName();
    }
}
