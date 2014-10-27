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

package yoda.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ninja.Context;
import ninja.FilterChain;
import ninja.Ninja;
import ninja.utils.NinjaProperties;

import org.apache.commons.lang.StringUtils;

import yoda.YodaAsyncTask;

import com.google.inject.Inject;
import com.google.inject.Singleton;

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

    private final int threadPriorty;
    private final int coreSize;
    private final int requestTimeout;
    private final int queueSize;
    private final LinkedBlockingQueue<Runnable> queue;
    private final ThreadPoolExecutor poolExecutor;

    private final long timeoutMs;
    
    private final Ninja ninja;
    
    private final YodaResults yodaResults;

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
        this.yodaResults = yodaResults;
        
        this.threadPriorty = parseThreadPriority(properties.get(THREAD_PRIORITY_KEY));
        this.coreSize = parseCoreSize(properties.get(THREAD_PRIORITY_KEY));
        this.requestTimeout = parseRequestTimeout(properties.get(REQUEST_TIMEOUT_KEY));
        this.queueSize = parseQueueSize(properties.get(QUEUE_SIZE_KEY));

        if (queueSize == 0) {
            // Unbounded queue
            queue = new LinkedBlockingQueue<>();
        }
        else {
            // Bounded
            queue = new LinkedBlockingQueue<>(queueSize);
        }

        // Calculate MS timeout once
        timeoutMs = requestTimeout * 1000;

        poolExecutor =
            new ThreadPoolExecutor(
                    coreSize
                    ,coreSize
                    ,0
                    ,TimeUnit.SECONDS
                    ,queue
                    ,new YodaThreadFactory("Responder Pool", threadPriorty),
                        responderRejectedExecutionHandler
                );
    }


    @Override
    public String toString() {
        return String.format(
            "Async Execution Handler.  Core Size [%d] Queue Size [%d], Thread Priority [%d], Request Timeout [%d]",
            coreSize, queueSize, threadPriorty, requestTimeout);
    }

    /**
     * Terminate executor.
     * 
     * @since 1.0
     */
    public void shutdown() {
        poolExecutor.shutdownNow();
    }

    /**
     * Execute the route with the context in the future.
     * 
     * Place the YodaAsyncTask on the queue.
     * 
     * @param context
     * @param route
     */
    public void execute(Context context, FilterChain filterChain) {
        // Its Async now..
        context.handleAsync();
        poolExecutor.execute(
                new YodaAsyncTask(
                        ninja, 
                        context, 
                        yodaResults,
                        filterChain, timeoutMs));
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

        return parsePositiveInt(value, CORE_POOL_SIZE_KEY);
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
            if (priority > Thread.MAX_PRIORITY || priority < Thread.MIN_PRIORITY) {
                throw new IllegalArgumentException(THREAD_PRIORITY_KEY);
            }

            return priority;
        }
        catch (NumberFormatException numberFormatException) {
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

            if (num < 0) {
                throw new IllegalArgumentException(key);
            }

            return num;
        }
        catch (NumberFormatException numberFormatException) {
            throw new IllegalArgumentException(key);
        }
    }
}
