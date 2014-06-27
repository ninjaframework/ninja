package yoda.threads;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ninja.Context;
import ninja.FilterChain;
import ninja.Ninja;
import ninja.utils.NinjaProperties;
import org.apache.commons.lang.StringUtils;
import yoda.YodaAsyncTask;

/**
 * Create a thread pool executor to handle Async requests.
 * 
 * @author dhudson - created 16 Jun 2014
 * @since 1.0
 */
@Singleton
public class ResponderExecutionHandler {

    public static final String CORE_POOL_SIZE_KEY = "responder.core.size";
    public static final String QUEUE_SIZE_KEY = "responder.queue.size";
    public static final String THREAD_PRIORITY_KEY = "responder.thread.priority";
    public static final String REQUEST_TIMEOUT_KEY = "responder.request.timeout";

    //public final Global theGlobal;

    private final int theThreadPriorty;
    private final int theCoreSize;
    private final int theRequestTimeout;
    private final int theQueueSize;
    private final LinkedBlockingQueue<Runnable> theQueue;
    private final ThreadPoolExecutor thePoolExecutor;

    private final long theTimeoutMS;
    
    private final Ninja ninja;
    
    private final YodaResults yodaResults;
    
    
    private final ResponderRejectedExecutionHandler responderRejectedExecutionHandler;

    /**
     * Constructor.
     * 
     * @param properties
     * @param global
     */
    @Inject
    public ResponderExecutionHandler(
            NinjaProperties properties, 
            Ninja ninja,
            ResponderRejectedExecutionHandler responderRejectedExecutionHandler,
            YodaResults yodaResults) {

        this.ninja = ninja;
        this.responderRejectedExecutionHandler = responderRejectedExecutionHandler;
        this.yodaResults = yodaResults;
        
        theThreadPriorty = parseThreadPriority(properties.get(THREAD_PRIORITY_KEY));
        theCoreSize = parseCoreSize(properties.get(THREAD_PRIORITY_KEY));
        theRequestTimeout = parseRequestTimeout(properties.get(REQUEST_TIMEOUT_KEY));
        theQueueSize = parseQueueSize(properties.get(QUEUE_SIZE_KEY));

        if (theQueueSize==0) {
            // Unbounded queue
            theQueue = new LinkedBlockingQueue<Runnable>();
        }
        else {
            // Bounded
            theQueue = new LinkedBlockingQueue<Runnable>(theQueueSize);
        }

        // Calculate MS timeout once
        theTimeoutMS = theRequestTimeout*1000;

        thePoolExecutor =
            new ThreadPoolExecutor(
                    theCoreSize
                    ,theCoreSize
                    ,0
                    ,TimeUnit.SECONDS
                    ,theQueue
                    ,new YodaThreadFactory("Responder Pool", theThreadPriorty),
                        responderRejectedExecutionHandler
                );
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
            "Async Execution Handler.  Core Size [%d] Queue Size [%d], Thread Priority [%d], Request Timeout [%d]",
            theCoreSize,theQueueSize,theThreadPriorty,theRequestTimeout);
    }

    /**
     * Terminate executor.
     * 
     * @since 1.0
     */
    public void shutdown() {
        thePoolExecutor.shutdownNow();
    }

    /**
     * Execute the route with the context in the future.
     * 
     * Place the YodaAsyncTask on the queue.
     * 
     * @param context
     * @param route
     * @since 1.0
     */
    public void execute(Context context, FilterChain filterChain) {
        // Its Async now..
        context.handleAsync();
        thePoolExecutor.execute(
                new YodaAsyncTask(
                        ninja, 
                        context, 
                        yodaResults,
                        filterChain, theTimeoutMS));
    }

    /**
     * Return the queue size.
     * 
     * If the property is missing, then a queue size of 0, unbounded will be used.
     * 
     * @param value
     * @return the queue size
     * @throws IllegalArgumentException if the value is invalid
     * @since 1.0
     */
    private int parseQueueSize(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }

        return parsePositiveInt(value,QUEUE_SIZE_KEY);
    }

    /**
     * Return the request timeout.
     * 
     * If the property is not present, then set timeout to 0.
     * 
     * @param value
     * @return the request timeout
     * @throws IllegalArgumentException if the value is invalid
     * @since 1.0
     */
    private int parseRequestTimeout(String value) {
        // No timeout in forced
        if (StringUtils.isBlank(value)) {
            return 0;
        }

        return parsePositiveInt(value,REQUEST_TIMEOUT_KEY);
    }

    /**
     * Return the thread pool size.
     * 
     * If the property is not present or the value is zero then uses the number of available processors.
     * 
     * @param value
     * @return the thread pool size
     * @throws IllegalArgumentException if the value is invalid
     * @since 1.0
     */
    private int parseCoreSize(String value) {

        // If the core pool size is not present or 0, then use the number of available processors
        if (StringUtils.isBlank(value)||value.trim().equals("0")) {
            return Runtime.getRuntime().availableProcessors();
        }

        return parsePositiveInt(value,CORE_POOL_SIZE_KEY);
    }

    /**
     * Return the thread priority.
     * 
     * If the property is not there then normal priority will be used.
     * 
     * @param value
     * @return the priority
     * @throws IllegalArgumentException if the value is invalid.
     * @since 1.0
     */
    private int parseThreadPriority(String value) {
        // It has not been set, so set it at 5
        if (StringUtils.isBlank(value)) {
            return Thread.NORM_PRIORITY;
        }

        try {
            int priority = Integer.parseInt(value);
            if (priority>Thread.MAX_PRIORITY||priority<Thread.MIN_PRIORITY) {
                throw new IllegalArgumentException(THREAD_PRIORITY_KEY);
            }

            return priority;
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException(THREAD_PRIORITY_KEY);
        }
    }

    /**
     * Parse the int from the properties, throwing a runtime exception if not valid or negative.
     * 
     * @param value
     * @param key
     * @return the parsed number
     * @throws IllegalArgumentExcection if number not valid.
     * @since 1.0
     */
    private int parsePositiveInt(String value,String key) {
        try {
            int num = Integer.parseInt(value);

            if (num<0) {
                throw new IllegalArgumentException(key);
            }

            return num;
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException(key);
        }
    }
}
