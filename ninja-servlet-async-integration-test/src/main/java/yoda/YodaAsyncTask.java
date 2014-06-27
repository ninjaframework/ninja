package yoda;

import ninja.Context;
import ninja.FilterChain;
import ninja.Ninja;
import ninja.Result;
import ninja.Results;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import yoda.threads.YodaResults;


/**
 * Async Processing of the inbound request.
 * 
 * @author dhudson - created 16 Jun 2014
 * @since 1.0
 */
public class YodaAsyncTask implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(YodaAsyncTask.class);

    private final Context context;
    private final FilterChain filterChain;
    
    private final Ninja ninja;
    private final YodaResults yodaResults;

    /**
     * Used to calculate if its been on the queue for too long
     */
    private long theTimeQueued;
    private final long timeout;


    public YodaAsyncTask(
            Ninja ninja, 
            Context context, 
            YodaResults yodaResults,
            FilterChain filterChain, 
            long timeout) {
        this.ninja = ninja;
        this.context = context;
        this.yodaResults = yodaResults;
        this.filterChain = filterChain;
        this.theTimeQueued = System.currentTimeMillis();
        this.timeout = timeout;
    }

    /**
     * Return the context.
     * 
     * @return the context for this task
     * @since 1.0
     */
    public Context getContext() {
        return context;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        
        // Lets run the thing
        try {
            
            if (timeout != 0) {
                
                if (System.currentTimeMillis() - theTimeQueued > timeout) {
                    
                    Result result = yodaResults.getTimeoutExceptionResult();
                    ninja.renderErrorResultAndCatchAndLogExceptions(result, context);
                
                }
                
            } else {

                context.returnResultAsync(filterChain.next(context));
                
            }
            
        } catch (Exception exception) {
            
            Result result = ninja.onException(context, exception);
            ninja.renderErrorResultAndCatchAndLogExceptions(result, context);
        
        }
    }

}
