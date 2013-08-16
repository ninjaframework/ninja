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

import static org.junit.Assert.assertEquals;
import models.Person;
import ninja.NinjaTest;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class PersonControllerTest extends NinjaTest {
    
    @Test
    public void testPostPersonJson() throws Exception {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        String response = ninjaTestBrowser.postJson(getServerAddress()
                + "api/person.json", person);

        System.out.println("j: " + response);

        Person result = new ObjectMapper().readValue(response, Person.class);
        assertEquals(person.name, result.name);
    }
    
    @Test
    public void testPostPersonXml() throws Exception {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        String response = ninjaTestBrowser
                .postXml(getServerAddress() + "api/person.xml", person);
        
        System.out.println("j: " + response);


        Person result = new XmlMapper().readValue(response, Person.class);
        assertEquals(person.name, result.name);
    }
    
}
