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

package ninja.metrics;

import ninja.Context;
import ninja.NinjaDefault;
import ninja.Result;
import ninja.Route;
import ninja.exceptions.BadRequestException;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;

/**
 * An implementation of DefaultNinja instrumented for collecting request
 * metrics.
 *
 * @author James Moger
 *
 */
public class InstrumentedNinja extends NinjaDefault {

    @Inject
    protected MetricsService metricsService;

    protected Meter allRequestsMeter;

    protected Counter activeRequests;

    protected Meter badRequests;

    protected Meter internalServerErrors;

    protected Meter routesNotFound;

    @Override
    public void onFrameworkStart() {

        MetricRegistry metrics = metricsService.getMetricRegistry();

        allRequestsMeter = metrics.meter(MetricsService.METER_ALL_REQUESTS);
        activeRequests = metrics.counter(MetricsService.COUNTER_ACTIVE_REQUESTS);
        badRequests = metrics.meter(MetricsService.METER_BAD_REQUESTS);
        internalServerErrors = metrics.meter(MetricsService.METER_INTERNAL_SERVER_ERRORS);
        routesNotFound = metrics.meter(MetricsService.METER_ROUTES_NOT_FOUND);

        super.onFrameworkStart();
    }

    @Override
    @Timed
    public void onRouteRequest(Context.Impl context) {

        activeRequests.inc();

        String httpMethod = context.getMethod();

        Route route = router.getRouteFor(httpMethod, context.getRequestPath());

        context.setRoute(route);

        if (route != null) {

            allRequestsMeter.mark();

            try {

                Result result = route.getFilterChain().next(context);

                resultHandler.handleResult(result, context);

            } catch (Exception exception) {

                if (exception instanceof BadRequestException) {

                    badRequests.mark();

                } else {

                    internalServerErrors.mark();

                }

                Result result = onException(context, exception);
                renderErrorResultAndCatchAndLogExceptions(result, context);

            }

        } else {
            // throw a 404 "not found" because we did not find the route
            routesNotFound.mark();

            Result result = getNotFoundResult(context);
            renderErrorResultAndCatchAndLogExceptions(result, context);
        }

        activeRequests.dec();
    }

}
