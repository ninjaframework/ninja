package yoda.threads;

import com.google.inject.Inject;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import ninja.Context;
import ninja.Ninja;
import ninja.Result;
import ninja.Results;
import yoda.YodaAsyncTask;


/**
 * If the queue is full, then rejectedExecution is called.
 * 
 * @author dhudson - created 17 Jun 2014
 * @since 1.0
 */
public class ResponderRejectedExecutionHandler implements RejectedExecutionHandler {
    
    public final Ninja ninja;
    public final YodaResults yodaResults;
    
    @Inject
    public ResponderRejectedExecutionHandler(
            Ninja ninja,
            YodaResults yodaResults) {
    
        this.ninja = ninja;
        this.yodaResults = yodaResults;
    
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
         
         
        YodaAsyncTask task = (YodaAsyncTask) runnable;
        Result result = yodaResults.getResponderRejectedExecutionResult();
        Context context = task.getContext();
        
        ninja.renderErrorResultAndCatchAndLogExceptions(result, context);
        
    }
    


}
