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

package controllers;

import javax.inject.Singleton;

import ninja.AuthenticityFilter;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;

@Singleton
public class AuthenticityController {
    
    public Result form() {
        return Results.html();
    }
    
    public Result token() {
        return Results.html();
    }
    
    public Result authenticate() {
        return Results.html();
    }
    
    public Result notauthenticate() {
        return Results.html();
    }

    @FilterWith(AuthenticityFilter.class)
    public Result unauthorized() {
        return Results.html();
    }
    
    @FilterWith(AuthenticityFilter.class)
    public Result authorized() {
        return Results.html();
    }
}