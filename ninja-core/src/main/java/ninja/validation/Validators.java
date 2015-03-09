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

import ninja.Context;

import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;
import java.io.Serializable;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Built in validators.
 *
 * @author James Roper
 * @author Thibault Meyer
 */
public class Validators {

    private static String fieldKey(String fieldName, String configuredFieldKey) {
        if (configuredFieldKey.length() > 0) {
            return configuredFieldKey;
        } else {
            return fieldName;
        }
    }

    /**
     * A basic Message interpolator.
     *
     * @author Thibault Meyer
     */
    private static class NinjaContextMsgInterpolator implements MessageInterpolator.Context, Serializable {

        private final Object value;
        private final ConstraintDescriptor<?> descriptor;

        /**
         * Create message interpolator context.
         *
         * @param value      value being validated
         * @param descriptor Constraint being validated
         */
        public NinjaContextMsgInterpolator(Object value, ConstraintDescriptor<?> descriptor) {
            this.value = value;
            this.descriptor = descriptor;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return descriptor;
        }

        @Override
        public Object getValidatedValue() {
            return value;
        }
    }

    public static class JSRValidator implements Validator<Object> {

        @Override
        @Deprecated
        public void validate(Object value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        @Override
        public void validate(Object value, String field, Validation validation, ninja.Context context) {
            if (value != null) {
                final ValidatorFactory validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory();
                final javax.validation.Validator validator = validatorFactory.getValidator();
                final Set<javax.validation.ConstraintViolation<Object>> violations = validator.validate(value);
                final Locale localeToUse = (context == null || context.getAcceptLanguage() == null) ? Locale.getDefault() : Locale.forLanguageTag(context.getAcceptLanguage().split(",", 2)[0]);

                for (javax.validation.ConstraintViolation<Object> violation : violations) {
                    final String violationMessage = validatorFactory.getMessageInterpolator().interpolate(
                            violation.getMessageTemplate(),
                            new NinjaContextMsgInterpolator(value, violation.getConstraintDescriptor()),
                            localeToUse
                    );
                    final ConstraintViolation constraintViolation = ConstraintViolation.create(violationMessage, violation.getInvalidValue());
                    validation.addBeanViolation(new FieldViolation(violation.getPropertyPath().toString(), constraintViolation));
                }
            }
        }

        @Override
        public Class<Object> getValidatedType() {
            return Object.class;
        }
    }

    public static class RequiredValidator implements Validator<Object> {

        private final Required required;

        public RequiredValidator(Required required) {
            this.required = required;
        }

        @Override
        @Deprecated
        public void validate(Object value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        @Override
        public void validate(Object value, String field, Validation validation, ninja.Context context) {
            if (value == null) {
                validation.addFieldViolation(
                        field,
                        ConstraintViolation.createForFieldWithDefault(
                                required.key(),
                                fieldKey(field, required.fieldKey()),
                                required.message()));
            }
        }

        @Override
        public Class<Object> getValidatedType() {
            return Object.class;
        }
    }

    public static class LengthValidator implements Validator<String> {

        private final Length length;

        public LengthValidator(Length length) {
            this.length = length;
        }

        @Override
        @Deprecated
        public void validate(String value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        /**
         * Validate the given value
         *
         * @param value      The value, may be null
         * @param field      The name of the field being validated, if applicable
         * @param validation The validation context
         * @param context    The Ninja request context
         */
        @Override
        public void validate(String value, String field, Validation validation, Context context) {
            if (value != null) {
                if (length.max() != -1 && value.length() > length.max()) {
                    validation.addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(length.maxKey(),
                                    fieldKey(field, length.fieldKey()),
                                    length.maxMessage(), length.max(), value));
                } else if (length.min() != -1 && value.length() < length.min()) {
                    validation.addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(length.minKey(),
                                    fieldKey(field, length.fieldKey()),
                                    length.minMessage(), length.min(), value));
                }
            }
        }

        @Override
        public Class<String> getValidatedType() {
            return String.class;
        }
    }

    public static class IntegerValidator implements Validator<String> {

        private final IsInteger isInteger;

        public IntegerValidator(IsInteger integer) {
            isInteger = integer;
        }

        @Override
        @Deprecated
        public void validate(String value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        /**
         * Validate the given value
         *
         * @param value      The value, may be null
         * @param field      The name of the field being validated, if applicable
         * @param validation The validation context
         * @param context    The Ninja request context
         */
        @Override
        public void validate(String value, String field, Validation validation, Context context) {
            if (value != null) {
                try {
                    Long.parseLong(value);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(isInteger.key(),
                                    fieldKey(field, isInteger.fieldKey()),
                                    isInteger.message(), value));
                }
            }
        }

        @Override
        public Class<String> getValidatedType() {
            return String.class;
        }
    }

    public static class FloatValidator implements Validator<String> {

        private final IsFloat isFloat;

        public FloatValidator(IsFloat aFloat) {
            isFloat = aFloat;
        }

        @Override
        @Deprecated
        public void validate(String value, String field, Validation validation) {
            this.validate(value, field, validation, null/**/);
        }

        /**
         * Validate the given value
         *
         * @param value      The value, may be null
         * @param field      The name of the field being validated, if applicable
         * @param validation The validation context
         * @param context    The Ninja request context
         */
        @Override
        public void validate(String value, String field, Validation validation, Context context) {
            if (value != null) {
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(isFloat.key(),
                                    fieldKey(field, isFloat.fieldKey()),
                                    isFloat.message(), value));
                }
            }
        }

        @Override
        public Class<String> getValidatedType() {
            return String.class;
        }
    }

    public static class MatchesValidator implements Validator<String> {

        private final Matches matches;
        private final Pattern pattern;

        public MatchesValidator(Matches matches) {
            this.matches = matches;
            pattern = Pattern.compile(matches.regexp());
        }

        @Override
        @Deprecated
        public void validate(String value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        /**
         * Validate the given value
         *
         * @param value      The value, may be null
         * @param field      The name of the field being validated, if applicable
         * @param validation The validation context
         * @param context    The Ninja request context
         */
        @Override
        public void validate(String value, String field, Validation validation, Context context) {
            if (value != null) {
                if (!pattern.matcher(value).matches()) {
                    validation
                            .addFieldViolation(
                                    field,
                                    ConstraintViolation.createForFieldWithDefault(
                                            matches.key(),
                                            fieldKey(field, matches.fieldKey()),
                                            matches.message(),
                                            matches.regexp(), value));
                }
            }
        }

        @Override
        public Class<String> getValidatedType() {
            return String.class;
        }
    }

    public static class NumberValidator implements Validator<Number> {

        private final NumberValue number;

        public NumberValidator(NumberValue number) {
            this.number = number;
        }

        @Override
        @Deprecated
        public void validate(Number value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        /**
         * Validate the given value
         *
         * @param value      The value, may be null
         * @param field      The name of the field being validated, if applicable
         * @param validation The validation context
         * @param context    The Ninja request context
         */
        @Override
        public void validate(Number value, String field, Validation validation, Context context) {
            if (value != null) {
                if (number.max() != Double.MAX_VALUE
                        && value.doubleValue() > number.max()) {
                    validation.addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(number.maxKey(),
                                    fieldKey(field, number.fieldKey()),
                                    number.maxMessage(), number.max(), value));
                } else if (number.min() != -1
                        && value.doubleValue() < number.min()) {
                    validation.addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(number.minKey(),
                                    fieldKey(field, number.fieldKey()),
                                    number.minMessage(), number.min(), value));
                }
            }
        }

        @Override
        public Class<Number> getValidatedType() {
            return Number.class;
        }
    }

    public static class EnumValidator implements Validator<String> {

        private final IsEnum isEnum;

        public EnumValidator(IsEnum anEnum) {
            isEnum = anEnum;
        }

        @Override
        @Deprecated
        public void validate(String value, String field, Validation validation) {
            this.validate(value, field, validation, null);
        }

        /**
         * Validate the given value
         *
         * @param value      The value, may be null
         * @param field      The name of the field being validated, if applicable
         * @param validation The validation context
         * @param context    The Ninja request context
         */
        @Override
        public void validate(String value, String field, Validation validation, Context context) {
            if (value != null) {
                Enum<?>[] values = isEnum.enumClass().getEnumConstants();
                for (Enum<?> v : values) {
                    if (isEnum.caseSensitive()) {
                        if (v.name().equals(value)) {
                            return;
                        }
                    } else {
                        if (v.name().equalsIgnoreCase(value)) {
                            return;
                        }
                    }
                }

                validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                        IsEnum.KEY, field, IsEnum.MESSAGE, new Object[]{value, isEnum.enumClass().getName()}));
            }
        }

        @Override
        public Class<String> getValidatedType() {
            return String.class;
        }
    }
}
