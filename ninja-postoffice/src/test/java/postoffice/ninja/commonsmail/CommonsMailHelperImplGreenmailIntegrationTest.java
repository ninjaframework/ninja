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

package postoffice.ninja.commonsmail;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.ServerSocket;

import javax.mail.internet.InternetAddress;

import postoffice.ninja.Mail;
import postoffice.ninja.Postoffice;
import postoffice.ninja.common.MailImplTestHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

/**
 * Full integration test. Check that emails really go out...
 * 
 * Smtp server is Greenmail.
 * 
 * Stolen from: http://developer.vz.net/2011/11/08/unit-testing-java-mail-code/
 * 
 * @author rbauer
 * 
 */
public class CommonsMailHelperImplGreenmailIntegrationTest {

    GreenMail greenMail;

    int SMTP_TEST_PORT;

    @Before
    public void setUp() throws Exception {

        SMTP_TEST_PORT = findAvailablePort(2000, 10000);

        greenMail = new GreenMail(new ServerSetup(SMTP_TEST_PORT, null, "smtp"));
        greenMail.start();
    }

    @After
    public void tearDown() throws Exception {
        greenMail.stop();
    }

    @Test
    public void testCommonsMailer() throws Exception {

        Mail mail = MailImplTestHelper.getMailImplWithDemoContent();

        // setup the postoffice:
        CommonsmailHelper commonsmailHelper = new CommonsmailHelperImpl();
        Postoffice postoffice =
                new PostofficeCommonsmailImpl(commonsmailHelper, "localhost", SMTP_TEST_PORT, false, false, false,
                        null, null, false);

        postoffice.send(mail);

        assertEquals("from1@domain", ((InternetAddress) greenMail.getReceivedMessages()[0]
                .getFrom()[0]).getAddress());

        assertEquals("subject", greenMail.getReceivedMessages()[0].getSubject());

    }

    private static int findAvailablePort(int min, int max) {
        for (int port = min; port < max; port++) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                // Must already be taken
            }
        }
        throw new IllegalStateException("Could not find available port in range " + min + " to "
                + max);
    }

}
