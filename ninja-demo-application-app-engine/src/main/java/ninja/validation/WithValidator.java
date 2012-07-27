package ninja.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotation should have this validator class applied to it
 *
 * @author James Roper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface WithValidator {
    /**
     * Validator that should be used to validate parameters annotated with
     * this annotation.
     *
     * @return The validator class
     */
    Class<? extends Validator<?>> value();
}
