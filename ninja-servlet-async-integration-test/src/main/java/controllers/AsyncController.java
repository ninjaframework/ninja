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

package controllers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.exceptions.BadRequestException;
import ninja.lifecycle.Dispose;
import yoda.Async;


public class AsyncController {
    
    @FilterWith(Async.class)
    public Result async() {
        
        throw new BadRequestException();
        
        //return Results.text().render("async yea!");
    
    }
    
    
    @FilterWith(Async.class)
    public Result worker() {
        
        return Results.text().render("dsfasdfadsf");
        
        //return Results.text().render("async yea!");
    
    }
    
}
