/**
 * Copyright (C) the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.scheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;

import ninja.utils.AopUtils;
import ninja.scheduler.cron.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.ConfigurationException;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

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

        for (final Method method : AopUtils.isAopProxy(clazz)
                ? clazz.getSuperclass().getMethods()
                : clazz.getMethods()) {
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
            for (final Method method : AopUtils.isAopProxy(target)
                    ? target.getClass().getSuperclass().getMethods()
                    : target.getClass().getMethods()) {
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
        if (!schedule.cron().equals(Schedule.NO_PROPERTY)) {
            scheduleCron(target, method, schedule);
        } else {
            scheduleDelay(target, method, schedule);
        }
    }

    private void scheduleCron(final Object target, final Method method, Schedule schedule) {
        final CronExpression cronExpression = new CronExpression(schedule.cron().trim());
        final ZoneId zoneId = schedule.cronZone().equals(Schedule.NO_PROPERTY)
                ? ZoneId.systemDefault()
                : ZoneId.of(schedule.cronZone());

        log.info("Scheduling method " + method.getName() + " on " + target
                + " using CRON expression " + schedule.cron()
                + " (TimeZone: " + zoneId + ")");

        final Callable<Void> callable = new Callable<Void>() {

            @Override
            public Void call() {
                executor.schedule(this, cronExpression.getNextDelayMilliseconds(zoneId), TimeUnit.MILLISECONDS);

                try {
                    method.invoke(target);
                } catch (final Exception exception) {
                    log.error("An error occurred during the execution of scheduled method {}::{}",
                            target.getClass().getName(),
                            method.getName(),
                            exception);
                }

                return null;
            }
        };

        executor.schedule(callable, cronExpression.getNextDelayMilliseconds(zoneId), TimeUnit.MILLISECONDS);
    }

    private void scheduleDelay(final Object target, final Method method, Schedule schedule) {
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

        final String targetName = AopUtils.isAopProxy(target)
                ? target.getClass().getSuperclass().getName()
                : target.getClass().getName();
        log.info("Scheduling method " + method.getName() + " on " + targetName + " to be run every " + delay
                + " " + timeUnit + " after " + initialDelay + " " + timeUnit);

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
