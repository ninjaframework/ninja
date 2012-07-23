package ninja.params;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Just an idea to inject parameters right into the methods...
 * 
 * This equals context.getParameter(...)
 *  
 * @author ra
 *
 */
@WithArgumentExtractor(ArgumentExtractors.ParamExtractor.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Param {
	String value();
}