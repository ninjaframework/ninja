package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the length of the field meets the given length constraints
 *
 * @author James Roper
 */
@WithValidator(Validators.RequiredValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Required {
    /**
     * The key for the violation message
     *
     * @return The key of the violation message
     */
    String key() default "validation.required.violation";

    /**
     * Default message if the key isn't found
     *
     * @return The default message
     */
    String message() default "{0} is required";

    /**
     * The key for formatting the field
     *
     * @return The key
     */
    String fieldKey() default "";
}
