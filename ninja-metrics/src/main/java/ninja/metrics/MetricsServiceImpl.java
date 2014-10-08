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

package ninja.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ninja.Route;
import ninja.Router;
import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implementation of the metrics service.
 *
 * @author James Moger
 */
@Singleton
public class MetricsServiceImpl implements MetricsService {

    private final Logger log = LoggerFactory.getLogger(MetricsService.class);
    private final Map<String, MetricRegistry> metricRegistries;
    private final NinjaProperties ninjaProps;
    private final List<JmxReporter> jmxReporters;

    @Inject
    public MetricsServiceImpl(MetricRegistry appMetrics,
                              NinjaProperties ninjaProps) {

        this.ninjaProps = ninjaProps;

        this.metricRegistries = new ConcurrentHashMap<>();
        this.jmxReporters = new ArrayList<>();
    }

    @Start(order = 10)
    @Override
    public void start() {
        long startTime = System.currentTimeMillis();

        int reporterCount = 0;
        if (ninjaProps.getBooleanWithDefault("metrics.jmx", true)) {

            String [] jmxReporterNames = { 
                METRICS_REGISTRY_APP
                    , METRICS_REGISTRY_REQUESTS
                    , METRICS_REGISTRY_CACHE};

            for (String jmxReporterName : jmxReporterNames) {
                MetricRegistry metricRegistry = getMetricRegistry(jmxReporterName);
                JmxReporter jmxReporter = JmxReporter.forRegistry(
                        metricRegistry).inDomain(jmxReporterName).build();
                jmxReporters.add(jmxReporter);
                jmxReporter.start();
            }

        }

        reporterCount += jmxReporters.size();

        if (reporterCount > 0) {

            long time = System.currentTimeMillis() - startTime;
            log.debug("Started Ninja Metrics reporters in {} ms", time);

        }

        log.info("Ninja Metrics is ready for collection.");
    }

    @Dispose(order = 10)
    @Override
    public void stop() {
        long start = System.currentTimeMillis();

        if (!jmxReporters.isEmpty()) {

            log.debug("Stopping Ninja Metrics reporters...");

            for (JmxReporter reporter : jmxReporters) {
                reporter.stop();
            }

            long time = System.currentTimeMillis() - start;
            log.debug("Ninja Metrics reporters stopped in {} ms", time);

        }

        log.info("Ninja Metrics stopped.");
    }

    @Override
    public MetricRegistry getMetricRegistry(String name) {
        metricRegistries.putIfAbsent(name, new MetricRegistry());
        return metricRegistries.get(name);
    }

}
