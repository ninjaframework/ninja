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

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import ninja.utils.NinjaConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Closeable;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.util.reflection.Whitebox.getInternalState;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricsServiceImpl.class, JmxReporter.class})
public class MetricsServiceImplTest extends MetricsServiceImplTestSupport {

    private JmxReporter jmxReporter;

    @Test
    public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
        //when
        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);

        //then
        assertNotNull(ninjaProps);
        assertNotNull(metricRegistry);
        assertEquals(ninjaProps, getInternalState(metricsService, "ninjaProps"));
        assertEquals(metricRegistry, getInternalState(metricsService, "metricRegistry"));
        assertNotNull(getInternalState(metricsService, "reporters"));
    }

    @Test
    public void testStop() throws Exception {
        //setup
        reporters = new ArrayList<>();
        Closeable reporter1 = PowerMockito.mock(Closeable.class);
        PowerMockito.doNothing().when(reporter1, "close");
        reporters.add(reporter1);
        Closeable reporter2 = PowerMockito.mock(Closeable.class);
        PowerMockito.doNothing().when(reporter2, "close");
        reporters.add(reporter2);

        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);
        setInternalState(metricsService, "reporters", reporters);

        //when
        metricsService.stop();

        //then
        Mockito.verify(reporter1).close();
        Mockito.verify(reporter2).close();
    }

    public void setupForStart() {
        when(ninjaProps.getWithDefault(NinjaConstant.applicationName, "Ninja")).thenReturn("MyNinja");
        when(ninjaProps.getBooleanWithDefault("metrics.jvm.enabled", false)).thenReturn(true);
        when(ninjaProps.getBooleanWithDefault("metrics.logback.enabled", false)).thenReturn(true);
        when(ninjaProps.getBooleanWithDefault("metrics.mbeans.enabled", true)).thenReturn(true);

        mockStatic(JmxReporter.class);
        jmxReporter = mock(JmxReporter.class);
        JmxReporter.Builder builder = mock(JmxReporter.Builder.class);
        when(builder.build()).thenReturn(jmxReporter);
        when(builder.inDomain("MyNinja")).thenReturn(builder);
        when(JmxReporter.forRegistry(metricRegistry)).thenReturn(builder);
        assertEquals(jmxReporter, JmxReporter.forRegistry(metricRegistry).inDomain("MyNinja").build());
    }

    @Test
    public void testStart() throws NoSuchFieldException, IllegalAccessException {
        //setup
        setupForStart();
        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);
        setInternalState(metricsService, "reporters", reporters);

        //when
        metricsService.start();

        //then
        verify(ninjaProps).getWithDefault(NinjaConstant.applicationName, "Ninja");
        verify(ninjaProps).getBooleanWithDefault("metrics.jvm.enabled", false);
        verify(metricRegistry, times(36)).register(anyString(), any(Metric.class));
        verify(metricRegistry, times(4)).register(startsWith("jvm.gc"), any(Metric.class));
        verify(jmxReporter).start();
        assertEquals(reporters.get(0), jmxReporter);
    }
}
