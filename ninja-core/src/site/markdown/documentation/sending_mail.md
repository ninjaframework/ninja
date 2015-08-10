Sending mail
============

Introduction
------------

Ninja allows you to send mails in a really simple and straightforward manner.
All you have to do is to configure your SMTP server in 
<code>application.conf</code>. Then
you can inject <code>Postoffice</code> and <code>Mail</code> in your application. 
This allows you to create a new <code>Mail</code> instance and send 
it via the <code>Postoffice</code>.

Basic usage and configuration
-----------------------------

The first step is to configure your mail server in application.conf. You 
can do so by using the following parameters:

<pre class="prettyprint">
smtp.host=...     // Hostname of the smtp server (e.g. smtp.mycompany.com)
smtp.port=...     // Port of the smtp server  (e.g. 465).
smtp.ssl=...      // Whether to enable ssl (true or false)
smtp.user=...     // Username to access the smtp server
smtp.password=... // The password
smtp.debug=...    // Enable logging of a huge amount of debug information (true or false)
</pre>


If you want to send a mail you can inject the Postoffice and a Provider for
Mail into your class.

<pre class="prettyprint">
public class MailController {

    @Inject
    Provider&lt;Mail&gt; mailProvider;

    @Inject
    Postoffice postoffice;

    public void sendMail() {
    
        Mail mail = mailProvider.get();

        // fill the mail with content:
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

        // finally send the mail
		try {
		    postoffice.send(mail);
		} catch (EmailException | AddressException e) {
		    // ...
		}
    }
}
</pre>


Mail in development and in testing
----------------------------------

When Ninja is running in <code>test</code> or <code>dev</code> mode 
<code>Postoffice</code> will be implemented by <code>PostofficeMockImpl</code> by default.

<code>PostofficeMockImpl</code> does not send mails, but stores mails, so you can check
if they would have been sent in reality via <code>getLastSentMail()</code>.

<code>PostofficeMockImpl</code> also prints out emails that would have been sent
to the system.out. This is nice if you are developing your application but
don't want to check your email account to see if something has been sent.


Advanced usage
--------------

### Specifying own Postoffice implementation

You can override the implementation that will be used via the following parameter
in <code>application.conf</code>:

<pre class="prettyprint">
postoffice.implementation=... // Implementation to use for interface Postoffice
</pre>

If you want to use the real SMTP Postoffice in dev mode (and not the mocked
Postoffice by default) you can override that behavior with the parameter:

<pre class="prettyprint">
%dev.postoffice.implementation=ninja.postoffice.commonsmail.PostofficeCommonsmailImpl
</pre>