/**
 * Copyright (C) 2012-2019 the original author or authors.
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

/**
 * Responsible for managing the Ninja Metrics module.
 *
 * @author James Moger
 */
public interface MetricsService {

    String METER_ALL_REQUESTS = "ninja.requests.allRequests";
    String COUNTER_ACTIVE_REQUESTS = "ninja.requests.activeRequests";
    String METER_BAD_REQUESTS = "ninja.requests.badRequests";
    String METER_INTERNAL_SERVER_ERRORS = "ninja.requests.internalServerErrors";
    String METER_ROUTES_NOT_FOUND = "ninja.requests.routesNotFound";

    /**
     * Start the Ninja Metrics service.
     */
    void start();

    /**
     * Stops the Ninja Metrics service.
     */
    void stop();

    /**
     * Returns the metric registry.
     *
     * @return the metric registry
     */
    MetricRegistry getMetricRegistry();

    /**
     * Returns the hostname of the server.
     *
     * @return the hostname
     */
    String getHostname();
}
