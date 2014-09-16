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
    private final Router router;
    private final Map<String, MetricRegistry> metricRegistries;
    private final NinjaProperties ninjaProps;
    private final List<JmxReporter> jmxReporters;

    @Inject
    public MetricsServiceImpl(Router router,
                              MetricRegistry appMetrics,
                              NinjaProperties ninjaProps) {

        this.router = router;
        this.ninjaProps = ninjaProps;

        this.metricRegistries = new ConcurrentHashMap<>();
        this.metricRegistries.put(METRICS_REGISTRY_APP, appMetrics);

        this.jmxReporters = new ArrayList<>();
    }

    @Start(order = 10)
    @Override
    public void start() {
        long startTime = System.currentTimeMillis();

        int reporterCount = 0;
        if (ninjaProps.getBooleanWithDefault("metrics.jmx", true)) {

            jmxReporters.add(createJmxReporter(METRICS_REGISTRY_APP));
            jmxReporters.add(createJmxReporter(METRICS_REGISTRY_REQUESTS));
            jmxReporters.add(createJmxReporter(METRICS_REGISTRY_CACHE));

            for (JmxReporter reporter : jmxReporters) {

                reporter.start();

            }

        }

        reporterCount += jmxReporters.size();

        if (reporterCount > 0) {

            long time = System.currentTimeMillis() - startTime;
            log.debug("Started Ninja Metrics reporters in {} ms", time);

        }

//        MetricRegistry routeRequestRegistry = getMetricRegistry(METRICS_REGISTRY_REQUESTS);
//        int timedRoutes = 0;
//        int meteredRoutes = 0;
//        for (Route route : router.getRoutes()) {
//            if (route.getControllerMethod() == null) {
//                // can occur in unit tests, not in real life
//                continue;
//            }
//
//            if (route.getControllerMethod().isAnnotationPresent(Timed.class)) {
//                String name = getName(route);
//                routeRequestRegistry.timer(name);
//                timedRoutes++;
//                log.debug("Registered @Timed route {}", name);
//            } else if (route.getControllerMethod().isAnnotationPresent(
//                    Metered.class)) {
//                String name = getName(route);
//                routeRequestRegistry.meter(name);
//                meteredRoutes++;
//                log.debug("Registered @Metered route {}", name);
//            }
//        }
//
//        if (timedRoutes > 0) {
//            log.info("Ninja Metrics registered {} @Timed routes.", timedRoutes);
//        }
//
//        if (meteredRoutes > 0) {
//            log.info("Ninja Metrics registered {} @Metered routes.",
//                    meteredRoutes);
//        }

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

    protected JmxReporter createJmxReporter(String name) {
        return JmxReporter.forRegistry(getMetricRegistry(name)).inDomain(name)
                .build();
    }

//    protected String getName(Route route) {
//        String name = null;
//        if (route.getControllerMethod().isAnnotationPresent(Timed.class)) {
//            Timed timed = route.getControllerMethod()
//                    .getAnnotation(Timed.class);
//            name = timed.value();
//        } else if (route.getControllerMethod().isAnnotationPresent(
//                Metered.class)) {
//            Metered metered = route.getControllerMethod().getAnnotation(
//                    Metered.class);
//            name = metered.value();
//        }
//
//        if (name == null || name.isEmpty()) {
//            name = MetricRegistry.name(route.getControllerClass(), route
//                    .getControllerMethod().getName());
//        }
//
//        return name;
//    }

//    @Override
//    public MetricRegistry getAllMetrics() {
//        MetricRegistry allMetrics = new MetricRegistry();
//        allMetrics.registerAll(getMetricRegistry(METRICS_REGISTRY_APP));
//        allMetrics.registerAll(getMetricRegistry(METRICS_REGISTRY_REQUESTS));
//        allMetrics.registerAll(getMetricRegistry(METRICS_REGISTRY_CACHE));
//        return allMetrics;
//    }

    @Override
    public MetricRegistry getMetricRegistry(String name) {
        if (!metricRegistries.containsKey(name)) {
            metricRegistries.put(name, new MetricRegistry());
        }

        return metricRegistries.get(name);
    }

//    @Override
//    public Metric getRouteMetric(Route route) {
//        if (route.getControllerMethod() == null) {
//            return null;
//        }
//
//        MetricRegistry routeRequestRegistry = getMetricRegistry(METRICS_REGISTRY_REQUESTS);
//        String name = getName(route);
//        return routeRequestRegistry.getMetrics().get(name);
//    }

}
