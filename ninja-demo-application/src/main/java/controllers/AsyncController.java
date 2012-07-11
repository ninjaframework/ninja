package controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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
    	
    	return Results.TODO();
//        ctx.handleAsync();
//        executorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//            	ctx.requestComplete();
//            	return Results.json(ctx.getHttpServletRequest().getParameter("message"));
//            	
//                ctx.status(Context.HTTP_STATUS.ok200);
//                ctx.renderJson(ctx.getHttpServletRequest().getParameter("message"));
//                
//            }
//        }, Long.parseLong(ctx.getHttpServletRequest().getParameter("timeout")), TimeUnit.MILLISECONDS);
    }

    @Dispose
    public void shutdownExecutor() {
        executorService.shutdown();
    }
}
