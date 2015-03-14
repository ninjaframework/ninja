/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.lifecycle;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;

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
