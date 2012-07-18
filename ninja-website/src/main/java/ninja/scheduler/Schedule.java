package ninja.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Schedules the annotated method for execution
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedule {
    /**
     * The delay between executions. Is used as the default if no delay property is found.
     *
     * @return The delay between executions
     */
    long delay() default -1;

    /**
     * The property to read the delay from.  If not specified, delay is used.
     *
     * @return The name of the property to read the delay from.
     */
    String delayProperty() default NO_PROPERTY;

    /**
     * The time unit, defaults to milliseconds
     *
     * @return The time unit
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * The property to read the time unit from.  If not specified, timeUnit is used.
     *
     * @return The property to read the time unit from.
     */
    String timeUnitProperty() default NO_PROPERTY;

    /**
     * The delay before it should first be run.  If negative, the delay is used as the initial delay.
     *
     * @return The delay
     */
    long initialDelay() default -1;


    /**
     * The property to read the initial delay from.  If not specified, initialDelay is used.
     *
     * @return The property to read the initial delay from.
     */
    String initialDelayProperty() default NO_PROPERTY;

    static String NO_PROPERTY = "_no-property";
}
