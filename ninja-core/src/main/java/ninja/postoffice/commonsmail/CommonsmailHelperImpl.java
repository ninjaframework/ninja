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

package ninja.postoffice.commonsmail;

import com.google.inject.ImplementedBy;
import ninja.postoffice.Mail;
import ninja.postoffice.common.Tuple;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommonsmailHelperImpl implements CommonsmailHelper {

    /**
     * Creates a MultiPartEmail. Selects the correct implementation
     * regarding html (MultiPartEmail) and/or txt content or both.
     * 
     * Populates the mutlipart email accordingly with the txt / html content.
     */
    @Override
    public MultiPartEmail createMultiPartEmailWithContent(Mail mail) throws EmailException {

        MultiPartEmail multiPartEmail;

        // set if it is a txt or html mail:

        if (mail.getBodyHtml() == null || mail.getBodyHtml().equals("")) {

            multiPartEmail = new MultiPartEmail();
            multiPartEmail.setMsg(mail.getBodyText());

        } else if (mail.getBodyText() == null || mail.getBodyText().equals("")) {
            multiPartEmail = new HtmlEmail().setHtmlMsg(mail.getBodyHtml());
        } else {
            multiPartEmail =
                    new HtmlEmail().setHtmlMsg(mail.getBodyHtml()).setTextMsg(mail.getBodyText());
        }

        // and return the nicely configured mail:
        return multiPartEmail;
    }

    @Override
    public void doPopulateMultipartMailWithContent(MultiPartEmail multiPartEmail, Mail mail)
            throws AddressException, EmailException {

        String charset = "utf-8";
        if (mail.getCharset() != null) {
            charset = mail.getCharset();
        }

        multiPartEmail.setCharset(charset);


        String subject = "";
        if (mail.getSubject() != null) {
            subject = mail.getSubject();
        }

        multiPartEmail.setSubject(subject);

        if (mail.getFrom() != null) {

            Tuple<String, String> from = createValidEmailFromString(mail.getFrom());

            if (from.y != null) {
                multiPartEmail.setFrom(from.x, from.y);
            } else {
                multiPartEmail.setFrom(from.x);
            }

        }


        if (mail.getTos() != null) {
            if (!mail.getTos().isEmpty()) {
                List<Tuple<String, String>> emails = createListOfAddresses(mail.getTos());
                for (Tuple<String, String> email : emails) {

                    if (email.y != null) {
                        multiPartEmail.addTo(email.x, email.y);
                    } else {
                        multiPartEmail.addTo(email.x);
                    }

                }
            }
        }

        if (mail.getReplyTo() != null) {
            if (!mail.getReplyTo().isEmpty()) {
                List<Tuple<String, String>> emails = createListOfAddresses(mail.getReplyTo());
                for (Tuple<String, String> email : emails) {
                    multiPartEmail.addReplyTo(email.x, email.y);
                }
            }
        }

        if (mail.getCcs() != null) {
            if (!mail.getCcs().isEmpty()) {
                List<Tuple<String, String>> emails = createListOfAddresses(mail.getCcs());
                for (Tuple<String, String> email : emails) {
                    multiPartEmail.addCc(email.x, email.y);
                }
            }
        }

        if (mail.getBccs() != null) {
            if (!mail.getBccs().isEmpty()) {
                List<Tuple<String, String>> emails = createListOfAddresses(mail.getBccs());
                for (Tuple<String, String> email : emails) {
                    multiPartEmail.addBcc(email.x, email.y);
                }
            }
        }

        if (mail.getHeaders() != null) {
            multiPartEmail.setHeaders(mail.getHeaders());
        }

    }

    @Override
    public void doSetServerParameter(MultiPartEmail multiPartEmail, String smtpHost,
            Integer smtpPort, Boolean smtpSsl, String smtpUser, String smtpPassword,
            Boolean smtpDebug) {

        // /set config params:
        multiPartEmail.setHostName(smtpHost);
        multiPartEmail.setSmtpPort(smtpPort);
        multiPartEmail.setSSL(smtpSsl);

        if (smtpUser != null) {
            multiPartEmail.setAuthentication(smtpUser, smtpPassword);
        }

        multiPartEmail.setDebug(smtpDebug);

    }

    @Override
    public List<Tuple<String, String>> createListOfAddresses(Collection<String> emails)
            throws AddressException {
        List<Tuple<String, String>> tuples = new ArrayList<Tuple<String, String>>();

        for (String email : emails) {

            tuples.add(createValidEmailFromString(email));
        }

        return tuples;

    }

    @Override
    public Tuple<String, String> createValidEmailFromString(String email) throws AddressException {

        InternetAddress internetAddress = new InternetAddress(email);

        Tuple<String, String> tuple =
                new Tuple<String, String>(internetAddress.getAddress(), internetAddress
                        .getPersonal());

        return tuple;

    }

}
