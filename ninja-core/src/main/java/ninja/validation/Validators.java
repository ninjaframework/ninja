package ninja.validation;

import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ValidatorFactory;


/**
 * Built in validators
 *
 * @author James Roper
 */
public class Validators {

    public static class JSRValidator implements Validator<Object> {

        @Override
        public void validate(Object value, String field, Validation validation) {
            ValidatorFactory validatorFactory =
                    javax.validation.Validation.buildDefaultValidatorFactory();
            javax.validation.Validator validator = validatorFactory.getValidator();
            Set<javax.validation.ConstraintViolation<Object>> violations =
                    validator.validate(value);
            for (javax.validation.ConstraintViolation<Object> violation : violations) {
                validation.addViolation(ninja.validation.ConstraintViolation.create(violation
                        .getMessage(), violation.getInvalidValue()));
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
        public void validate(Object value, String field, Validation validation) {
            if (value == null) {
                validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                    required.key(), fieldKey(field, required.fieldKey()), required.message()));
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
        public void validate(String value, String field, Validation validation) {
            if (value != null) {
                if (length.max() != -1 && value.length() > length.max()) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            length.maxKey(), fieldKey(field, length.fieldKey()), length.maxMessage(),
                            length.max(), value));
                } else if (length.min() != -1 && value.length() < length.min()) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            length.minKey(), fieldKey(field, length.fieldKey()), length.minMessage(),
                            length.min(), value));
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
        public void validate(String value, String field, Validation validation) {
            if (value != null) {
                try {
                    Long.parseLong(value);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            isInteger.key(), fieldKey(field, isInteger.fieldKey()), isInteger.message(), value));
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
        public void validate(String value, String field, Validation validation) {
            if (value != null) {
                try {
                    Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            isFloat.key(), fieldKey(field, isFloat.fieldKey()), isFloat.message(), value));
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
        public void validate(String value, String field, Validation validation) {
            if (value != null) {
                if (!pattern.matcher(value).matches()) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            matches.key(), fieldKey(field, matches.fieldKey()), matches.message(),
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
        public void validate(Number value, String field, Validation validation) {
            if (value != null) {
                if (number.max() != Double.MAX_VALUE && value.doubleValue() > number.max()) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            number.maxKey(), fieldKey(field, number.fieldKey()), number.maxMessage(),
                            number.max(), value));
                } else if (number.min() != -1 && value.doubleValue() < number.min()) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                            number.minKey(), fieldKey(field, number.fieldKey()), number.minMessage(),
                            number.min(), value));
                }
            }
        }

        @Override
        public Class<Number> getValidatedType() {
            return Number.class;
        }
    }

    private static String fieldKey(String fieldName, String configuredFieldKey) {
        if (configuredFieldKey.length() > 0) {
            return configuredFieldKey;
        } else {
            return fieldName;
        }
    }
}
