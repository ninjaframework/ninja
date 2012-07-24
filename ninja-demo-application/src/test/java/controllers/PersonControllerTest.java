package controllers;

import models.Person;
import ninja.NinjaApiTest;
import ninja.NinjaApiTestHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PersonControllerTest extends NinjaApiTest {
    @Test
    public void testPostPerson() throws Exception {
        Person person = new Person();
        person.name = "zeeess name - and some utf8 => öäü";

        String response = NinjaApiTestHelper
                .postJson(getServerAddress() + "person", person);

        Person result = new ObjectMapper().readValue(response, Person.class);
        assertEquals("zeeess name - and some utf8 => öäü", result.name);
    }
}
