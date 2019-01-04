/**
 * Copyright (C) 2012-2019 the original author or authors.
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

import ninja.Context;
import ninja.Result;
import ninja.Results;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import etc.GreetingService;

/**
 * This class demonstrates how easy it is to inject any guice services into a
 * controller.
 * 
 * In this case we are injecting a simple GreetingService that can say hello...
 * 
 * @author ra
 * 
 */
@Singleton
public class InjectionExampleController {

    private GreetingService greeter;

    @Inject
    public InjectionExampleController(GreetingService greeter) {
        this.greeter = greeter;

    }

    public Result injection(Context context) {

        return Results.html().render("greeting", greeter.hello());

    }

    public Result serviceInitTime(Context context) {
        return Results.json().render("initTime", greeter.getServiceInitializationTime());
    }

}
