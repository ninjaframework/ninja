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
        person.name = "blah";

        String response = NinjaApiTestHelper
                .postJson(getServerAddress() + "person", person);

        Person result = new ObjectMapper().readValue(response, Person.class);
        assertEquals("blah", person.name);
    }
}
