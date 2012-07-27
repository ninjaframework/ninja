package ninja.params;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this annotation should use the given argument extractor
 *
 * @author James Roper
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithArgumentExtractor {
    /**
     * The argument extractor that should be used with this annotation
     */
    Class<? extends ArgumentExtractor<?>> value();
}
