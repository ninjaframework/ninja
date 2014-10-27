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

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import ninja.Context;
import ninja.Ninja;
import ninja.Result;
import yoda.YodaAsyncTask;

import com.google.inject.Inject;


/**
 * If the queue is full, then rejectedExecution is called.
 * 
 * @author dhudson - created 17 Jun 2014
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
        Context context = task.getContext();
        Result result = yodaResults.getResponderRejectedExecutionResult(context);
        
        
        ninja.renderErrorResultAndCatchAndLogExceptions(result, context);
        
    }
    


}
