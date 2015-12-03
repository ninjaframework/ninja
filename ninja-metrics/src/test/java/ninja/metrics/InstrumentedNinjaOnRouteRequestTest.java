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
import ninja.Context;
import ninja.FilterChain;
import ninja.Result;
import ninja.Route;
import ninja.Router;
import ninja.exceptions.BadRequestException;
import ninja.lifecycle.LifecycleService;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class InstrumentedNinjaOnRouteRequestTest {

    private Meter allRequestsMeter;

    private Counter activeRequests;

    private Meter badRequests;

    private Meter internalServerErrors;

    private Meter routesNotFound;

    private Router router;

    private InstrumentedNinja instrumentedNinja;

    private ResultHandler resultHandler;

    private Context.Impl ctx;

    @Before
    public void setUp() throws Exception {
        MetricsService metricsService = mock(MetricsService.class);
        LifecycleService lifecycleService = mock(LifecycleService.class);
        NinjaProperties ninjaProperties = mock(NinjaProperties.class);
        ctx = mock(Context.Impl.class);
        MetricRegistry metricRegistry = mock(MetricRegistry.class);
        PowerMockito.mockStatic(MetricRegistry.class);
        when(metricsService.getMetricRegistry()).thenReturn(metricRegistry);
        allRequestsMeter = spy(new Meter());
        badRequests = spy(new Meter());
        activeRequests = mock(Counter.class);
        internalServerErrors = spy(new Meter());
        routesNotFound = spy(new Meter());
        router = mock(Router.class);
        instrumentedNinja = new InstrumentedNinja();
        resultHandler = mock(ResultHandler.class);

        Route route = mock(Route.class);
        FilterChain filterChain = mock(FilterChain.class);
        when(route.getFilterChain()).thenReturn(filterChain);
        when(router.getRouteFor(anyString(), anyString())).thenReturn(route);
        when(ninjaProperties.isDev()).thenReturn(true);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.DIAGNOSTICS_KEY_NAME, Boolean.TRUE)).thenReturn(true);

        setInternalState(instrumentedNinja, "lifecycleService", lifecycleService);
        setInternalState(instrumentedNinja, "router", router);
        setInternalState(instrumentedNinja, "ninjaProperties", ninjaProperties);
        setInternalState(instrumentedNinja, "resultHandler", resultHandler);
        setInternalState(instrumentedNinja, "metricsService", metricsService);
        setInternalState(instrumentedNinja, "activeRequests", activeRequests);
        setInternalState(instrumentedNinja, "allRequestsMeter", allRequestsMeter);
        setInternalState(instrumentedNinja, "badRequests", badRequests);
        setInternalState(instrumentedNinja, "internalServerErrors", internalServerErrors);
        setInternalState(instrumentedNinja, "routesNotFound", routesNotFound);
    }

    @Test
    public void testHappyFlow() throws Exception {
        //when
        instrumentedNinja.onRouteRequest(ctx);

        //then
        verify(activeRequests).inc();
        verify(activeRequests).dec();
        assertEquals(1, allRequestsMeter.getCount());
        verify(resultHandler).handleResult(any(Result.class), any(Context.class));
    }

    @Test
    public void testForRouteNotFoundScenario() throws Exception {
        //setup
        when(router.getRouteFor(anyString(), anyString())).thenReturn(null);

        //when
        instrumentedNinja.onRouteRequest(ctx);

        //then
        verify(activeRequests).inc();
        verify(activeRequests).dec();
        assertEquals(0, allRequestsMeter.getCount());
        assertEquals(1, routesNotFound.getCount());
    }


    @Test
    public void testForBadRequestException() throws Exception {
        //setup
        doThrow(new BadRequestException()).when(resultHandler).handleResult(any(Result.class), any(Context.class));

        //when
        instrumentedNinja.onRouteRequest(ctx);

        //then
        verify(activeRequests).inc();
        verify(activeRequests).dec();
        assertEquals(1, allRequestsMeter.getCount());
        assertEquals(1, badRequests.getCount());
        assertEquals(0, routesNotFound.getCount());
    }

    @Test
    public void testForUnknownException() throws Exception {
        //setup
        doThrow(new RuntimeException("Unknown exception")).when(resultHandler).handleResult(any(Result.class), any(Context.class));

        //when
        instrumentedNinja.onRouteRequest(ctx);

        //then
        verify(activeRequests).inc();
        verify(activeRequests).dec();
        assertEquals(1, allRequestsMeter.getCount());
        assertEquals(0, badRequests.getCount());
        assertEquals(1, internalServerErrors.getCount());
        assertEquals(0, routesNotFound.getCount());
    }
}
