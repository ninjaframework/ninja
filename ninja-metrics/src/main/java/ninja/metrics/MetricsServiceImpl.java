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

import info.ganglia.gmetric4j.gmetric.GMetric;
import info.ganglia.gmetric4j.gmetric.GMetric.UDPAddressingMode;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.ganglia.GangliaReporter;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteSender;
import com.codahale.metrics.graphite.PickledGraphite;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.codahale.metrics.logback.InstrumentedAppender;
import com.google.common.base.Strings;
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
    private final List<Closeable> reporters;

    @Inject
    public MetricsServiceImpl(MetricRegistry appMetrics,
                              NinjaProperties ninjaProps) {

        this.ninjaProps = ninjaProps;
        this.metricRegistry = appMetrics;
        this.reporters = new ArrayList<>();

    }

    @Start(order = 10)
    @Override
    public void start() {

        String hostname = determineHostname();
        String applicationName = ninjaProps.getWithDefault(
                NinjaConstant.applicationName, "Ninja");

        /*
         * Register optional metrics
         */
        if (ninjaProps.getBooleanWithDefault("metrics.jvm.enabled", false)) {

            registerAll("jvm.gc", new GarbageCollectorMetricSet());
            registerAll("jvm.memory", new MemoryUsageGaugeSet());
            registerAll("jvm.threads", new ThreadStatesGaugeSet());
            registerAll("jvm.classes", new ClassLoadingGaugeSet());

            log.debug("Registered JVM-Metrics integration");

        }

        if (ninjaProps.getBooleanWithDefault("metrics.logback.enabled", false)) {

            final LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
            final ch.qos.logback.classic.Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);

            final InstrumentedAppender appender = new InstrumentedAppender(metricRegistry);
            appender.setContext(root.getLoggerContext());
            appender.start();
            root.addAppender(appender);

            log.debug("Registered Logback-Metrics integration");

        }

        /*
         * JMX
         */
        if (ninjaProps.getBooleanWithDefault("metrics.jmx.enabled", true)) {

            JmxReporter reporter = JmxReporter.forRegistry(metricRegistry)
                    .inDomain(applicationName).build();

            reporter.start();

            reporters.add(reporter);

            log.debug("Started Ninja Metrics JMX reporter");

        }

        /*
         * Graphite
         */
        if (ninjaProps.getBooleanWithDefault("metrics.graphite.enabled", false)) {

            String address = ninjaProps.getWithDefault("metrics.graphite.address", "graphite.example.com");
            int port = ninjaProps.getIntegerWithDefault("metrics.graphite.port", 2003);
            boolean isPickled = ninjaProps.getBooleanWithDefault("metrics.graphite.pickled", false);
            InetSocketAddress graphiteAddress = new InetSocketAddress(address, port);

            final GraphiteSender sender;
            if (isPickled) {
                sender = new PickledGraphite(graphiteAddress);
            } else {
                sender = new Graphite(graphiteAddress);
            }

            final GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                                                              .prefixedWith(hostname)
                                                              .convertRatesTo(TimeUnit.SECONDS)
                                                              .convertDurationsTo(TimeUnit.MILLISECONDS)
                                                              .filter(MetricFilter.ALL)
                                                              .build(sender);
            reporter.start(1, TimeUnit.MINUTES);

            reporters.add(reporter);

            log.debug("Started Ninja Metrics Graphite reporter");

        }

        /*
         * Ganglia
         */
        if (ninjaProps.getBooleanWithDefault("metrics.ganglia.enabled", false)) {

            String address = ninjaProps.getWithDefault("metrics.ganglia.address", "ganglia.example.com");
            int port = ninjaProps.getIntegerWithDefault("metrics.ganglia.port", 8649);

            try {
                GMetric ganglia = new GMetric(address, port, UDPAddressingMode.MULTICAST, 1);
                final GangliaReporter reporter = GangliaReporter.forRegistry(metricRegistry)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .build(ganglia);

                reporters.add(reporter);

                reporter.start(1, TimeUnit.MINUTES);

                log.debug("Started Ninja Metrics Ganglia reporter");

            } catch (IOException e) {
                log.error("Failed to create Ganglia reporter!", e);
            }

        }

        log.info("Ninja Metrics is ready for collection.");

    }

    @Dispose(order = 10)
    @Override
    public void stop() {

        for (Closeable reporter : reporters) {

            log.debug("Stopping {}", reporter.getClass().getName());

            try {

                reporter.close();

            } catch (IOException e) {
                log.error("Failed to stop Metrics reporter", e);
            }

        }

        log.info("Ninja Metrics stopped.");
    }

    @Override
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    private void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
        for (Map.Entry<String, Metric> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(MetricRegistry.name(prefix, entry.getKey()), (MetricSet) entry.getValue());
            } else {
                metricRegistry.register(MetricRegistry.name(prefix, entry.getKey()), entry.getValue());
            }
        }
    }

    private String determineHostname() {
        // try InetAddress.LocalHost first;
        // NOTE -- InetAddress.getLocalHost().getHostName() will not work in
        // certain environments.
        try {
            String result = InetAddress.getLocalHost().getHostName();
            if (!Strings.isNullOrEmpty(result))
                return result;
        } catch (UnknownHostException e) {
            // failed; try alternate means.
        }

        // try environment properties.
        //
        String host = System.getenv("COMPUTERNAME");
        if (host != null)
            return host;
        host = System.getenv("HOSTNAME");
        if (host != null)
            return host;

        // undetermined.
        return "Ninja";
    }
}
