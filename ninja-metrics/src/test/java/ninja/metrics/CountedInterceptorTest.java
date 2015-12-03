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

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import ninja.metrics.dummy.DummyCountedController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.getMethod;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricRegistry.class})
public class CountedInterceptorTest extends InterceptorTestSupport {

    private CountedInterceptor countedInterceptor;

    private Counter counter;

    @Before
    public void setup() {
        super.setup();
        counter = new Counter();
        counter.inc(INITIAL_COUNTER);
        when(metricRegistry.counter("dummyValue")).thenReturn(counter);
        countedInterceptor = new CountedInterceptor(metricsServiceProvider);
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        assertEquals("metricsServiceProvider must be set in the field variable",
                metricsServiceProvider, getInternalState(countedInterceptor, "metricsServiceProvider"));
    }

    @Test(expected = NullPointerException.class)
    public void testInvoke_WithoutCountedAnnotation() throws Throwable {
        Method method = getMethod(DummyCountedController.class, "dummyMethodWithoutCountedAnnotation");
        when(methodInvocation.getMethod()).thenReturn(method);

        countedInterceptor.invoke(methodInvocation);
    }

    @Test
    public void testInvoke_WithCountedAnnotation() throws Throwable {
        Method method = getMethod(DummyCountedController.class, "dummyMethodWithCountedAnnotation");
        when(methodInvocation.getThis()).thenReturn(new DummyCountedController());
        when(methodInvocation.getMethod()).thenReturn(method);

        countedInterceptor.invoke(methodInvocation);

        assertEquals(INITIAL_COUNTER + 1, counter.getCount());
        verify(methodInvocation).proceed();
    }

    @Test
    public void testInvoke_withCountedInactive() throws Throwable {
        Method method = getMethod(DummyCountedController.class, "dummyMethodWithCountedAnnotationAndValue");
        when(methodInvocation.getMethod()).thenReturn(method);

        countedInterceptor.invoke(methodInvocation);

        assertEquals(INITIAL_COUNTER + 1, counter.getCount());
        verify(methodInvocation).proceed();
    }

    @Test
    public void testInvoke_withCountedActive() throws Throwable {
        Method method = getMethod(DummyCountedController.class, "dummyMethodWithCountedAnnotationAndValueAndActive");
        when(methodInvocation.getMethod()).thenReturn(method);

        countedInterceptor.invoke(methodInvocation);

        assertEquals("Counter should remain same as before on execution completion", INITIAL_COUNTER, counter.getCount());
        verify(methodInvocation).proceed();
    }
}
