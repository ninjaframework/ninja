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
