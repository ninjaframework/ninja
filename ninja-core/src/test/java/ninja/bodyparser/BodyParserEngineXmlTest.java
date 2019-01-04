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

package ninja.bodyparser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
 * Unit tests for the Xml body parser.
 *
 * @author Thibault Meyer
 */
@RunWith(MockitoJUnitRunner.class)
public class BodyParserEngineXmlTest {

    private static final String DATA_FIRSTNAME = "John";
    private static final String DATA_LASTNAME = "Do";
    private static final Integer DATA_BIRTHYEAR = 1664;
    private static final String DATA_LASTSEEN = "2015-03-15 15:45:00";
    private static final String PARSER_DATEFORMAT = "yyyy-MM-dd hh:mm:ss";
    private static final String PARSER_DATETZ = "GMT";

    @Mock
    private Context context;

    @Test
    public void testValidXmlBody() {
        final String xmlDocument = String.format("<form><firstName>%s</firstName><lastName>%s</lastName><birthYear>%d</birthYear><lastSeen>%s</lastSeen></form>",
                BodyParserEngineXmlTest.DATA_FIRSTNAME,
                BodyParserEngineXmlTest.DATA_LASTNAME,
                BodyParserEngineXmlTest.DATA_BIRTHYEAR,
                BodyParserEngineXmlTest.DATA_LASTSEEN);
        final InputStream is = new ByteArrayInputStream(xmlDocument.getBytes());
        final XmlMapper xmlObjMapper = new XmlMapper();
        final BodyParserEngineXml bodyParserEngineXml = new BodyParserEngineXml(xmlObjMapper);
        SimpleTestForm testForm = null;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            testForm = bodyParserEngineXml.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(BodyParserEngineXmlTest.PARSER_DATEFORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(BodyParserEngineXmlTest.PARSER_DATETZ));
        try {
            cal.setTime(dateFormat.parse(BodyParserEngineXmlTest.DATA_LASTSEEN));
        } catch (ParseException ignore) {
        }
        cal.setTimeZone(TimeZone.getTimeZone(BodyParserEngineXmlTest.PARSER_DATETZ));

        assertTrue(testForm != null);
        assertThat(testForm.firstName, equalTo(BodyParserEngineXmlTest.DATA_FIRSTNAME));
        assertThat(testForm.lastName, equalTo(BodyParserEngineXmlTest.DATA_LASTNAME));
        assertThat(testForm.birthYear, CoreMatchers.equalTo(BodyParserEngineXmlTest.DATA_BIRTHYEAR));
        assertTrue(testForm.lastSeen != null);
        assertTrue(testForm.lastSeen.compareTo(cal) == 0);
    }

    @Test
    public void testXmlBodyWithMissingVariables() {
        final String xmlDocument = String.format("<form><firstName>%s</firstName><lastName>%s</lastName></form>",
                BodyParserEngineXmlTest.DATA_FIRSTNAME,
                BodyParserEngineXmlTest.DATA_LASTNAME);
        final InputStream is = new ByteArrayInputStream(xmlDocument.getBytes());
        final XmlMapper xmlObjMapper = new XmlMapper();
        final BodyParserEngineXml bodyParserEngineXml = new BodyParserEngineXml(xmlObjMapper);
        SimpleTestForm testForm = null;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            testForm = bodyParserEngineXml.invoke(context, SimpleTestForm.class);
        } catch (BadRequestException ignore) {
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }

        assertTrue(testForm != null);
        assertThat(testForm.firstName, equalTo(BodyParserEngineXmlTest.DATA_FIRSTNAME));
        assertThat(testForm.lastName, equalTo(BodyParserEngineXmlTest.DATA_LASTNAME));
        assertTrue(testForm.birthYear == null);
        assertTrue(testForm.lastSeen == null);
    }

    @Test
    public void testEmptyXmlBody() {
        final String xmlDocument = "";
        final InputStream is = new ByteArrayInputStream(xmlDocument.getBytes());
        final XmlMapper xmlObjMapper = new XmlMapper();
        final BodyParserEngineXml bodyParserEngineXml = new BodyParserEngineXml(xmlObjMapper);
        boolean badRequestThrown = false;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            bodyParserEngineXml.invoke(context, SimpleTestForm.class);
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
    public void testInvalidXmlBadCloseBody() {
        final String xmlDocument = String.format("<form><firstName>%s</firstName><lastName>%s</lastName><birthYear>%d</birthYear><lastSeen>%s</lastSeen></>",
                BodyParserEngineXmlTest.DATA_FIRSTNAME,
                BodyParserEngineXmlTest.DATA_LASTNAME,
                BodyParserEngineXmlTest.DATA_BIRTHYEAR,
                BodyParserEngineXmlTest.DATA_LASTSEEN);
        final InputStream is = new ByteArrayInputStream(xmlDocument.getBytes());
        final XmlMapper xmlObjMapper = new XmlMapper();
        final BodyParserEngineXml bodyParserEngineXml = new BodyParserEngineXml(xmlObjMapper);
        boolean badRequestThrown = false;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            bodyParserEngineXml.invoke(context, SimpleTestForm.class);
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
    public void testInvalidXmlMissingRootBody() {
        final String xmlDocument = String.format("<firstName>%s</firstName><lastName>%s</lastName><birthYear>%d</birthYear><lastSeen>%s</lastSeen>",
                BodyParserEngineXmlTest.DATA_FIRSTNAME,
                BodyParserEngineXmlTest.DATA_LASTNAME,
                BodyParserEngineXmlTest.DATA_BIRTHYEAR,
                BodyParserEngineXmlTest.DATA_LASTSEEN);
        final InputStream is = new ByteArrayInputStream(xmlDocument.getBytes());
        final XmlMapper xmlObjMapper = new XmlMapper();
        final BodyParserEngineXml bodyParserEngineXml = new BodyParserEngineXml(xmlObjMapper);
        boolean badRequestThrown = false;

        try {
            Mockito.when(context.getInputStream()).thenReturn(is);
        } catch (IOException ignore) {
        }
        try {
            bodyParserEngineXml.invoke(context, SimpleTestForm.class);
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

    /**
     * Simple form used during unit tests.
     *
     * @author Thibault Meyer
     */
    private static final class SimpleTestForm {

        public String firstName;
        public String lastName;
        public Integer birthYear;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = BodyParserEngineXmlTest.PARSER_DATEFORMAT, timezone = BodyParserEngineXmlTest.PARSER_DATETZ)
        public Calendar lastSeen;
    }
}
