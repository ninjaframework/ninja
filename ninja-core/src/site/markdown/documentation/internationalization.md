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

The message file name follow a convention. 
The convention is messages.LANGUAGE.property or messages.LANGUAGE-COUNTRY.property.

Some examples:

 * language "en" is specified in conf/messages.en.properties
 * language "en-US" is specified in conf/messages.en-US.properties
 
 
conf/messages.en.properties might look like:

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

Two important thing:

 * Use ONLY camelCaseWriting for your messages
 * Internally we use MessageFormat.format(text, values) to format the messages. Therefore
   all information from http://docs.oracle.com/javase/6/docs/api/java/text/MessageFormat.html do apply.


Getting a message inside your code
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
        	
            Optional<String> language = Optional.of("en");
			Optional<Result> optResult = Optional.absent();
			
			// messages use messageFormat. If you use placeholders, messages can format them for you.
			Object [] messageParamters = {"kevin"};
			
           String message1 = "localized message1: " + msg.get("casinoRegistrationTitle", language);
           
           // This will determine the language from context and result:
           String message2 = "localized message2: " + msg.get("casinoYourUsername", context, optResult, messageParamters);
           
           return Results.text(message1 + " " + message2);

        }

    }
</pre>

Getting a message inside a template
-----------------------------------

Inside a freemarker template (ftl.html) you can get internationalized messages by using


    <html>
        <head>
            <title>${i18n("casinoRegistrationTitle")}</title>
        </head>
    <html>
    
You can also format messages automatically:

    <html>
        <head>
            <title>${i18n("casinoYourUsername", username)}</title>
        </head>
    <html>




Fallback messages
-----------------

You can define a fallback message as message.properties.

Ninja always looks up messages from more specific to less specific.

Example: The user requests "en-US". The lookup then is
 
 * return messages.en-US.propertes if file and key is found or
 * return messages.en.propertes if file and key is found or
 * return messages.propertes if file and key is found or
 * return null
 
If you specify

<pre class="prettyprint">
    application.languages=en
</pre>

It makes sense to only have one message file called messages.properties in English. Therefore
English acts as fallback for all language - country combinations.



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

After setting the language all messages will displayed in German.



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
        
        Optional<String> flashMessage = messages.get("flashError", context, Optional.of(result), "PLACEHOLDER");
        
        if (flashMessage.isPresent()) {
            context.getFlashCookie().error(flashMessage.get());
        }

        return result;

    }

</pre>



