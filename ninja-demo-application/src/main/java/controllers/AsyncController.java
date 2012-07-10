package controllers;

import ninja.Context;
import ninja.lifecycle.Dispose;

import javax.inject.Singleton;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronous controller
 */
@Singleton
public class AsyncController {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void asyncEcho(final Context ctx) {
        ctx.handleAsync();
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                ctx.status(Context.HTTP_STATUS.ok200);
                ctx.renderJson(ctx.getHttpServletRequest().getParameter("message"));
                ctx.requestComplete();
            }
        }, Long.parseLong(ctx.getHttpServletRequest().getParameter("timeout")), TimeUnit.MILLISECONDS);
    }

    @Dispose
    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
