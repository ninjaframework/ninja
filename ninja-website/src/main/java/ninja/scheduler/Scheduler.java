package ninja.scheduler;

import com.google.inject.*;
import com.google.inject.name.Names;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The actual scheduler
 */
@Singleton
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

    @Inject
    private Injector injector;
    private volatile ScheduledExecutorService executor;
    private final List<Object> objectsToSchedule = Collections.synchronizedList(new ArrayList<Object>());

    @Start(order = 90)
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor();
        scheduleCachedObjects();
    }

    @Dispose(order = 90)
    public void dispose() {
        executor.shutdown();
        executor = null;
    }

    public boolean hasScheduledMethod(Class<?> clazz) {
        for (Method method : clazz.getMethods()) {
            Schedule schedule = method.getAnnotation(Schedule.class);
            if (schedule != null) {
                return true;
            }
        }
        return false;
    }

    public void schedule(Object target) {
        if (executor == null) {
            objectsToSchedule.add(target);
        } else {
            for (final Method method : target.getClass().getMethods()) {
                Schedule schedule = method.getAnnotation(Schedule.class);
                if (schedule != null) {
                    schedule(target, method, schedule);
                }
            }
        }
    }

    private void scheduleCachedObjects() {
        List<Object> copy = new ArrayList<Object>(objectsToSchedule);
        objectsToSchedule.clear();
        for (Object object : copy) {
            schedule(object);
        }
    }

    private void schedule(final Object target, final Method method, Schedule schedule) {
        long delay = schedule.delay();
        if (!schedule.delayProperty().equals(Schedule.NO_PROPERTY)) {
            String delayString = getProperty(schedule.delayProperty());
            if (delayString != null) {
                delay = Long.parseLong(delayString);
            } else if (delay < 0) {
                throw new IllegalArgumentException("No delay property found: " + schedule.delayProperty() + " and no default delay set");
            }
        }
        if (delay < 0) {
            throw new IllegalArgumentException("No delay or delay property specified");
        }
        TimeUnit timeUnit = schedule.timeUnit();
        if (!schedule.timeUnitProperty().equals(Schedule.NO_PROPERTY)) {
            String timeUnitString = getProperty(schedule.timeUnitProperty());
            if (timeUnitString != null) {
                timeUnit = TimeUnit.valueOf(timeUnitString);
            }
        }
        long initialDelay = schedule.initialDelay();
        if (!schedule.initialDelayProperty().equals(Schedule.NO_PROPERTY)) {
            String initialDelayString = getProperty(schedule.initialDelayProperty());
            if (initialDelayString != null) {
                initialDelay = Long.parseLong(initialDelayString);
            }
        }
        if (initialDelay < 0) {
            initialDelay = delay;
        }

        log.info("Scheduling method " + method.getName() + " on " + target + " to be run every " + delay
                + " " + timeUnit + " after " + delay + " " + timeUnit);
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    log.debug("Running scheduled method {} on {}", method.getName(), target);
                    method.invoke(target);
                } catch (Exception e) {
                    log.error("Error invoking scheduled run of method " + method.getName() + " on " + target, e);
                }
            }
        }, initialDelay, delay, timeUnit);
    }

    private String getProperty(String name) {
        try {
            return injector.getInstance(Key.get(String.class, Names.named(name)));
        } catch (ConfigurationException e) {
            // Ignore
            return null;
        }
    }


}
