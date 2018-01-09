/**
 * Copyright (C) 2012-2018 the original author or authors.
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

import info.ganglia.gmetric4j.gmetric.GMetric;
import info.ganglia.gmetric4j.gmetric.GMetric.UDPAddressingMode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.metrics.MetricsService;
import ninja.utils.NinjaProperties;
import ninja.utils.TimeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.ganglia.GangliaReporter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Integration of Ninja Metrics with Ganglia.
 *
 * @author James Moger
 */
@Singleton
public class NinjaGanglia {

    private final Logger log = LoggerFactory.getLogger(NinjaGanglia.class);

    private final NinjaProperties ninjaProperties;

    private final MetricsService metricsService;

    private GangliaReporter reporter;

    @Inject
    public NinjaGanglia(NinjaProperties ninjaProperties,
                        MetricsService metricsService) {
        this.ninjaProperties = ninjaProperties;
        this.metricsService = metricsService;
    }

    @Start(order = 90)
    public void start() {

        if (ninjaProperties.getBooleanWithDefault("metrics.ganglia.enabled",
                false)) {

            final String hostname = metricsService.getHostname();
            final String address = ninjaProperties
                    .getOrDie("metrics.ganglia.address");
            final int port = ninjaProperties.getIntegerWithDefault(
                    "metrics.ganglia.port", 8649);
            final String period = ninjaProperties.getWithDefault(
                    "metrics.ganglia.period", "60s");
            final int delay = TimeUtil.parseDuration(period);

            try {
                GMetric ganglia = new GMetric(address, port,
                        UDPAddressingMode.MULTICAST, 1);
                reporter = GangliaReporter
                        .forRegistry(metricsService.getMetricRegistry())
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build(ganglia);

                reporter.start(delay, TimeUnit.SECONDS);

                log.info(
                        "Started Ganglia Metrics reporter for '{}', updating every {}",
                        hostname, period);

            } catch (IOException e) {
                log.error("Failed to start Ganglia reporter!", e);
            }

        }
    }

    @Dispose(order = 10)
    public void stop() {

        if (reporter != null) {

            reporter.stop();

            log.debug("Stopped Ganglia Metrics reporter");
        }

    }
}
