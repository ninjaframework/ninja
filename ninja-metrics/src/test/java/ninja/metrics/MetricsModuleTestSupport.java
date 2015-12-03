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

import com.codahale.metrics.MetricRegistry;
import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.binder.AnnotatedBindingBuilder;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricsModule.class, TimedInterceptor.class, MeteredInterceptor.class, CountedInterceptor.class})

public class MetricsModuleTestSupport {

    protected TimedInterceptor timedInterceptor;

    protected MeteredInterceptor meteredInterceptor;

    protected CountedInterceptor countedInterceptor;

    protected Provider<MetricsService> metricsServiceProvider;

    protected Binder binder;

    protected AnnotatedBindingBuilder<MetricRegistry> metricRegistryBuilder;

    protected AnnotatedBindingBuilder<MetricsService> metricServiceBuilder;

    protected MetricRegistry metricRegistry;

    protected void mockMetricRegistry() throws Exception {
        metricRegistry = mock(MetricRegistry.class);
        whenNew(MetricRegistry.class).withAnyArguments().thenReturn(metricRegistry);
    }

    protected void mockBinder() {
        binder = mock(Binder.class);
        when(binder.getProvider(MetricsService.class)).thenReturn(metricsServiceProvider);
        when(binder.bind(MetricRegistry.class)).thenReturn(metricRegistryBuilder);
        when(binder.bind(MetricsService.class)).thenReturn(metricServiceBuilder);
        when(binder.bind(MetricRegistry.class)).thenReturn(metricRegistryBuilder);
    }

    protected void mockBuilders() {
        metricRegistryBuilder = (AnnotatedBindingBuilder<MetricRegistry>)
                mock(AnnotatedBindingBuilder.class);
        metricServiceBuilder = (AnnotatedBindingBuilder<MetricsService>)
                mock(AnnotatedBindingBuilder.class);
    }

    protected void mockInterceptors() throws Exception {
        timedInterceptor = mock(TimedInterceptor.class);
        meteredInterceptor = mock(MeteredInterceptor.class);
        countedInterceptor = mock(CountedInterceptor.class);

//        Mocking the constructors using PowerMockito
        whenNew(TimedInterceptor.class).withArguments(metricsServiceProvider).thenReturn(timedInterceptor);
        whenNew(MeteredInterceptor.class).withArguments(metricsServiceProvider).thenReturn(meteredInterceptor);
        whenNew(CountedInterceptor.class).withArguments(metricsServiceProvider).thenReturn(countedInterceptor);
    }

    protected void mockProvider() {
        metricsServiceProvider = (Provider<MetricsService>) mock(Provider.class);
    }
}
