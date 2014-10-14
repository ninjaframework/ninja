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

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Implementation of the Metrics service.
 *
 * @author James Moger
 */
@Singleton
public class MetricsServiceImpl implements MetricsService {

    private final Logger log = LoggerFactory.getLogger(MetricsService.class);
    private final NinjaProperties ninjaProps;
    private final MetricRegistry metricRegistry;
    private JmxReporter jmxReporter;

    @Inject
    public MetricsServiceImpl(MetricRegistry appMetrics,
                              NinjaProperties ninjaProps) {

        this.ninjaProps = ninjaProps;
        this.metricRegistry = new MetricRegistry();

    }

    @Start(order = 10)
    @Override
    public void start() {

        if (ninjaProps.getBooleanWithDefault("metrics.jmx", true)) {

            String applicationName = ninjaProps.getWithDefault(
                    NinjaConstant.applicationName, "Ninja");

            jmxReporter = JmxReporter.forRegistry(metricRegistry)
                    .inDomain(applicationName).build();

            jmxReporter.start();

            log.debug("Started Ninja Metrics JMX reporter");

        }

        log.info("Ninja Metrics is ready for collection.");
    }

    @Dispose(order = 10)
    @Override
    public void stop() {

        if (jmxReporter != null) {

            log.debug("Stopping Ninja Metrics JMX reporter...");

            jmxReporter.stop();

        }

        log.info("Ninja Metrics stopped.");
    }

    @Override
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

}
