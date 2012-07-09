package ninja.lifecycle;

import com.google.inject.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class LifecycleSupportTest {

    @Before
    public void setUp() {
        MockSingletonService.started = 0;
        MockService.started = 0;
    }

    @Test
    public void serviceShouldNotBeStartedBeforeLifecycleServiceIsStarted() {
        createInjector().getInstance(MockService.class);
        assertThat(MockService.started, equalTo(0));
    }

    @Test
    public void serviceShouldBeStartedWhenLifecycleServiceIsStarted() {
        Injector injector = createInjector();
        injector.getInstance(MockService.class);
        start(injector);
        assertThat(MockService.started, equalTo(1));
    }

    @Test
    public void serviceShouldBeStartedIfAccessedAfterLifecycleServiceIsStarted() {
        Injector injector = createInjector();
        start(injector);
        injector.getInstance(MockService.class);
        assertThat(MockService.started, equalTo(1));
    }

    @Test
    public void serviceShouldBeStartedIfExplicitlyBoundAndSingleton() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MockSingletonService.class);
            }
        });
        start(injector);
        assertThat(MockSingletonService.started, equalTo(1));
    }

    @Test
    public void serviceShouldBeStartedIfExplicitlyBoundAsSingleton() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MockService.class).toInstance(new MockService());
            }
        });
        start(injector);
        assertThat(MockService.started, equalTo(1));
    }

    @Test
    public void serviceShouldNotBeStartedIfExplicitlyBoundAndNotSingleton() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MockService.class);
            }
        });
        start(injector);
        assertThat(MockService.started, equalTo(0));
    }

    @Test
    public void singletonServiceShouldNotBeStartedTwice() {
        Injector injector = createInjector();
        injector.getInstance(MockSingletonService.class);
        injector.getInstance(MockSingletonService.class);
        start(injector);
        assertThat(MockSingletonService.started, equalTo(1));
    }

    @Test
    public void nonSingletonServicesShouldBeInstantiatedForEachInstance() {
        Injector injector = createInjector();
        injector.getInstance(MockService.class);
        injector.getInstance(MockService.class);
        start(injector);
        assertThat(MockService.started, equalTo(2));
    }

    @Test
    public void disposablesShouldBeDisposedOf() {
        Injector injector = createInjector();
        injector.getInstance(MockService.class);
        start(injector);
        stop(injector);
        assertThat(MockService.disposed, equalTo(1));
    }

    @Test
    public void providedSingletonStartableShouldBeStarted() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }
            @Provides
            @Singleton
            public MockSingletonService provide() {
                return new MockSingletonService();
            }
        });
        start(injector);
        assertThat(MockSingletonService.started, equalTo(1));
    }

    @Test
    public void providedSingletonDisposableShouldBeDisposed() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }
            @Provides
            @Singleton
            public MockSingletonService provide() {
                return new MockSingletonService();
            }
        });
        start(injector);
        stop(injector);
        assertThat(MockSingletonService.disposed, equalTo(1));
    }

    private Injector createInjector(Module... modules) {
        List<Module> ms = new ArrayList<Module>(Arrays.asList(modules));
        ms.add(LifecycleSupport.getModule());
        return Guice.createInjector(ms);
    }

    private void start(Injector injector) {
        injector.getInstance(LifecycleService.class).start();
    }

    private void stop(Injector injector) {
        injector.getInstance(LifecycleService.class).stop();
    }

    @Singleton
    public static class MockSingletonService {
        static int started;
        static int disposed;
        @Start
        public void start() {
            started++;
        }
        @Dispose
        public void dispose() {
            disposed++;
        }
    }

    public static class MockService {
        static int started;
        static int disposed;
        @Start
        public void start() {
            started++;
        }
        @Dispose
        public void dispose() {
            disposed++;
        }
    }

}
