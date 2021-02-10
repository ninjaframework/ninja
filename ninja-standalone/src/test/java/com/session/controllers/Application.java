/**
 * Copyright (C) the original author or authors.
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

package com.session.controllers;

import ninja.Result;
import ninja.Results;
import ninja.session.Session;

public class Application {
    
    public Result getOrCreateSession(Session session) {
        if (session.get("user") == null) {
            session.put("user", System.currentTimeMillis()+"");
        }
        
        return Results
            .ok()
            .html()
            .renderRaw(session.get("user"));
    }
    
    public Result getSession(Session session) {
        return Results
            .ok()
            .html()
            .renderRaw(session.get("user"));
    }
    
}
