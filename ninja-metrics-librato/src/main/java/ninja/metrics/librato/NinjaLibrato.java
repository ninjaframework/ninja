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

package ninja.metrics.librato;

import java.util.concurrent.TimeUnit;

import ninja.lifecycle.Dispose;
import ninja.lifecycle.Start;
import ninja.metrics.MetricsService;
import ninja.utils.NinjaProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.librato.metrics.LibratoReporter;

/**
 * Integration of Ninja Metrics with Librato.
 *
 * @author James Moger
 */
@Singleton
public class NinjaLibrato {

    private final Logger logger = LoggerFactory.getLogger(NinjaLibrato.class);

    private final NinjaProperties ninjaProperties;

    private final MetricsService metricsService;

    @Inject
    public NinjaLibrato(NinjaProperties ninjaProperties,
                        MetricsService metricsService) {
        this.ninjaProperties = ninjaProperties;
        this.metricsService = metricsService;
    }

    @Start(order = 90)
    public void startService() {

        if (ninjaProperties.getBooleanWithDefault("metrics.librato.enabled",
                false)) {

            final String hostname = metricsService.getHostname();
            final String username = ninjaProperties
                    .getOrDie("metrics.librato.username");
            final String apiKey = ninjaProperties
                    .getOrDie("metrics.librato.apikey");

            LibratoReporter.enable(LibratoReporter.builder(
                    metricsService.getMetricRegistry(), username, apiKey,
                    hostname), 10, TimeUnit.SECONDS);

            logger.info("Started Librato Metrics reporter for '{}'", hostname);

        }

    }

    @Dispose(order = 90)
    public void stopService() {
    }
}
