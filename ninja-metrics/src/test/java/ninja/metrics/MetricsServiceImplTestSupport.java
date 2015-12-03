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
import ninja.utils.NinjaProperties;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricsServiceImpl.class})

public class MetricsServiceImplTestSupport {

    protected MetricsServiceImpl metricsServiceImpl;

    protected NinjaProperties ninjaProps;

    protected MetricRegistry metricRegistry;

    protected List<Closeable> reporters = new ArrayList<>();

    @Before
    public void before() {
        ninjaProps = mock(NinjaProperties.class);
        metricRegistry = mock(MetricRegistry.class);
        metricsServiceImpl = new MetricsServiceImpl(metricRegistry, ninjaProps);
        when(ninjaProps.getBooleanWithDefault("metrics.jvm.enabled", false)).thenReturn(true);
    }

    protected void setupForGetHostName(String inetAddressHostName, String computerName, String hsotname) throws UnknownHostException {
        // Mocked the InetAddress class through Powermockito
        PowerMockito.mockStatic(InetAddress.class);
        InetAddress inetAddress = mock(InetAddress.class);
        when(inetAddress.getHostName()).thenReturn(inetAddressHostName);
        PowerMockito.when(InetAddress.getLocalHost()).thenReturn(inetAddress);

        PowerMockito.mockStatic(System.class);
        when(System.getenv("COMPUTERNAME")).thenReturn(computerName);
        when(System.getenv("HOSTNAME")).thenReturn(hsotname);
    }
}
