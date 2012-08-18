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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import ninja.postoffice.Mail;
import ninja.postoffice.common.MailImpl;
import ninja.postoffice.common.MailImplTestHelper;
import ninja.postoffice.common.Tuple;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Before;
import org.junit.Test;

public class CommonsMailHelperImplTest {

    CommonsmailHelper commonsmailHelper;

    @Before
    public void setUp() {

        commonsmailHelper = new CommonsmailHelperImpl();
    }

    @Test
    public void testCreateMultiPartEmailWithContent() throws Exception {

        // /////////////////////////////////////////////////////////////////////
        // Test with text only content
        // /////////////////////////////////////////////////////////////////////
        Mail mail = new MailImpl();
        // set only text:
        mail.setBodyText("simple body text");

        MultiPartEmail multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        assertTrue(multiPartEmail instanceof MultiPartEmail);

        // /////////////////////////////////////////////////////////////////////
        // Test with html only content
        // /////////////////////////////////////////////////////////////////////
        mail = new MailImpl();
        // set only text:
        mail.setBodyHtml("<br>simple body text<br>");

        multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        assertTrue(multiPartEmail instanceof HtmlEmail);

        // /////////////////////////////////////////////////////////////////////
        // Test with html AND text content
        // /////////////////////////////////////////////////////////////////////
        mail = new MailImpl();
        // set only text:
        mail.setBodyText("simple body text");
        mail.setBodyHtml("<br>simple body text<br>");

        multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        assertTrue(multiPartEmail instanceof HtmlEmail);

    }

    /**
     * Note:
     * - Setting of header parameters not (yet) tested as we cannot get back the headers set easily.
     * 
     * 
     * @throws Exception
     */
    @Test
    public void testDoPopulateMultipartMailWithContent() throws Exception {

        Mail mail = MailImplTestHelper.getMailImplWithDemoContent();

        MultiPartEmail multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);
        commonsmailHelper.doPopulateMultipartMailWithContent(multiPartEmail, mail);


        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getBccAddresses())
                .contains(new InternetAddress("bcc1@domain")));

        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getBccAddresses())
                .contains(new InternetAddress("bcc2@domain")));

        assertEquals("subject", multiPartEmail.getSubject());

        assertEquals(new InternetAddress("from1@domain"), multiPartEmail.getFromAddress());

        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getReplyToAddresses())
                .contains(new InternetAddress("replyTo1@domain")));
        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getReplyToAddresses())
                .contains(new InternetAddress("replyTo2@domain")));




        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getCcAddresses())
                .contains(new InternetAddress("cc1@domain")));
        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getCcAddresses())
                .contains(new InternetAddress("cc1@domain")));


        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getBccAddresses())
                .contains(new InternetAddress("bcc1@domain")));
        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getBccAddresses())
                .contains(new InternetAddress("bcc2@domain")));

        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getToAddresses())
                .contains(new InternetAddress("to1@domain")));
        assertTrue(doConvertAdressesToInternetAddressList(multiPartEmail.getToAddresses())
                .contains(new InternetAddress("to2@domain")));

    }


    @Test
    public void testDoSetServerParameter() throws Exception {

        Mail mail = MailImplTestHelper.getMailImplWithDemoContent();

        MultiPartEmail multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        commonsmailHelper.doSetServerParameter(
                multiPartEmail,
                "mail.superserver.com",
                33,
                true,
                "username",
                "password",
                true);

        assertEquals("33", multiPartEmail.getSmtpPort());
        assertEquals("mail.superserver.com", multiPartEmail.getHostName());
        assertEquals(true, multiPartEmail.getMailSession().getDebug());


    }

    @Test
    public void testCreateListOfAddresses() throws Exception {

        List<String> list = new ArrayList<String>();
        list.add("The user <the.user@me.com>");
        list.add("another.user@me.com");

        List<Tuple<String, String>> tupleList = commonsmailHelper.createListOfAddresses(list);

        assertEquals(2, tupleList.size());

        assertEquals("the.user@me.com", tupleList.get(0).x);
        assertEquals("The user", tupleList.get(0).y);

        assertEquals("another.user@me.com", tupleList.get(1).x);
        assertEquals(null, tupleList.get(1).y);

    }

    @Test
    public void testCreateValidEmailFromString() throws Exception {

        // /////////////////////////////////////////////////////////////////////
        // test with full email like "The user <the.user@me.com>"
        // /////////////////////////////////////////////////////////////////////
        String user = "The user <the.user@me.com>";

        Tuple<String, String> tuple = commonsmailHelper.createValidEmailFromString(user);
        assertEquals("the.user@me.com", tuple.x);
        assertEquals("The user", tuple.y);

        // /////////////////////////////////////////////////////////////////////
        // test with full simple email like "the.user@me.com"
        // /////////////////////////////////////////////////////////////////////
        user = "email@me.com";

        tuple = commonsmailHelper.createValidEmailFromString(user);
        assertEquals("email@me.com", tuple.x);
        assertEquals(null, tuple.y);
    }




    /**
     * Convenience method as commonsmail does not use generics...
     * 
     * @param list
     * @return
     */
    private List<InternetAddress> doConvertAdressesToInternetAddressList(List<?> list) {

        List<InternetAddress> returnList = new ArrayList<InternetAddress>();

        for (Object object : list) {
            InternetAddress internetAddress = (InternetAddress) object;
            returnList.add(internetAddress);
        }

        return returnList;

    }

}
