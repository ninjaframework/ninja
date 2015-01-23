/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.lifecycle.Dispose;

/**
 * Asynchronous controller
 */
@Singleton
public class AsyncController {
    private final ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();

    public Result asyncEcho(final Context ctx) {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                ctx.returnResultAsync(Results.json().render(ctx.getParameter("message")));

            }
        }, Long.parseLong(ctx.getParameter("timeout")), TimeUnit.MILLISECONDS);
        return Results.async();
    }

    @Dispose
    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
