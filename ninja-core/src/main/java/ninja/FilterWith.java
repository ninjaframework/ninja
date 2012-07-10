package ninja;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A simple annotation that let's you put a filter
 * on a controller method or class.
 * 
 * The filter will then be executed before the controller method is executed.
 * 
 * Please check out also {@link Filter}.
 * 
 * @author ra
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FilterWith {
	Class<? extends Filter>[] value();
}