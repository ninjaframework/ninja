/**
 * Copyright (C) 2012- the original author or authors.
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

package ninja.bodyparser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import ninja.Context;
import ninja.exceptions.BadRequestException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the Json body parser.
 *
 * @author Thibault Meyer
 */
@RunWith(MockitoJUnitRunner.class)
public class BodyParserEngineJsonTest {

    private static final String DATA_FIRSTNAME = "John";
    private static final String DATA_LASTNAME = "Do";
    private static final Integer DATA_BIRTHYEAR = 1664;
    private static final String DATA_LASTSEEN = "2015-03-15 15:45:00";
    private static final String PARSER_DATEFORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final String PARSER_DATETZ = "GMT";

    @Mock
    private Context context;

    @Test
    public void testValidJsonBody() {
        final String jsonDocument = String.format("{\"firstName\":\"%s\", \"lastName\":\"%s\", \"birthYear\":%d, \"lastSeen\":\"%s\"}",
                BodyParserEngineJsonTest.DATA_FIRSTNAME,
                BodyParserEngineJsonTest.DATA_LASTNAME,
                BodyParserEngineJsonTest.DATA_BIRTHYEAR,
                BodyParserEngineJsonTest.DATA_LASTSEEN);
        final InputStream is = new ByteArrayInputStream(jsonDocument.getBytes());
        final ObjectMapper jsonObjMapper = new ObjectMapper();
        final BodyParserEngineJson bodyParserEngineJson = new BodyParserEngineJson(jsonObjMapper);
        SimpleTestForm testForm = null;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            testForm = bodyParserEngineJson.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(BodyParserEngineJsonTest.PARSER_DATEFORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(BodyParserEngineJsonTest.PARSER_DATETZ));
        try {
            cal.setTime(dateFormat.parse(BodyParserEngineJsonTest.DATA_LASTSEEN));
        } catch (ParseException ignore) {
        }
        cal.setTimeZone(TimeZone.getTimeZone(BodyParserEngineJsonTest.PARSER_DATETZ));

        assertTrue(testForm != null);
        assertThat(testForm.firstName, equalTo(BodyParserEngineJsonTest.DATA_FIRSTNAME));
        assertThat(testForm.lastName, equalTo(BodyParserEngineJsonTest.DATA_LASTNAME));
        assertThat(testForm.birthYear, CoreMatchers.equalTo(BodyParserEngineJsonTest.DATA_BIRTHYEAR));
        assertTrue(testForm.lastSeen != null);
        assertTrue(testForm.lastSeen.compareTo(cal) == 0);
    }

    @Test
    public void testEmptyJsonBody() {
        final String jsonDocument = "";
        final InputStream is = new ByteArrayInputStream(jsonDocument.getBytes());
        final ObjectMapper jsonObjMapper = new ObjectMapper();
        final BodyParserEngineJson bodyParserEngineJson = new BodyParserEngineJson(jsonObjMapper);
        boolean badRequestThrown = false;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            bodyParserEngineJson.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
            badRequestThrown = true;
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        assertTrue(badRequestThrown);
    }

    @Test
    public void testInvalidJsonBody() {
        final String jsonDocument = String.format("{\"firstName\":\"%s\", \"lastName\":\"%s\", \"birthYear\":%d, \"lastSeen\":\"%s\"",
                BodyParserEngineJsonTest.DATA_FIRSTNAME,
                BodyParserEngineJsonTest.DATA_LASTNAME,
                BodyParserEngineJsonTest.DATA_BIRTHYEAR,
                BodyParserEngineJsonTest.DATA_LASTSEEN);
        final InputStream is = new ByteArrayInputStream(jsonDocument.getBytes());
        final ObjectMapper jsonObjMapper = new ObjectMapper();
        final BodyParserEngineJson bodyParserEngineJson = new BodyParserEngineJson(jsonObjMapper);
        boolean badRequestThrown = false;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            bodyParserEngineJson.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
            badRequestThrown = true;
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        assertTrue(badRequestThrown);
    }

    @Test
    public void testJsonBodyWithFullSpacesAndEndOfLines() {
        final String jsonDocument = String.format("  \n\n\n    {  \n    \"firstName\"  \n  :   \"%s\", \"lastName\"\n : \"%s\", \"birthYear\":%d,\n \"lastSeen\":\"%s\"}   ",
                BodyParserEngineJsonTest.DATA_FIRSTNAME,
                BodyParserEngineJsonTest.DATA_LASTNAME,
                BodyParserEngineJsonTest.DATA_BIRTHYEAR,
                BodyParserEngineJsonTest.DATA_LASTSEEN);
        final InputStream is = new ByteArrayInputStream(jsonDocument.getBytes());
        final ObjectMapper jsonObjMapper = new ObjectMapper();
        final BodyParserEngineJson bodyParserEngineJson = new BodyParserEngineJson(jsonObjMapper);
        SimpleTestForm testForm = null;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            testForm = bodyParserEngineJson.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(BodyParserEngineJsonTest.PARSER_DATEFORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(BodyParserEngineJsonTest.PARSER_DATETZ));
        try {
            cal.setTime(dateFormat.parse(BodyParserEngineJsonTest.DATA_LASTSEEN));
        } catch (ParseException ignore) {
        }
        cal.setTimeZone(TimeZone.getTimeZone(BodyParserEngineJsonTest.PARSER_DATETZ));

        assertTrue(testForm != null);
        assertThat(testForm.firstName, equalTo(BodyParserEngineJsonTest.DATA_FIRSTNAME));
        assertThat(testForm.lastName, equalTo(BodyParserEngineJsonTest.DATA_LASTNAME));
        assertThat(testForm.birthYear, CoreMatchers.equalTo(BodyParserEngineJsonTest.DATA_BIRTHYEAR));
        assertTrue(testForm.lastSeen != null);
        assertTrue(testForm.lastSeen.compareTo(cal) == 0);
    }

    @Test
    public void testJsonBodyWithMissingVariables() {
        final String jsonDocument = String.format("{\"firstName\":\"%s\", \"lastName\":\"%s\"}",
                BodyParserEngineJsonTest.DATA_FIRSTNAME,
                BodyParserEngineJsonTest.DATA_LASTNAME);
        final InputStream is = new ByteArrayInputStream(jsonDocument.getBytes());
        final ObjectMapper jsonObjMapper = new ObjectMapper();
        final BodyParserEngineJson bodyParserEngineJson = new BodyParserEngineJson(jsonObjMapper);
        SimpleTestForm testForm = null;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            testForm = bodyParserEngineJson.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        assertTrue(testForm != null);
        assertThat(testForm.firstName, equalTo(BodyParserEngineJsonTest.DATA_FIRSTNAME));
        assertThat(testForm.lastName, equalTo(BodyParserEngineJsonTest.DATA_LASTNAME));
        assertTrue(testForm.birthYear == null);
        assertTrue(testForm.lastSeen == null);
    }

    /**
     * Simple form used during unit tests.
     *
     * @author Thibault Meyer
     */
    private static final class SimpleTestForm {

        public String firstName;
        public String lastName;
        public Integer birthYear;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BodyParserEngineJsonTest.PARSER_DATEFORMAT, timezone = BodyParserEngineJsonTest.PARSER_DATETZ)
        public java.util.Calendar lastSeen;
    }
}
