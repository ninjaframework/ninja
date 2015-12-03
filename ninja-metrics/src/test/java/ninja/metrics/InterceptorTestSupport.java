/**
 * Copyright (C) 2012-2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.metrics;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Provider;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Method;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.internal.WhiteboxImpl.getMethod;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricRegistry.class})
public class InterceptorTestSupport {

    protected Provider<MetricsService> metricsServiceProvider;

    protected MethodInvocation methodInvocation;

    protected MetricRegistry metricRegistry;

    protected MetricsService metricsService;

    protected static final long INITIAL_COUNTER = 10;

    @Before
    public void setup() {
        metricsService = mock(MetricsService.class);
        metricRegistry = mock(MetricRegistry.class);
        mockStatic(MetricRegistry.class); //PowerMockito allows to mock the static methods
        Mockito.when(metricsService.getMetricRegistry()).thenReturn(metricRegistry);
        metricsServiceProvider = (Provider<MetricsService>) mock(Provider.class);
        when(metricsServiceProvider.get()).thenReturn(metricsService);
        when(MetricRegistry.name(any(Class.class), anyString())).thenReturn("dummyValue");
        methodInvocation = PowerMockito.mock(MethodInvocation.class);
    }

    protected void mockGetMthodOnMethodInvocation(Class clazz, String methodName) throws NoSuchFieldException, IllegalAccessException, InstantiationException {
        when(methodInvocation.getThis()).thenReturn(clazz.newInstance());
        Method method = getMethod(clazz, methodName);
        when(methodInvocation.getMethod()).thenReturn(method);
    }
}
