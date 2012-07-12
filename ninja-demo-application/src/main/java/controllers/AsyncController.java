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
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public Result asyncEcho(final Context ctx) {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
            	ctx.returnResultAsync(Results.json(ctx.getParameter("message")));

            }
        }, Long.parseLong(ctx.getParameter("timeout")), TimeUnit.MILLISECONDS);
        return Results.async();
    }

    @Dispose
    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
