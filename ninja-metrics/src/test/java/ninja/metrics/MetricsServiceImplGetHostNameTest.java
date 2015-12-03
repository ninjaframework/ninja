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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MetricsServiceImpl.class})

public class MetricsServiceImplGetHostNameTest extends MetricsServiceImplTestSupport {

    @Test
    public void testGetHostName_WhenINetAddressCallWorks() throws Exception {
        //setup
        setupForGetHostName("myhostname", null, null);
        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);

        //when
        String hostName = metricsService.getHostname();

        //then
        assertEquals("myhostname", hostName);
    }


    @Test
    public void testGetHostName_WhenInetAddressIsNullAndComputerNameIsSet() throws Exception {
        //setup
        setupForGetHostName(null, "SetThroughEnvCOMPUTERNAME", null);
        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);

        //when
        String hostName = metricsService.getHostname();

        //then
        assertEquals("SetThroughEnvCOMPUTERNAME", hostName);
    }

    @Test
    public void testGetHostName_WhenInetAddressIsNullAndHostNameIsSet() throws Exception {
        //setup
        setupForGetHostName(null, null, "SetThroughEnvHOSTNAME");
        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);

        //when
        String hostName = metricsService.getHostname();

        //then
        assertEquals("SetThroughEnvHOSTNAME", hostName);
    }

    @Test
    public void testGetHostName_WhenNothingIsSet() throws Exception {
        //setup
        setupForGetHostName(null, null, null);
        MetricsServiceImpl metricsService = new MetricsServiceImpl(metricRegistry, ninjaProps);

        //when
        String hostName = metricsService.getHostname();

        //then
        assertEquals("Ninja", hostName);
    }
}
