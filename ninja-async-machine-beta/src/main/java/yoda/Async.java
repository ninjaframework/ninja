/**
 * Copyright (C) 2012-2016 the original author or authors.
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

package yoda;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yoda.threads.ResponderExecutionHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;


@Singleton
public class Async implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(Async.class);
    
    private final ResponderExecutionHandler responderExecutionHandler;

    @Inject
    public Async(ResponderExecutionHandler responderExecutionHandler) {
        
        // This throws a runtime exception if not correct properties
        this.responderExecutionHandler = responderExecutionHandler;
    
        logger.info(this.responderExecutionHandler.toString());      
        
    }
    
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        // Its all Async...
        responderExecutionHandler.execute(context, filterChain);
        return null;
    }
    
}
