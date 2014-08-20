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

package testapplication.controllers;

import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.exceptions.BadRequestException;
import yoda.Async;


public class AsyncController {
    
    @FilterWith(Async.class)
    public Result async() {
        
        // a regular async operation
        return Results.text().renderRaw("Async works!");
    
    }
    
    @FilterWith(Async.class)
    public Result throwException() {
        
        // just make sure that Async handles exceptions well.
        throw new BadRequestException();
    
    }
    
    
}
