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

import ninja.Route;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;

/**
 * Responsible for managing the Ninja Metrics module.
 *
 * @author James Moger
 */
public interface MetricsService {

    String METRICS_REGISTRY_APP = "ninja.app";

    String METRICS_REGISTRY_REQUESTS = "ninja.requests";

    String METRICS_REGISTRY_CACHE = "ninja.cache";

    String METER_ALL_REQUESTS = "ninja.allRequests";

    String COUNTER_ACTIVE_REQUESTS = "ninja.activeRequests";

    String METER_BAD_REQUESTS = "ninja.badRequests";

    String METER_INTERNAL_SERVER_ERRORS = "ninja.internalServerErrors";

    String METER_ROUTES_NOT_FOUND = "ninja.routesNotFound";

    /**
     * Start the Ninja Metrics service.
     */
    void start();

    /**
     * Stops the Ninja Metrics service.
     */
    void stop();

    /**
     * Return all collected metrics in a single metrics registry.
     *
     * @return the aggregate metrics registry
     */
    MetricRegistry getAllMetrics();

    /**
     * Returns the specified metric registry.
     *
     * @param name
     * @return the specified metric registry
     */
    MetricRegistry getMetricRegistry(String name);

    /**
     * Returns the metric for this specific route. This will be null if the
     * controller method was not annotated with @Timed or @Metered.
     *
     * @param route
     * @return a metric or null
     */
    Metric getRouteMetric(Route route);

}
