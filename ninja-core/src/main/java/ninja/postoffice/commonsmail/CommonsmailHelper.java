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
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.internet.AddressException;
import java.util.Collection;
import java.util.List;

@ImplementedBy(CommonsmailHelperImpl.class)
public interface CommonsmailHelper {

    public void doPopulateMultipartMailWithContent(MultiPartEmail multiPartEmail, Mail mail)
            throws AddressException, EmailException;

    /**
     * Creates a MultiPartEmail. Selects the correct implementation
     * regarding html (MultiPartEmail) and/or txt content or both.
     * 
     * Populates the mutlipart email accordingly with the txt / html content.
     */
    public MultiPartEmail createMultiPartEmailWithContent(Mail mail) throws EmailException;

    public void doSetServerParameter(MultiPartEmail multiPartEmail, String smtpHost,
            Integer smtpPort, Boolean smtpSsl, String smtpUser, String smtpPassword,
            Boolean smtpDebug);

    public List<Tuple<String, String>> createListOfAddresses(Collection<String> emails)
            throws AddressException;

    public Tuple<String, String> createValidEmailFromString(String email) throws AddressException;

}
