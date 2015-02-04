/**
 * Copyright (C) 2012-2015 the original author or authors.
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
import ninja.Result;
import ninja.Results;

import com.google.inject.Singleton;

@Singleton
public class PersonController {

    public Result getPersonJson() {

        // simply render a json as result:
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        // render
        return Results.json().render(person);
    }

    public Result postPersonJson(Person person) {
        // okay... we simply render the parsed object again as json
        // usually we would save something into a db or so..
        return Results.json().render(person);
    }
    
    public Result getPersonXml() {

        // simply render a json as result:
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        // render
        return Results.xml().render(person);
    }

    public Result postPersonXml(Person person) {
        // okay... we simply render the parsed object again as json
        // usually we would save something into a db or so..
        return Results.xml().render(person);
    }
    
    public Result getPersonViaContentNegotiation() {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";
        
        return Results.ok().render(person);
    }
    
    public Result getPersonViaContentNegotiationAndFallback() {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";
        
        return Results
                .ok()
                .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON)
                .fallbackContentType(Result.TEXT_HTML)
                .template("views/PersonController/getPersonViaContentNegotiation.ftl.html")
                .render(person);
    }

}
