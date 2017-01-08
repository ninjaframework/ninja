/**
 * Copyright (C) 2012-2017 the original author or authors.
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

package ninja.build;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DelayedRestartTrigger extends Thread {
    static private final Logger log = LoggerFactory.getLogger(DelayedRestartTrigger.class);

    private boolean shutdown;
    private final AtomicInteger restartCount;
    private final AtomicInteger accumulatedTriggerCount;
    private final ReentrantLock restartLock;
    private final Condition restartRequested;
    private long settleDownMillis = 500;
    private final RunClassInSeparateJvmMachine runClassInSeparateJvmMachine;

    public DelayedRestartTrigger(
            RunClassInSeparateJvmMachine runClassInSeparateJvmMachine) {
        
        this.shutdown = false;
        this.setDaemon(true);
        this.setName("DelayedRestartTrigger");
        this.restartCount = new AtomicInteger(0);
        this.accumulatedTriggerCount = new AtomicInteger(0);
        this.restartLock = new ReentrantLock();
        this.restartRequested = this.restartLock.newCondition();
        this.runClassInSeparateJvmMachine = runClassInSeparateJvmMachine;

    }
    
    public void shutdown() {
        this.shutdown = true;
        this.interrupt();
    }

    public int getRestartCount() {
        return restartCount.get();
    }
    
    public int getAccumulatedTriggerCount() {
        return accumulatedTriggerCount.get();
    }
    
    public long getSettleDownMillis() {
        return settleDownMillis;
    }

    public void setSettleDownMillis(long settleDownMillis) {
        this.settleDownMillis = settleDownMillis;
    }
    
    @Override
    public void run() {
        
        while (!shutdown) {
            try {
                // wait for a restart request
                this.restartLock.lock();
                try {
                    if (this.accumulatedTriggerCount.get() <= 0) {
                        this.restartRequested.await();
                    }
                    this.restartCount.incrementAndGet();
                } finally {
                    this.restartLock.unlock();
                }
                
                log.info("------------------------------------------------------------------------");
                log.info("Restart process starting...");
                // wait for restarts requests to settle (e.g. accumlated file changes)
                int totalTriggerCount = 0;
                do {
                    
                    log.info("Delaying restart for " + settleDownMillis + " ms to wait for file changes to settle");
                    totalTriggerCount += this.accumulatedTriggerCount.getAndSet(0);
                    
                    try {
                        Thread.sleep(settleDownMillis);
                    } catch (InterruptedException e) {
                        // ignore it
                    }
                
                    // is it still zero after sleeping?
                } while (this.accumulatedTriggerCount.get() != 0);
                
                log.info("Restarting SuperDevMode (" + totalTriggerCount + " file change(s) detected)");
                log.info("------------------------------------------------------------------------");
                
                // set back to zero immediately before restart (if we do happen
                // to get paused, triggers come in, then we restart, worst case
                // is that the restart will occur immediately again but at least
                // with a slight pause due to the "settle down" period
                this.accumulatedTriggerCount.set(0);
                runClassInSeparateJvmMachine.restart();

            } catch (InterruptedException e) {
                if (!shutdown) {
                    log.error("Unexpected thread interrupt (maybe you are shutting down Maven?)", e);
                }
                break;
            }
        }
    }
    
    /**
     * Usually this method is called by an external component that watches
     * a directory to restart Ninja's dev mode.
     * 
     * The restart will be executed with a delay. If a bunch of files are
     * changed at the same time only one restart is performed.
     * 
     */
    public void trigger() {
        // signal for a restart
        this.restartLock.lock();
        try {
            // accumulate restart triggers (e.g. # of files changed)
            accumulatedTriggerCount.incrementAndGet();
            this.restartRequested.signal();
        } finally {
            this.restartLock.unlock();
        }
    }
    
}
