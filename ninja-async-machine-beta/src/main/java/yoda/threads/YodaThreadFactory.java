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
