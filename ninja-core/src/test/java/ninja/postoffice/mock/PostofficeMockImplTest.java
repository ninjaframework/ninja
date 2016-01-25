/**
 * Copyright (C) 2012-2016 the original author or authors.
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

package ninja.postoffice.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import ninja.postoffice.common.MailImpl;

import org.junit.Test;

public class PostofficeMockImplTest {

    @Test
    public void testSending() throws Exception {

        // ////////////////////////////////////////////////////////////////////
        // Setup the mockpostoffice
        // ////////////////////////////////////////////////////////////////////
        Postoffice postoffice = new PostofficeMockImpl();

        // /////////////////////////////////////////////////////////////////////
        // Sending of first mail.
        // /////////////////////////////////////////////////////////////////////
        Mail firstMail = new MailImpl();

        firstMail.setSubject("first mail");
        firstMail.addTo("to@localhost");
        firstMail.setFrom("from@localhost");
        firstMail.setBodyText("simple body text");

        // make sure that mocked mailer did not send email previously
        assertEquals(null, ((PostofficeMockImpl) postoffice).getLastSentMail());

        postoffice.send(firstMail);

        // and test that mail has been sent.
        assertEquals("first mail", ((PostofficeMockImpl) postoffice).getLastSentMail().getSubject());
        assertTrue(((PostofficeMockImpl) postoffice).getLastSentMail().getTos()
                .contains("to@localhost"));
        assertTrue(((PostofficeMockImpl) postoffice).getLastSentMail().getFrom().equals(
                "from@localhost"));
        assertTrue(((PostofficeMockImpl) postoffice).getLastSentMail().getBodyText().equals(
                "simple body text"));

        // /////////////////////////////////////////////////////////////////////
        // Sending of another mail. Check that mock mailer handles repeated
        // sending correctly.
        // /////////////////////////////////////////////////////////////////////
        Mail secondMail = new MailImpl();

        secondMail.setSubject("second mail");
        secondMail.addTo("to@localhost");
        secondMail.setFrom("from@localhost");
        secondMail.setBodyText("simple body text");

        // send simple mail via mocked postoffice
        postoffice.send(secondMail);

        // and test that mail has been sent.
        assertEquals("second mail", ((PostofficeMockImpl) postoffice).getLastSentMail().getSubject());
        assertTrue(((PostofficeMockImpl) postoffice).getLastSentMail().getTos()
                .contains("to@localhost"));
        assertTrue(((PostofficeMockImpl) postoffice).getLastSentMail().getFrom().equals(
                "from@localhost"));
        assertTrue(((PostofficeMockImpl) postoffice).getLastSentMail().getBodyText().equals(
                "simple body text"));

    }

}
