/**
 * Copyright (C) 2012 the original author or authors.
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
