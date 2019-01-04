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

package ninja.postoffice.common;

import static org.junit.Assert.assertTrue;
import ninja.postoffice.Mail;

import org.junit.Test;

public class MailImplUnitTest {

    @Test
    public void testThatMailImplWorksAsExpected() {

        // /////////////////////////////////////////////////////////////////////
        // Setup a simple mail with full content:
        // /////////////////////////////////////////////////////////////////////
        Mail mail = MailImplTestHelper.getMailImplWithDemoContent();

        // /////////////////////////////////////////////////////////////////////
        // Test that content has been set correctly
        // /////////////////////////////////////////////////////////////////////
        MailImpl mailImpl = (MailImpl) mail;

        assertTrue(mailImpl.getSubject().equals("subject"));

        assertTrue(mailImpl.getFrom().contains("from1@domain"));

        assertTrue(mailImpl.getReplyTo().contains("replyTo1@domain"));
        assertTrue(mailImpl.getReplyTo().contains("replyTo2@domain"));

        mail.setCharset("utf-8");
        assertTrue(mailImpl.getCharset().contains("utf-8"));

        assertTrue(mailImpl.getHeaders().get("header1").equals("value1"));
        assertTrue(mailImpl.getHeaders().get("header2").equals("value2"));

        assertTrue(mailImpl.getTos().contains("to1@domain"));
        assertTrue(mailImpl.getTos().contains("to2@domain"));

        assertTrue(mailImpl.getCcs().contains("cc1@domain"));
        assertTrue(mailImpl.getCcs().contains("cc2@domain"));

        assertTrue(mailImpl.getBccs().contains("bcc1@domain"));
        assertTrue(mailImpl.getBccs().contains("bcc2@domain"));


        assertTrue(mailImpl.getBodyText().equals("bodyText"));
        assertTrue(mailImpl.getBodyHtml().equals("bodyHtml"));

    }

}
