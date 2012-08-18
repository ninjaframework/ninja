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

package ninja.postoffice;

import java.util.Collection;
import java.util.Map;

import ninja.postoffice.common.MailImpl;

import com.google.inject.ImplementedBy;

/**
 * A simple interface.
 * 
 * It is modeled after org.apache.commons.mail.Email.
 * 
 * But it allows us not to use org.apache.commons.mail.Email at all, use
 * Javamail or anything else.
 * 
 * @author rbauer
 * 
 */
@ImplementedBy(MailImpl.class)
public interface Mail {

    void setSubject(String subject);

    String getSubject();

    /**
     * In general email addresses could look like:
     * Joe Jocker <joe.jocker@me.com>
     * or
     * joe@joe.com.
     * 
     * Make sure your implementation and / or your mailer can handle these.
     * 
     * @param tos
     */
    void addTo(String... tos);

    Collection<String> getTos();

    /**
     * In general email addresses could look like:
     * Joe Jocker <joe.jocker@me.com>
     * or
     * joe@joe.com.
     * 
     * Make sure your implementation and / or your mailer can handle these.
     * 
     * @param tos
     */
    void setFrom(String from);

    String getFrom();

    /**
     * In general email addresses could look like:
     * Joe Jocker <joe.jocker@me.com>
     * or
     * joe@joe.com.
     * 
     * Make sure your implementation and / or your mailer can handle these.
     * 
     * @param replyTos
     */
    void addReplyTo(String... replyTos);

    Collection<String> getReplyTo();

    /**
     * In general email addresses could look like:
     * Joe Jocker <joe.jocker@me.com>
     * or
     * joe@joe.com.
     * 
     * Make sure your implementation and / or your mailer can handle these.
     * 
     * @param ccs
     */
    void addCc(String... ccs);

    Collection<String> getCcs();

    /**
     * In general email addresses could look like:
     * Joe Jocker <joe.jocker@me.com>
     * or
     * joe@joe.com.
     * 
     * Make sure your implementation and / or your mailer can handle these.
     * 
     * @param bccs
     */
    void addBcc(String... bccs);

    Collection<String> getBccs();

    void setCharset(String charset);

    String getCharset();

    void addHeader(String key, String value);

    Map<String, String> getHeaders();

    void setBodyHtml(String html);

    String getBodyHtml();

    void setBodyText(String text);

    String getBodyText();

}
