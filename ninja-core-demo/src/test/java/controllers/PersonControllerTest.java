/**
 * Copyright (C) 2012 the original author or authors.
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
import ninja.NinjaTest;
import ninja.utils.NinjaTestBrowser;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.google.gson.Gson;

import static org.junit.Assert.assertEquals;

public class PersonControllerTest extends NinjaTest {
    @Test
    public void testPostPerson() throws Exception {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        String response = ninjaTestBrowser
                .postJson(getServerAddress() + "person", person);

        Person result = new Gson().fromJson(response, Person.class);
        assertEquals(person.name, result.name);
    }
}
