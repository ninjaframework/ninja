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


import com.google.code.tempusfugit.temporal.Condition;
import com.google.code.tempusfugit.temporal.Conditions;
import static com.google.code.tempusfugit.temporal.Duration.millis;
import com.google.code.tempusfugit.temporal.Timeout;
import static com.google.code.tempusfugit.temporal.WaitFor.waitOrTimeout;
import org.junit.Test;

import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DelayedRestartTriggerTest {
    
    @Test
    public void trigger() throws Exception {
        
        RunClassInSeparateJvmMachine machine = mock(RunClassInSeparateJvmMachine.class);
        
        DelayedRestartTrigger restartTrigger = new DelayedRestartTrigger(machine);
        
        // no settling
        restartTrigger.setSettleDownMillis(10);
        
        try {
            restartTrigger.start();
            
            assertEquals(0, restartTrigger.getRestartCount());
            assertEquals(0, restartTrigger.getAccumulatedTriggerCount());
            
            // thread needs to be waiting after start
            waitOrTimeout(Conditions.isWaiting(restartTrigger), Timeout.timeout(millis(10000)));
            
            restartTrigger.trigger();
            
            verify(machine, timeout(10000)).restart();
            
            // thread needs to be waiting after restart
            waitOrTimeout(Conditions.isWaiting(restartTrigger), Timeout.timeout(millis(10000)));
            
            assertEquals(1, restartTrigger.getRestartCount());
            assertEquals(0, restartTrigger.getAccumulatedTriggerCount());
        } finally {
            restartTrigger.shutdown();
        }
        
    }
    
    @Test
    public void settleTimeMillis() throws Exception {
        
        RunClassInSeparateJvmMachine machine = mock(RunClassInSeparateJvmMachine.class);
        
        final DelayedRestartTrigger restartTrigger = new DelayedRestartTrigger(machine);
        
        // long settling down period to ensure it'll happen
        restartTrigger.setSettleDownMillis(10000);
        
        try {
            restartTrigger.start();
            
            assertEquals(0, restartTrigger.getRestartCount());
            assertEquals(0, restartTrigger.getAccumulatedTriggerCount());
            
            // thread needs to be waiting after start
            waitOrTimeout(Conditions.isWaiting(restartTrigger), Timeout.timeout(millis(10000)));
            
            // call restart quickly in a row
            restartTrigger.trigger();
            Thread.sleep(5);
            restartTrigger.trigger();
            Thread.sleep(5);
            restartTrigger.trigger();
            Thread.sleep(5);
            restartTrigger.trigger();
            
            // wait until restart count is 1
            waitOrTimeout(
                new Condition() {
                    @Override
                    public boolean isSatisfied() {
                        return restartTrigger.getRestartCount() > 0;
                    }
                }, Timeout.timeout(millis(10000)));
            
            // wait until accumulated trigger is set back to zero
            waitOrTimeout(
                new Condition() {
                    @Override
                    public boolean isSatisfied() {
                        restartTrigger.interrupt();
                        return restartTrigger.getAccumulatedTriggerCount() <= 0;
                    }
                }, Timeout.timeout(millis(10000)));
            
            // should have only called this exactly once
            verify(machine, timeout(10000).atLeast(1)).restart();
            
            // thread needs to be waiting after restart
            waitOrTimeout(Conditions.isWaiting(restartTrigger), Timeout.timeout(millis(10000)));
            
            assertEquals(1, restartTrigger.getRestartCount());
            assertEquals(0, restartTrigger.getAccumulatedTriggerCount());
        } finally {
            restartTrigger.shutdown();
        }
        
    }
    
}
