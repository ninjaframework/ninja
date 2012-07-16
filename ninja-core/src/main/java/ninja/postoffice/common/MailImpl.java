package ninja.postoffice.common;

import ninja.postoffice.Mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of Mail.
 * 
 * Does nothing special. In particular it does NOT validate any content.
 * 
 * @author rbauer
 * 
 */
public class MailImpl implements Mail {

    private String subject;

    private String from;

    private final Collection<String> replyTos;

    private String charset;

    private final Map<String, String> headers;

    private final Collection<String> tos;

    private final Collection<String> ccs;

    private final Collection<String> bccs;

    private String bodyText;

    private String bodyHtml;

    public MailImpl() {
        // make sure stuff gets initialized
        headers = new HashMap<String, String>();
        tos = new ArrayList<String>();
        ccs = new ArrayList<String>();
        bccs = new ArrayList<String>();
        replyTos = new ArrayList<String>();

    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public void addReplyTo(String... replyTos) {
        for (String replyTo : replyTos) {
            this.replyTos.add(replyTo);
        }

    }

    @Override
    public void setCharset(String charset) {
        this.charset = charset;

    }

    @Override
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    @Override
    public void addCc(String... ccs) {
        for (String ccRecipient : ccs) {
            this.ccs.add(ccRecipient);
        }

    }

    @Override
    public void addBcc(String... bccs) {
        for (String bccRecipient : bccs) {
            this.bccs.add(bccRecipient);
        }

    }

    @Override
    public void addTo(String... tos) {
        for (String to : tos) {
            this.tos.add(to);
        }

    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getFrom() {
        return from;
    }

    @Override
    public Collection<String> getReplyTo() {
        return replyTos;
    }

    @Override
    public String getCharset() {
        return charset;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Collection<String> getCcs() {
        return ccs;
    }

    @Override
    public Collection<String> getBccs() {
        return bccs;
    }

    @Override
    public Collection<String> getTos() {
        return tos;
    }

    @Override
    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;

    }

    @Override
    public String getBodyHtml() {
        return bodyHtml;
    }

    @Override
    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;

    }

    @Override
    public String getBodyText() {
        return this.bodyText;
    }

}
