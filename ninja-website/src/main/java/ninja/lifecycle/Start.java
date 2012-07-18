package ninja.lifecycle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating this method should be called when the application starts up.
 * <p/>
 * If nothing else depends on this bean, then this bean will only work if the bean is explicitly bound.
 * <p/>
 * Note: If this bean is provided by an @Provided method, then that method *must* be annotated with @Singleton,
 * otherwise it won't be detected.
 *
 * @author James Roper
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Start {
    /**
     * The order in which it should be started, higher meaning later.  While apps are free to use any ordering system
     * they wish, the following convention is recommended:
     * <p/>
     * 10 - Services that connect to resources and do not depend on other services, for example, database connections
     * 20-80 - Services that depend on resources, but don't actually start the app doing its core functions
     * 90 - Services that start the app doing its core functions, for example, listen on queues, listen for HTTP, start
     * scheduled services
     *
     * @return The order, the least being started first, the greatest being started last
     */
    int order() default 50;
}
