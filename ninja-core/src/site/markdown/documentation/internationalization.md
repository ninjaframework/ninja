Internationalization
====================

Note: All files of Ninja are encoded in UTF-8. Yes. Also the .properties files.
If you edit the files make sure your editor actually uses UTF-8 and not the default
ISO encoding!


Defining your supported languages
----------------------------------

First you need a definition of supported languages in your <code>conf/application.conf</code> file.
This is a simple comma separated list (whitespaces are omitted).

<pre class="prettyprint">
application.languages=en,de
</pre>

The languages are one or two part ISO coded languages. Usually they resemble language or language and country.
Examples are "en", "de", "en-US", "en-CA" and so on.


Defining messages for your application
------------------------------------

The message file name follows a convention. 
The convention is messages_LANGUAGE.property or messages_LANGUAGE-COUNTRY.property.

Some examples:

 * language "en" is specified in conf/messages_en.properties
 * language "en-US" is specified in conf/messages_en-US.properties
 
 
conf/messages_en.properties might look like:

<pre class="prettyprint">
# registration.ftl.html
casinoRegistrationTitle=Register
casinoRegistrationEmail=Your email
casinoRegistrationConfirm=Confirm
casinoRegistrationAcceptTermsOfService=Accept terms of service          
casinoRegistrationRegister=Register
casinoRegistrationFlashError=An error occurred.

casinoYourUsername=Your username is: {0}

# registrationPending.ftl.html
registrationPleaseVerifyEmailAddress=Please check your email inbox to verify your account.
registrationPendingError=Error confirming email.
registrationPendingSuccess=Success confirming email.  
</pre>


Internally we use MessageFormat.format(text, values) to format the messages. Therefore 
all the information from http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html does apply.

<div class="alert alert-info">
MessageFormat is really cool, but there is one thing to keep in mind:
The apostrophe ' is a special character used for escaping. If you need ASCII apostrophes
you have to enter them two times like ''.
</div>


Getting a message in your code
----------------------------------

Ninja provides the message through the class Messages.

You can inject and use Messages in your application like so:

<pre class="prettyprint">
public class ApplicationController {

    Messages msg

    @Inject
    ApplicationController(Messages msg) {
        this.msg = msg
    }

    public Result controllerMethod(Context context) {

        Optional&lt;String&gt; language = Optional.of(&quot;en&quot;);
        Optional&lt;Result&gt; optResult = Optional.absent();

        // messages use messageFormat. If you use placeholders, messages can format them for you.
        Object [] messageParamters = {&quot;kevin&quot;};

       String message1 = &quot;localized message1: &quot; + msg.get(&quot;casinoRegistrationTitle&quot;, language);

       // This will determine the language from context and result:
       String message2 = &quot;localized message2: &quot; + msg.get(&quot;casinoYourUsername&quot;, context, optResult, messageParamters);

       return Results.text(message1 + &quot; &quot; + message2);

    }

}
</pre>

Getting a message inside a template
-----------------------------------

Inside a Freemarker template (ftl.html) you can get internationalized messages by using

<pre class="prettyprint">
&lt;html&gt;
    &lt;head&gt;
        &lt;title&gt;${i18n(&quot;casinoRegistrationTitle&quot;)}&lt;/title&gt;
    &lt;/head&gt;
&lt;html&gt;
</pre>

You can also format messages automatically:

<pre class="prettyprint">
&lt;html&gt;
    &lt;head&gt;
        &lt;title&gt;${i18n(&quot;casinoYourUsername&quot;, username)}&lt;/title&gt;
    &lt;/head&gt;
&lt;html&gt;
</pre>


<div class="alert alert-info">
If a value for a requested key is missing you'll get the key inside the rendered
template as value. For instance <code>${i18n(&quot;my.message.key&quot;)}</code>
will be displayed as "my.message.key" inside the template if your messages.properties
file is missing that key.
</div>


Fallback messages
-----------------

You can define fallback messages in the file message.properties.

Ninja always looks up messages from more specific to less specific.

Example: The user requests a message in "en-US". The lookup then is
 
 * return messages_en-US.properties if file and key is found or
 * return messages_en.properties if file and key is found or
 * return messages.properties if file and key is found or
 * return null
 
If you specify

<pre class="prettyprint">
application.languages=en
</pre>

It makes sense to only have one message file called messages.properties in English. Therefore
English acts as fallback for all languages - country combinations.



Setting a language by force
---------------------------

Ninja tries to do its best to determine the language from the Accept-Language header.
But there are times, when it makes sense to ignore the header and force the
usage of a certain language.

Ninja provides that possibility by a cookie. The cookie is usually called
NINJA_LANG and contains only one value - the language to use for this user.

You can set the language by using the Lang tools like so:

<pre class="prettyprint">
@Inject
Lang lang;

public Result index() {

    Result result = Results.html().ok();
    lang.setLanguage("de", result);

    return result;

}
</pre>

After setting the language all messages will be displayed in German.



Flash scope and i18n translation
--------------------------------

The flash scope is available in the template via e.g. ${flash.error}. There is a simple rule regarding i18n:
If the value of the flash scope key (eg "error") can be found in the messages the translated version is used.
Otherwise the value is used without any translation.

Consider the messages file introduced some sections above. If you'd use casinoRegistrationFlashError 
as error in your flash cookie it would be automatically translated into "An error occurred".
Using "An error occurred - please check your input" as value won't trigger any translation as the value cannot
be found.

One note: This automatic translation facility cannot be used when placeholders aka {0} are used. In that
case you have to translate the message in your controller and set the translated value yourself (See the demo application
for more hints).

Translating your messages with placeholders inside your controller would look like:

<pre class="prettyprint">
public Result flashError(Context context) {

    Result result = Results.html();

    Optional&lt;String&gt; flashMessage = messages.get(&quot;flashError&quot;, context, Optional.of(result), &quot;PLACEHOLDER&quot;);

    if (flashMessage.isPresent()) {
        context.getFlashScope().error(flashMessage.get());
    }

    return result;

}
</pre>



