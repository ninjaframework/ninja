package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that the annoted element is conform to its JSR303-Annotations
 * 
 * @author psommer
 * 
 */
@WithValidator(Validators.JSRValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface JSR303Validation {
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

    public static final String KEY = "validation.is.JSR303.violation";
    public static final String MESSAGE = "{0} cannot be validated with JSR303 annotations";
}
