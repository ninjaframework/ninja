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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricsModule.class, TimedInterceptor.class, MeteredInterceptor.class, CountedInterceptor.class})

public class MetricsModuleTest extends MetricsModuleTestSupport {

    @Before
    public void setUp() throws Exception {
        mockProvider();
        mockInterceptors();
        mockBuilders();
        mockBinder();
        mockMetricRegistry();
    }

    @Test
    public void testConfigure() throws Exception {
        //setup
        MetricsModule metricsModule = new MetricsModule();

        //when
        metricsModule.configure(binder);

        //then
        verifyPrivate(binder).invoke("bind", MetricRegistry.class);
        verifyPrivate(binder).invoke("bindInterceptor", any(), annotatedWith(Timed.class), timedInterceptor);
        verifyPrivate(binder).invoke("bindInterceptor", any(), annotatedWith(Metered.class), meteredInterceptor);
        verifyPrivate(binder).invoke("bindInterceptor", any(), annotatedWith(Counted.class), countedInterceptor);
        verifyPrivate(metricServiceBuilder).invoke("to", MetricsServiceImpl.class);
        verifyPrivate(metricRegistryBuilder).invoke("toInstance", metricRegistry);
    }
}
