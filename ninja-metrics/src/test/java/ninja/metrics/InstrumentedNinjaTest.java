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
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import ninja.lifecycle.LifecycleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class InstrumentedNinjaTest {

    private MetricsService metricsService;

    private LifecycleService lifecycleService;

    private Meter allRequestsMeter;

    private Counter activeRequests;

    private Meter badRequestsMeter;

    private Meter internalServerErrors;

    private Meter routesNotFound;

    @Before
    public void setUp() throws Exception {
        metricsService = mock(MetricsService.class);
        lifecycleService = mock(LifecycleService.class);
        MetricRegistry metricRegistry = mock(MetricRegistry.class);
        PowerMockito.mockStatic(MetricRegistry.class);
        Mockito.when(metricsService.getMetricRegistry()).thenReturn(metricRegistry);
        allRequestsMeter = mock(Meter.class);
        activeRequests = mock(Counter.class);
        badRequestsMeter = mock(Meter.class);
        internalServerErrors = mock(Meter.class);
        routesNotFound = mock(Meter.class);
//        router = mock(Router.class);

        when(metricRegistry.meter(MetricsService.METER_ALL_REQUESTS)).thenReturn(allRequestsMeter);
        when(metricRegistry.counter(MetricsService.COUNTER_ACTIVE_REQUESTS)).thenReturn(activeRequests);
        when(metricRegistry.meter(MetricsService.METER_BAD_REQUESTS)).thenReturn(badRequestsMeter);
        when(metricRegistry.meter(MetricsService.METER_INTERNAL_SERVER_ERRORS)).thenReturn(internalServerErrors);
        when(metricRegistry.meter(MetricsService.METER_ROUTES_NOT_FOUND)).thenReturn(routesNotFound);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testOnFrameworkStart() throws Exception {
        //setup
        InstrumentedNinja instrumentedNinja = new InstrumentedNinja();
        setInternalState(instrumentedNinja, "metricsService", metricsService);
        setInternalState(instrumentedNinja, "lifecycleService", lifecycleService);

        //when
        instrumentedNinja.onFrameworkStart();

        //then
        assertEquals(allRequestsMeter, getInternalState(instrumentedNinja, "allRequestsMeter"));
        assertEquals(activeRequests, getInternalState(instrumentedNinja, "activeRequests"));
        assertEquals(badRequestsMeter, getInternalState(instrumentedNinja, "badRequests"));
        assertEquals(internalServerErrors, getInternalState(instrumentedNinja, "internalServerErrors"));
        assertEquals(routesNotFound, getInternalState(instrumentedNinja, "routesNotFound"));
    }
}
