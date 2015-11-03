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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Map;

import models.Person;
import ninja.NinjaTest;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.collect.Maps;

public class PersonControllerTest extends NinjaTest {
    
    @Test
    public void testPostPersonJson() throws Exception {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        String response = ninjaTestBrowser.postJson(getServerAddress()
                + "api/person.json", person);

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
    
    @Test
    public void testThatContentNegotiationWithoutFallbackWorks() throws Exception {

        Map<String, String> headers = Maps.newHashMap();
        
        
        headers.put("Accept", "text/html");
        String response = ninjaTestBrowser
                .makeRequest(getServerAddress() + "api/person", headers);
        assertThat(response, equalTo("Person is: zeeess name - and some utf8 =&gt; öäü"));
        
        headers.put("Accept", "application/json");
        response = ninjaTestBrowser
                .makeRequest(getServerAddress() + "api/person", headers);
        assertThat(response, equalTo("{\"name\":\"zeeess name - and some utf8 => öäü\"}"));
        
        headers.put("Accept", "application/xml");
        response = ninjaTestBrowser
            .makeRequest(getServerAddress() + "api/person", headers);
        assertThat(response, equalTo("<Person><name>zeeess name - and some utf8 => öäü</name></Person>"));
        
        //not supported => expecting error result:
        headers.put("Accept", "text/plain");
        response = ninjaTestBrowser
                .makeRequest(getServerAddress() + "api/person", headers);
        assertThat(response.contains("Oops. That&#39;s a bad request and all we know."), equalTo(true));
        
    }
    
    @Test
    public void testThatContentNegotiationWithFallbackWorks() throws Exception {

        Map<String, String> headers = Maps.newHashMap();
        
        
        headers.put("Accept", "text/html");
        String response = ninjaTestBrowser
                .makeRequest(getServerAddress() + "api/person_with_content_negotiation_fallback", headers);
        assertThat(response, equalTo("Person is: zeeess name - and some utf8 =&gt; öäü"));
        
        headers.put("Accept", "application/xml");
        response = ninjaTestBrowser
                .makeRequest(getServerAddress() + "api/person_with_content_negotiation_fallback", headers);
        assertThat(response, equalTo("Person is: zeeess name - and some utf8 =&gt; öäü"));
        
        headers.put("Accept", "application/unknown_content_type");
        response = ninjaTestBrowser
                .makeRequest(getServerAddress() + "api/person_with_content_negotiation_fallback", headers);
        assertThat(response, equalTo("Person is: zeeess name - and some utf8 =&gt; öäü"));
          
    }
}
