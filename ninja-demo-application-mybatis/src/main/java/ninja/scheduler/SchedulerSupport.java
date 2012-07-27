package ninja.scheduler;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * Support for scheduling using annotations
 *
 * @since 1.0.0
 * @author James Roper
 */
public class SchedulerSupport {

    /**
     * Get the scheduler support module
     */
    public static Module getModule() {
        return new SchedulerSupport().constructModule();
    }

    private SchedulerSupport() {
    }

    private static class SchedulableListener implements TypeListener {
        private final Scheduler scheduler;

        private SchedulableListener(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            if (scheduler.hasScheduledMethod(type.getRawType())) {
                // Add the listener
                encounter.register(new ScheduleListener<I>(scheduler));
            }
        }
    }

    private static class ScheduleListener<I> implements InjectionListener<I> {
        private final Scheduler scheduler;

        private ScheduleListener(Scheduler scheduler) {
            this.scheduler = scheduler;
        }

        @Override
        public void afterInjection(final I injectee) {
            scheduler.schedule(injectee);
        }
    }

    private Module constructModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                Scheduler scheduler = new Scheduler();
                requestInjection(scheduler);
                bindListener(Matchers.any(), new SchedulableListener(scheduler));
                bind(Scheduler.class).toInstance(scheduler);
            }
        };
    }
}
