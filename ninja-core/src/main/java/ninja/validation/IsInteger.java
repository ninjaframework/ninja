package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the field is an integer.
 * Only needed if you want to customise the validation messages.
 *
 * @author James Roper
 */
@WithValidator(Validators.IntegerValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface IsInteger {
    /**
     * The key for the violation message
     *
     * @return The key of the violation message
     */
    String key() default KEY;

    /**
     * Default message if the key isn't found
     *
     * @return The default message
     */
    String message() default MESSAGE;

    /**
     * The key for formatting the field
     *
     * @return The key
     */
    String fieldKey() default "";

    public static final String KEY = "validation.is.integer.violation";
    public static final String MESSAGE = "{0} must be an integer";
}
