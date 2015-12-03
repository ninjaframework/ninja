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

package ninja.metrics;

import com.codahale.metrics.Timer;
import com.google.inject.Provider;
import ninja.metrics.dummy.DummyTimedController;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TimedInterceptorUnitTest extends InterceptorTestSupport {

    private Timer timer;

    @Before
    public void setup() {
        super.setup();
        timer = new Timer();
        when(metricRegistry.timer("dummyValue")).thenReturn(timer);
    }

    @Test
    public void testInvoke() throws Throwable {
        //setup
        TimedInterceptor timedInterceptor = new TimedInterceptor(metricsServiceProvider);
        assertEquals("Constructor must have set the private field",
                metricsServiceProvider,
                (Provider<MetricsService>) Whitebox.getInternalState(timedInterceptor, "metricsServiceProvider"));

        mockGetMthodOnMethodInvocation(DummyTimedController.class, "dummyMethodWithTimedAnnotation");
        assertEquals("Meter count must be zero", 0, timer.getCount());

        //when
        timedInterceptor.invoke(methodInvocation);

        //then
        assertEquals("Meter count must increase by two", 2, timer.getCount());
        verify(methodInvocation).proceed();
    }
}
