package ninja.validation;

/**
 * A validator for validating parameters
 *
 * @author James Roper
 */
public interface Validator<T> {
    /**
     * Validate the given value
     *
     * @param value The value, may be null
     * @param field The name of the field being validated, if applicable
     * @param validation The validation context
     */
    void validate(T value, String field, Validation validation);

    /**
     * Get the type that this validator validates
     *
     * @return The type that the validator validates
     */
    Class<T> getValidatedType();
}
