/**
 * Copyright (C) 2012-2017 the original author or authors.
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
