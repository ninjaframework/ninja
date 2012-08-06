package controllers;

import models.Person;
import ninja.NinjaTest;
import ninja.utils.NinjaTestBrowser;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersonControllerTest extends NinjaTest {
    @Test
    public void testPostPerson() throws Exception {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        String response = ninjaTestBrowser
                .postJson(getServerAddress() + "person", person);

        Person result = new ObjectMapper().readValue(response, Person.class);
        assertEquals("zeeess name - and some utf8 => öäü", result.name);
    }
}
