package ninja.params;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Inject a session value to a controller method invocation
 *
 * @author James Roper
 */
@WithArgumentExtractor(ArgumentExtractors.SessionParamExtractor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SessionParam {
    /**
     * The key to look up the session value
     *
     * @return The key
     */
    String value();
}
