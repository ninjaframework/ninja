/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ninja.lifecycle.FailedStartException;
import ninja.lifecycle.LifecycleService;
import ninja.lifecycle.LifecycleSupport;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class SchedulerSupportTest {

    private Injector injector;

    @Before
    public void setUp() {
        MockScheduled.countDownLatch = new CountDownLatch(1);
        MockPropertyScheduled.countDownLatch = new CountDownLatch(1);
    }

    @After
    public void tearDown() {
        if (injector != null) {
            stop(injector);
        }
    }

    @Test
    public void schedulableShouldNotBeScheduledBeforeStart() throws Exception {
        injector = createInjector();
        injector.getInstance(MockScheduled.class);
        Thread.sleep(100);
        assertThat(MockScheduled.countDownLatch.getCount(), equalTo(1L));
    }

    @Test(timeout = 5000)
    public void schedulableShouldBeScheduledAfterStart() throws Exception {
        injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(MockScheduled.class);
            }
        });
        start(injector);
        MockScheduled.countDownLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 5000)
    public void schedulableAddedAfterStartShouldBeScheduledImmediately() throws Exception {
        injector = createInjector();
        start(injector);
        injector.getInstance(MockScheduled.class);
        MockScheduled.countDownLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 5000)
    public void schedulableShouldUsePropertyConfig() throws Exception {
        injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Key.get(String.class, Names.named("delay.property"))).toInstance("10");
            }
        });
        injector.getInstance(MockPropertyScheduled.class);
        start(injector);
        MockPropertyScheduled.countDownLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test(expected = FailedStartException.class)
    public void schedulableShouldThrowExceptionWhenNoPropertyFound() throws Exception {
        injector = createInjector();
        injector.getInstance(MockPropertyScheduled.class);
        start(injector);
    }

    private Injector createInjector(Module... modules) {
        List<Module> ms = new ArrayList<Module>(Arrays.asList(modules));
        ms.add(LifecycleSupport.getModule());
        ms.add(SchedulerSupport.getModule());
        return Guice.createInjector(ms);
    }

    private void start(Injector injector) {
        injector.getInstance(LifecycleService.class).start();
    }

    private void stop(Injector injector) {
        if (injector.getInstance(LifecycleService.class).isStarted()) {
            injector.getInstance(LifecycleService.class).stop();
        }
    }

    @Singleton
    public static class MockScheduled {
        static CountDownLatch countDownLatch;

        @Schedule(initialDelay = 10, delay = 1000000000)
        public void doSomething() {
            countDownLatch.countDown();
        }
    }

    @Singleton
    public static class MockPropertyScheduled {
        static CountDownLatch countDownLatch;

        @Schedule(delayProperty = "delay.property")
        public void doSomething() {
            countDownLatch.countDown();
        }
    }

}
