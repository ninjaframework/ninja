/**
 * Copyright (C) the original author or authors.
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

package ninja.metrics.graphite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteReporter.Builder;
import com.codahale.metrics.graphite.GraphiteSender;

import ninja.metrics.MetricsService;
import ninja.utils.NinjaPropertiesImpl;

@PrepareForTest(GraphiteReporter.class)
@RunWith(PowerMockRunner.class)
public class NinjaGraphiteTest {

    private static final String TEST_HOSTNAME = "test.hostname";
    private static final String TEST_PREFIX = "my.prefix";

    @Mock
    private NinjaPropertiesImpl ninjaProperties;

    @Mock
    private MetricsService metricsService;

    @Mock
    private Builder builder;

    private NinjaGraphite ninjaGraphite;

    @Before
    public final void setup() {

        // configure mocks
        MockitoAnnotations.initMocks(this);
        Mockito.when(metricsService.getHostname()).thenReturn(TEST_HOSTNAME);
        Mockito.when(ninjaProperties.getBooleanWithDefault(Mockito.eq("metrics.graphite.enabled"), Mockito.anyBoolean())).thenReturn(Boolean.TRUE);
        Mockito.when(ninjaProperties.getOrDie(Mockito.eq("metrics.graphite.address"))).thenReturn("localhost");
        Mockito.when(ninjaProperties.getIntegerWithDefault(Mockito.eq("metrics.graphite.port"), Mockito.anyInt())).thenReturn(2003);
        Mockito.when(ninjaProperties.getBooleanWithDefault(Mockito.eq("metrics.graphite.pickled"), Mockito.anyBoolean())).thenReturn(Boolean.FALSE);
        Mockito.when(ninjaProperties.getWithDefault(Mockito.eq("metrics.graphite.period"), Mockito.anyString())).thenReturn("60s");

        PowerMockito.mockStatic(GraphiteReporter.class);
        PowerMockito.when(GraphiteReporter.forRegistry(Mockito.any())).thenReturn(builder);
        Mockito.when(builder.prefixedWith(Mockito.anyString())).thenCallRealMethod();
        Mockito.when(builder.convertRatesTo(Mockito.any())).thenCallRealMethod();
        Mockito.when(builder.convertDurationsTo(Mockito.any())).thenCallRealMethod();
        Mockito.when(builder.filter(Mockito.any())).thenCallRealMethod();
        Mockito.when(builder.build(Mockito.any(GraphiteSender.class))).thenCallRealMethod();

        // create instance under test
        ninjaGraphite = new NinjaGraphite(ninjaProperties, metricsService);
    }

    @Test
    public void testWithoutCustomPrefix() {
        Mockito.when(ninjaProperties.get(Mockito.eq("metrics.graphite.prefix"))).thenReturn(null);
        ninjaGraphite.start();
        Mockito.verify(builder, Mockito.times(1)).prefixedWith(TEST_HOSTNAME);
    }

    @Test
    public void testWithEmptyCustomPrefix() {
        Mockito.when(ninjaProperties.get(Mockito.eq("metrics.graphite.prefix"))).thenReturn("");
        ninjaGraphite.start();
        Mockito.verify(builder, Mockito.times(1)).prefixedWith(TEST_HOSTNAME);
    }

    @Test
    public void testWithCustomPrefix() {
        Mockito.when(ninjaProperties.get(Mockito.eq("metrics.graphite.prefix"))).thenReturn(TEST_PREFIX);
        ninjaGraphite.start();
        Mockito.verify(builder, Mockito.times(1)).prefixedWith(TEST_PREFIX);
    }

    @After
    public final void teardown() {
        if (ninjaGraphite != null) {
            ninjaGraphite.stop();
        }
    }
}
