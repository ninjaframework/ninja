package ninja.lifecycle;


import com.google.common.collect.Lists;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Register of lifecycle classes
 *
 * @author James Roper
 */
@Singleton
class LifecycleRegister {
    private static final Logger log = LoggerFactory.getLogger(LifecycleRegister.class);
    private final List<Target> startables = Collections.synchronizedList(new ArrayList<Target>());
    private final List<Target> disposables = Collections.synchronizedList(new ArrayList<Target>());
    private final AtomicBoolean started = new AtomicBoolean(false);

    public void start() {
        if (started.get()) {
            throw new FailedStartException("VZ service is already started!");
        }
        List<Target> toStart = new ArrayList<Target>(this.startables);
        started.set(true);
        startables.clear();
        // Sort the beans to start
        Collections.sort(toStart);
        // Start them
        for (Target target : toStart) {
            invokeTarget(target);
        }
    }

    public void stop() {
        if (!started.get()) {
            throw new FailedDisposeException("VZ service is not started!");
        }
        List<Target> toDispose = new ArrayList<Target>(this.disposables);
        started.set(false);
        disposables.clear();
        // Sort the beans to dispose
        Collections.sort(toDispose);
        for (Target target : Lists.reverse(toDispose)) {
            try {
                invokeTarget(target);
            } catch (Exception e) {
                log.warn("Error stopping service", e);
            }
        }
    }

    public boolean isStarted() {
        return started.get();
    }

    public void registerStartable(Target target) {
        if (started.get()) {
            log.warn("Startable instantiated after the application has been started: " + target.getTarget().toString());
            invokeTarget(target);
        } else {
            startables.add(target);
        }
    }

    public void registerDisposable(Target target) {
        disposables.add(target);
    }

    public void invokeTarget(Target target) {
        try {
            target.getStartMethod().invoke(target.getTarget());
        } catch (IllegalAccessException e) {
            throw new FailedStartException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof FailedStartException) {
                throw (FailedStartException) e.getCause();
            } else {
                throw new FailedStartException(e.getCause());
            }
        }
    }
}
