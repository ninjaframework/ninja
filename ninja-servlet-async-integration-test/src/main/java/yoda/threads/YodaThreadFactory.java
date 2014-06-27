package yoda.threads;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create a thread with the correct name and priority
 *
 * @author dhudson - created 16 Jun 2014
 * @since 1.0
 */
public class YodaThreadFactory implements ThreadFactory {

    /**
     * The current thread count - this indicates the number of threads that have
     * been created and is used only in thread naming. As threads may have been
     * removed this does not represent the current number of threads.
     */
    private final AtomicInteger theThreadNumber = new AtomicInteger();
    
    private final int thePriority;
    private final String theThreadPrefix;
    
    /**
     * Constructor.
     * @param threadPrefix
     * @param priority
     */
    public YodaThreadFactory(String threadPrefix, int priority) {
        thePriority = priority;
        theThreadPrefix = threadPrefix;
    }
    
    /**
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(theThreadPrefix + "-" + theThreadNumber.incrementAndGet());
        thread.setPriority(thePriority);
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
        return thread;
    }

}
