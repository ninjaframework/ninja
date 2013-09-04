#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
 * Copyright (C) 2013 the original author or authors.
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

import models.Person;
import ninja.Context;
import ninja.Result;
import ninja.Results;

import com.google.inject.Singleton;

@Singleton
public class PersonController {

    public Result getPerson() {

        // simply render a json as result:
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        // render
        return Results.json().render(person);
    }

    public Result postPerson(Person person) {
        // okay... we simply render the parsed object again as json
        // usually we would save something into a db or so..
        return Results.json().render(person);
    }

}
