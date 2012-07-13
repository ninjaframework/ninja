package ninja.postoffice.mock;

import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostofficeMockImpl implements Postoffice {

    Mail lastSentMail;
    private static final Logger log = LoggerFactory.getLogger(PostofficeMockImpl.class);

    @Override
    public void send(Mail mail) throws EmailException {
        this.lastSentMail = mail;
        prettyPrintLastSentMail(mail);

    }

    public Mail getLastSentMail() {
        return lastSentMail;
    }

    private void prettyPrintLastSentMail(Mail mail) {
        
        log.info("--------------------------------------------------------");
        log.info("Mock mailer received send email request");
        log.info("--------------------------------------------------------");
        log.info("Subject : " + mail.getSubject());
        log.info("From    : " + mail.getFrom());

        for (String email : mail.getTos()) {
            log.info("To      : " + email);
        }

        for (String email : mail.getReplyTo()) {
            log.info("ReplyTo : " + email);
        }

        for (String email : mail.getCcs()) {
            log.info("Cc      : " + email);
        }

        for (String email : mail.getBccs()) {
            log.info("Bcc      : " + email);
        }

        log.info("----- Html content -------------------------------------");
        log.info(mail.getBodyHtml());
        log.info("----- Text content -------------------------------------");
        log.info(mail.getBodyText());
        log.info("--------------------------------------------------------");

    }

}
