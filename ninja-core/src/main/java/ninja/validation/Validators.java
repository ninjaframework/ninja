/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import com.google.common.base.Optional;
import com.google.inject.Inject;
import ninja.Context;
import ninja.Result;
import ninja.i18n.Lang;

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

        /**
         * Get the constraint descriptor.
         *
         * @return The constraint descriptor
         * @see javax.validation.metadata.ConstraintDescriptor
         */
        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return this.descriptor;
        }

        /**
         * Get the validated value.
         *
         * @return The value
         */
        @Override
        public Object getValidatedValue() {
            return this.value;
        }
    }

    public static class JSRValidator implements Validator<Object> {

        private final Lang requestLanguage;

        @Inject
        public JSRValidator(Lang requestLanguage) {
            this.requestLanguage = requestLanguage;
        }

        /**
         * Validate the given value.
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(Object value, String field, Context context) {
            if (value != null) {
                final ValidatorFactory validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory();
                final javax.validation.Validator validator = validatorFactory.getValidator();
                final Set<javax.validation.ConstraintViolation<Object>> violations = validator.validate(value);
                final Locale localeToUse = this.requestLanguage.getLocaleFromStringOrDefault(this.requestLanguage.getLanguage(context, Optional.<Result>absent()));
                final Validation validation = context.getValidation();

                for (final javax.validation.ConstraintViolation<Object> violation : violations) {
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
        public void validate(Object value, String field, Context context) {
            if (value == null) {
                context.getValidation().addFieldViolation(
                        field,
                        ConstraintViolation.createForFieldWithDefault(
                                this.required.key(),
                                fieldKey(field, this.required.fieldKey()),
                                this.required.message()));
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

        /**
         * Validate the given value
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(String value, String field, Context context) {
            if (value != null) {
                if (this.length.max() != -1 && value.length() > this.length.max()) {
                    context.getValidation().addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(this.length.maxKey(),
                                    fieldKey(field, this.length.fieldKey()),
                                    this.length.maxMessage(), this.length.max(), value));
                } else if (this.length.min() != -1 && value.length() < this.length.min()) {
                    context.getValidation().addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(this.length.minKey(),
                                    fieldKey(field, this.length.fieldKey()),
                                    this.length.minMessage(), this.length.min(), value));
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
            this.isInteger = integer;
        }

        /**
         * Validate the given value
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(String value, String field, Context context) {
            if (value != null) {
                try {
                    Long.parseLong(value);
                } catch (NumberFormatException e) {
                    context.getValidation().addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(this.isInteger.key(),
                                    fieldKey(field, this.isInteger.fieldKey()),
                                    this.isInteger.message(), value));
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
            this.isFloat = aFloat;
        }

        /**
         * Validate the given value
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(String value, String field, Context context) {
            if (value != null) {
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    context.getValidation().addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(this.isFloat.key(),
                                    fieldKey(field, this.isFloat.fieldKey()),
                                    this.isFloat.message(), value));
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
            this.pattern = Pattern.compile(matches.regexp());
        }

        /**
         * Validate the given value
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(String value, String field, Context context) {
            if (value != null) {
                if (!this.pattern.matcher(value).matches()) {
                    context.getValidation().addFieldViolation(
                            field,
                            ConstraintViolation.createForFieldWithDefault(
                                    this.matches.key(),
                                    fieldKey(field, this.matches.fieldKey()),
                                    this.matches.message(),
                                    this.matches.regexp(), value));
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

        /**
         * Validate the given value
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(Number value, String field, Context context) {
            if (value != null) {
                if (this.number.max() != Double.MAX_VALUE
                        && value.doubleValue() > this.number.max()) {
                    context.getValidation().addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(this.number.maxKey(),
                                    fieldKey(field, this.number.fieldKey()),
                                    this.number.maxMessage(), this.number.max(), value));
                } else if (this.number.min() != -1
                        && value.doubleValue() < this.number.min()) {
                    context.getValidation().addFieldViolation(field, ConstraintViolation
                            .createForFieldWithDefault(this.number.minKey(),
                                    fieldKey(field, this.number.fieldKey()),
                                    this.number.minMessage(), this.number.min(), value));
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
            this.isEnum = anEnum;
        }

        /**
         * Validate the given value
         *
         * @param value   The value, may be null
         * @param field   The name of the field being validated, if applicable
         * @param context The Ninja request context
         */
        @Override
        public void validate(String value, String field, Context context) {
            if (value != null) {
                Enum<?>[] values = this.isEnum.enumClass().getEnumConstants();
                for (Enum<?> v : values) {
                    if (this.isEnum.caseSensitive()) {
                        if (v.name().equals(value)) {
                            return;
                        }
                    } else {
                        if (v.name().equalsIgnoreCase(value)) {
                            return;
                        }
                    }
                }

                context.getValidation().addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                        IsEnum.KEY, field, IsEnum.MESSAGE, value, this.isEnum.enumClass().getName()));
            }
        }

        @Override
        public Class<String> getValidatedType() {
            return String.class;
        }
    }
}
