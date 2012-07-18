package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the parameter matches the given regular expression
 */
@WithValidator(Validators.MatchesValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Matches {

    /**
     * The regular expression to match
     */
    String regexp();

    /**
     * The key for the violation message
     *
     * @return The key of the violation message
     */
    String key() default "validation.matches.violation";

    /**
     * Default message if the key isn't found
     *
     * @return The default message
     */
    String message() default "{0} is doesn't match the format {1}";

    /**
     * The key for formatting the field
     *
     * @return The key
     */
    String fieldKey() default "";
}
