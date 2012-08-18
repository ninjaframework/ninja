Internationalization
====================

Note: All files of Ninja are encoded in UTF-8. Yes. Also the .properties files.


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

The message file name follow a convention. The convention is messages.LANGUAGE.property or messages.LANGUAGE-COUNTRY.property.

Some examples:

 * language "en" is specified in conf/messages.en.properties
 * language "en-US" is specified in conf/messages.en-US.properties
 
 
conf/messages.en.properties might look like:

<pre class="prettyprint">
    # registration.ftl.html
    i18nCasinoRegistrationTitle=Register
    i18nCasinoRegistrationEmail=Your email
    i18nCasinoRegistrationConfirm=Confirm
    i18nCasinoRegistrationAcceptTermsOfService=Accept terms of service          
    i18nCasinoRegistrationRegister=Register

    # registrationPending.ftl.html
    i18nRegistrationPleaseVerifyEmailAddress=Please check your email inbox to verify your account.
    i18nRegistrationPendingError=Error confirming email.
    i18nRegistrationPendingSuccess=Success confirming email.  
</pre>

Two important things:

 * Use camelCaseWriting for your messages
 * Start your message ALWAYS with i18n


Getting a message inside your code
----------------------------------

Ninja provides the message through the class Lang.

You can inject and use Lang your application like so:

<pre class="prettyprint">
    public class ApplicationController {
    
        Lang lang

        @Inject
        ApplicationController(Lang lang) {
            this.lang = lang
        }
    
        public Result controllerMethod() {
        
           String message = "localized message: " + lang.get("i18nCasinoRegistrationTitle", "en");
           return Results.text(message);

        }

    }
</pre>

Getting a message in a template
-------------------------------

Inside a freemarker template (ftl.html) you can get internationalized messages by using


    <html>
        <head>
            <title>${i18nCasinoRegistrationTitle}</title>
        </head>
    <html>




Fallback messages
-----------------

You can define a fallback message as message.properties.

Ninja always looks up messages from more specific to less specific.

Example: The user requests "en-US". The lookup then is
 
 * return messages.en-US.propertes if file and key is found or
 * return messages.en.propertes if file and key is found or
 * return messages.en.propertes if file and key is found or
 * return null
 
If you specify

<pre class="prettyprint">
    application.languages=en
</pre>

It makes sense to only have one message file called messages.properties in english. Therefore
english acts as fallback for all language - country combinations.

