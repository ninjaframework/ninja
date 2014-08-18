package router;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Route annotations allow for automatic configuration of routes.
 * 
 * For example,
 * 
 * <pre>
 * @Route(httpMethod = Route.GET, path = "/index")
 * </pre>
 */
@Target({METHOD})
@Retention(RUNTIME)
@Documented
public @interface Route {

    final static String DELETE = "DELETE";
    final static String GET = "GET";
    final static String HEAD = "HEAD";
    final static String OPTIONS = "OPTIONS"; 
    final static String POST = "POST"; 
    final static String PUT = "PUT";

    String httpMethod() default Route.GET;

    String path() default "";
}
