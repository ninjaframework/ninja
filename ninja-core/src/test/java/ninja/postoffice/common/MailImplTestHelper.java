package ninja.postoffice.common;

import ninja.postoffice.Mail;

public class MailImplTestHelper {

    public static Mail getMailImplWithDemoContent() {

        Mail mail = new MailImpl();

        mail.setSubject("subject");

        mail.setFrom("from1@domain");

        mail.addReplyTo("replyTo1@domain");
        mail.addReplyTo("replyTo2@domain");

        mail.setCharset("utf-8");
        mail.addHeader("header1", "value1");
        mail.addHeader("header2", "value2");

        mail.addTo("to1@domain");
        mail.addTo("to2@domain");

        mail.addCc("cc1@domain");
        mail.addCc("cc2@domain");

        mail.addBcc("bcc1@domain");
        mail.addBcc("bcc2@domain");

        mail.setBodyHtml("bodyHtml");

        mail.setBodyText("bodyText");

        return mail;

    }

}
