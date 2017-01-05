/**
 * Copyright (C) 2012-2017 the original author or authors.
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

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.metrics.MetricsService;
import ninja.utils.NinjaProperties;
import ninja.utils.TimeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.PickledGraphite;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Integration of Ninja Metrics with Graphite.
 *
 * @author James Moger
 */
@Singleton
public class NinjaGraphite {

    private final Logger log = LoggerFactory.getLogger(NinjaGraphite.class);

    private final NinjaProperties ninjaProperties;

    private final MetricsService metricsService;

    private GraphiteReporter reporter;

    @Inject
    public NinjaGraphite(NinjaProperties ninjaProperties,
                         MetricsService metricsService) {
        this.ninjaProperties = ninjaProperties;
        this.metricsService = metricsService;
    }

    @Start(order = 90)
    public void start() {

        if (ninjaProperties.getBooleanWithDefault("metrics.graphite.enabled",
                false)) {

            final String hostname = metricsService.getHostname();
            final String address = ninjaProperties
                    .getOrDie("metrics.graphite.address");
            final int port = ninjaProperties.getIntegerWithDefault(
                    "metrics.graphite.port", 2003);
            final boolean isPickled = ninjaProperties.getBooleanWithDefault(
                    "metrics.graphite.pickled", false);
            final String period = ninjaProperties.getWithDefault(
                    "metrics.graphite.period", "60s");
            final int delay = TimeUtil.parseDuration(period);
            final InetSocketAddress graphiteAddress = new InetSocketAddress(
                    address, port);

            final GraphiteSender sender;
            if (isPickled) {
                sender = new PickledGraphite(graphiteAddress);
            } else {
                sender = new Graphite(graphiteAddress);
            }

            reporter = GraphiteReporter
                    .forRegistry(metricsService.getMetricRegistry())
                    .prefixedWith(hostname).convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL).build(sender);

            reporter.start(delay, TimeUnit.SECONDS);

            log.info(
                    "Started Graphite Metrics reporter for '{}', updating every {}",
                    hostname, period);

        }
    }

    @Dispose(order = 10)
    public void stop() {

        if (reporter != null) {

            reporter.stop();

            log.debug("Stopped Graphite Metrics reporter");
        }

    }
}
