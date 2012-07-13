package ninja.postoffice.mock;

import com.google.inject.Inject;
import ninja.postoffice.Mail;
import ninja.postoffice.Postoffice;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;

public class PostofficeMockImpl implements Postoffice {

    Mail lastSentMail;
    private final Logger logger;
    
    @Inject
    public PostofficeMockImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void send(Mail mail) throws EmailException {
        this.lastSentMail = mail;
        prettyPrintLastSentMail(mail);

    }

    public Mail getLastSentMail() {
        return lastSentMail;
    }

    private void prettyPrintLastSentMail(Mail mail) {
        
        logger.info("--------------------------------------------------------");
        logger.info("Mock mailer received send email request");
        logger.info("--------------------------------------------------------");
        logger.info("Subject : " + mail.getSubject());
        logger.info("From    : " + mail.getFrom());

        for (String email : mail.getTos()) {
            logger.info("To      : " + email);
        }

        for (String email : mail.getReplyTo()) {
            logger.info("ReplyTo : " + email);
        }

        for (String email : mail.getCcs()) {
            logger.info("Cc      : " + email);
        }

        for (String email : mail.getBccs()) {
            logger.info("Bcc      : " + email);
        }

        logger.info("----- Html content -------------------------------------");
        logger.info(mail.getBodyHtml());
        logger.info("----- Text content -------------------------------------");
        logger.info(mail.getBodyText());
        logger.info("--------------------------------------------------------");

    }

}
