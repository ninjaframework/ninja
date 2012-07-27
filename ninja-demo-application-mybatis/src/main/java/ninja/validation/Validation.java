package ninja.validation;

import com.google.inject.ImplementedBy;

import java.util.Locale;

/**
 * Validation context
 *
 * @author James Roper
 */
@ImplementedBy(ValidationImpl.class)
public interface Validation {
    /**
     * Whether the validation context has violations
     *
     * @return True if it does
     */
    boolean hasViolations();

    /**
     * Whether the validation context has a violation for the given field
     *
     * @return True if it does
     */
    boolean hasFieldViolation(String field);

    /**
     * Add a violation to the given field
     *
     * @param field The field to add the violation to
     * @param constraintViolation The constraint violation
     */
    void addFieldViolation(String field, ConstraintViolation constraintViolation);

    /**
     * Add a general violation
     *
     * @param constraintViolation The constraint violation
     */
    void addViolation(ConstraintViolation constraintViolation);


    /**
     * Get the voilation for the given field
     *
     * @param field The field
     * @return The constraint violation, or null if no constraint violation was found
     */
    ConstraintViolation getFieldViolation(String field);

    /**
     * Get the formatted violation message for the given field
     *
     * @param field The field
     * @param locale The locale
     * @return The message, or null if there was no violation
     */
    String getFieldViolationMessage(String field, Locale locale);

}
