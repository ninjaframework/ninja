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

package ninja.metrics.ganglia;

import com.codahale.metrics.ganglia.GangliaReporter;
import info.ganglia.gmetric4j.gmetric.GMetric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GMetric.class, GangliaReporter.class, GangliaReporter.Builder.class, NinjaGanglia.class})
public class NinjaGangliaTest extends NinjaGangliaTestSupport {

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(GangliaReporter.class);
        PowerMockito.mockStatic(NinjaGanglia.class);
        mockReporter();
        mockMetricService();
        mockNinjaProperties();
        mockGMetric();
        mockMetricRegistry();
        mockGangliaReporterBuilder();
    }

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        //when
        ninjaGanglia = new NinjaGanglia(ninjaProperties, metricsService);

        //then
        assertEquals(ninjaProperties, Whitebox.getInternalState(ninjaGanglia, "ninjaProperties"));
        assertEquals(metricsService, Whitebox.getInternalState(ninjaGanglia, "metricsService"));
    }

    @Test
    public void testStart_withGangliaDisabled() throws Exception {
        //setup
        ninjaGanglia = new NinjaGanglia(ninjaProperties, metricsService);
        Mockito.when(ninjaProperties.getBooleanWithDefault("metrics.ganglia.enabled", false)).thenReturn(false);

        //when
        ninjaGanglia.start();

        //then: Verifying that the start method was not called when the config has been set to false
        Mockito.verify(reporter, Mockito.times(0)).start(Matchers.anyLong(), Matchers.any(TimeUnit.class));
    }

    @Test
    public void testStart_withGangliaEnabled() throws Exception {
        //setup
        ninjaGanglia = new NinjaGanglia(ninjaProperties, metricsService);

        //when
        ninjaGanglia.start();

        //then
        Mockito.verify(metricsService).getHostname();
        Mockito.verify(ninjaProperties).getOrDie("metrics.ganglia.address");
        Mockito.verify(ninjaProperties).getIntegerWithDefault("metrics.ganglia.port", 8649);
        Mockito.verify(ninjaProperties).getWithDefault("metrics.ganglia.period", "60s");
        PowerMockito.verifyNew(GMetric.class).withArguments("www.google.com", 8080, GMetric.UDPAddressingMode.MULTICAST, 1);
        Mockito.verify(reporter).start(70, TimeUnit.SECONDS);
    }

    @Test
    public void testStart_withGangliaEnabled_IOException() throws Exception {
        //setup
        ninjaGanglia = new NinjaGanglia(ninjaProperties, metricsService);
        PowerMockito.whenNew(GMetric.class).withArguments("www.google.com", 8080, GMetric.UDPAddressingMode.MULTICAST, 1).thenThrow(new IOException("A dummy IOException"));

        //when
        ninjaGanglia.start();

        //then
        Mockito.verify(metricsService).getHostname();
        Mockito.verify(ninjaProperties).getOrDie("metrics.ganglia.address");
        Mockito.verify(ninjaProperties).getIntegerWithDefault("metrics.ganglia.port", 8649);
        Mockito.verify(ninjaProperties).getWithDefault("metrics.ganglia.period", "60s");
        PowerMockito.verifyNew(GMetric.class).withArguments("www.google.com", 8080, GMetric.UDPAddressingMode.MULTICAST, 1);
        Mockito.verify(reporter, Mockito.times(0)).start(70, TimeUnit.SECONDS);
    }

    @Test
    public void testStop() throws Exception {
        //setup
        ninjaGanglia = new NinjaGanglia(ninjaProperties, metricsService);
        Whitebox.setInternalState(ninjaGanglia, "reporter", reporter);

        //when
        ninjaGanglia.stop();

        //then
        Mockito.verify(reporter).stop();
    }
}
