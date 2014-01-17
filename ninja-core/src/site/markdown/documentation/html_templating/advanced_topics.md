Advanced topics - Security, i18n and more
=========================================

Security and templating
-----------------------

Ninja is html escaping all variables that you render by default. If you don't want to do that you
can use the noescape directive around that particular variable.
<pre class="prettyprint">
&lt;#noescape&gt;${yourVariableThatShouldNotBeEscaped}&lt;/#noescape&gt;
</pre>


i18n and the view
----------------

You can access all messages by using a simple <html>

Lets say your message.properties looks like:

<pre class="prettyprint">
casinoRegistrationTitle=My funky title
</pre>   

You can then access the variable inside your view like that.

<pre class="prettyprint">
&lt;html&gt;
    &lt;head&gt;
        &lt;title&gt;${i18n(&quot;casinoRegistrationTitle&quot;)}&lt;/title&gt;
    &lt;/head&gt;
&lt;html&gt;
</pre>

flash content in the view
-------------------------

The flash scope of the application is important. It helps to maintain
a little bit of state between requests even on
a restful architecture.

Rendering the flash error and success messages is straight forward.

Please note that all variables of the flash scope are prefixed with
"flash_".

A simple demo of that

<pre class="prettyprint"> 
&lt;#if flash_error??&gt;
        &lt;p class=&quot;error&quot;&gt;${flash_error}&lt;/p&gt;
&lt;/#if&gt;

&lt;#if flash_success??&gt;
        &lt;p class=&quot;success&quot;&gt;${flash_success}&lt;/p&gt;
&lt;/#if&gt;
</pre>

This simply checks if a flash error or success is there and prints the message out.
The message itself is being translated.



Implicit variables available in templates
-----------------------------------------

 * <code>${session.*}</code> You can access all session-cookie values 
   by their keys prefixed with the accessor "session.". E.g.: 
   If you had set a cookie with the key "username", then you can use 
   <code>${session.username}</code> to resolve the username and display it.
 * <code>${flash.success}</code> Translated (if possible) flash success message 
   (via success("value")).
 * <code>${flash.error}</code> Translated (if possible) flash error message 
   (via error("value")).
 * <code>${flash.*}</code> Translated (if possible) flash message with 
   arbitrary key (via put("key", "value")).
 * <code>${lang}</code> resolves to the language Ninja uses currently. 
 * <code>${contextPath}</code> resolves the context path of the application 
   (empty if running on root)


