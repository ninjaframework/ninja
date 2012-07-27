package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the value of a number meets certain parameters
 *
 * @author James Roper
 */
@WithValidator(Validators.NumberValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface NumberValue {
    /**
     * The maximum value of the field
     */
    double max() default Double.MAX_VALUE;

    /**
     * The minimum value of the field
     */
    double min() default Double.MIN_VALUE;

    /**
     * The key max violation message
     *
     * @return The key of the max violation message
     */
    String maxKey() default "validation.number.max.violation";

    /**
     * Default message if max violation message isn't found
     *
     * @return The default message
     */
    String maxMessage() default "{0} exceeds maximum value of {1}";

    /**
     * The key min violation message
     *
     * @return The key of the min violation message
     */
    String minKey() default "validation.number.min.violation";

    /**
     * Default message if min violation message isn't found
     *
     * @return The default message
     */
    String minMessage() default "{0} is less than minimum value of {1}";

    /**
     * The key for formatting the field
     *
     * @return The key
     */
    String fieldKey() default "";
}
