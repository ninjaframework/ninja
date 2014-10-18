/**
 * Copyright (C) 2012-2014 the original author or authors.
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

package ninja.metrics.influxdb;

import java.util.concurrent.TimeUnit;

import metrics_influxdb.Influxdb;
import metrics_influxdb.InfluxdbReporter;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.metrics.MetricsService;
import ninja.utils.NinjaProperties;
import ninja.utils.TimeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricFilter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Integration of Ninja Metrics with InfluxDB.
 *
 * @author James Moger
 */
@Singleton
public class NinjaInfluxDB {

    private final Logger log = LoggerFactory.getLogger(NinjaInfluxDB.class);

    private final NinjaProperties ninjaProperties;

    private final MetricsService metricsService;

    private InfluxdbReporter reporter;

    @Inject
    public NinjaInfluxDB(NinjaProperties ninjaProperties,
                         MetricsService metricsService) {
        this.ninjaProperties = ninjaProperties;
        this.metricsService = metricsService;
    }

    @Start(order = 90)
    public void start() {

        if (ninjaProperties.getBooleanWithDefault("metrics.influxdb.enabled",
                false)) {

            final String hostname = metricsService.getHostname();
            final String address = ninjaProperties
                    .getOrDie("metrics.influxdb.address");
            final int port = ninjaProperties.getIntegerWithDefault(
                    "metrics.influxdb.port", 8086);
            final String database = ninjaProperties
                    .getOrDie("metrics.influxdb.database");
            final String username = ninjaProperties
                    .getOrDie("metrics.influxdb.username");
            final String password = ninjaProperties
                    .getOrDie("metrics.influxdb.password");
            final String period = ninjaProperties.getWithDefault(
                    "metrics.influxdb.period", "60s");
            final int delay = TimeUtil.parseDuration(period);

            try {

                Influxdb influxdb = new Influxdb(address, port, database,
                        username, password);
                final InfluxdbReporter reporter = InfluxdbReporter
                        .forRegistry(metricsService.getMetricRegistry())
                        .prefixedWith(hostname)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .filter(MetricFilter.ALL).build(influxdb);

                reporter.start(delay, TimeUnit.SECONDS);

                log.info(
                        "Started InfluxDB Metrics reporter for '{}', updating every {}",
                        hostname, period);

            } catch (Exception e) {
                log.error("Failed to start InfluxDB reporter!", e);
            }
        }
    }

    @Dispose(order = 10)
    public void stop() {

        if (reporter != null) {

            reporter.stop();

            log.debug("Stopped InfluxDB Metrics reporter");
        }

    }
}
