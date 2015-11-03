Flash scope
===========

Introduction
------------

The flash scope is designed to transport success and error messages between
stateless web applications. Flash messages only live for one request and
will be deleted afterwards.

The flash scope is similar to a Session. Like a session the flash scope 
is implemented as client side cookie. But the flash scope is not signed.

Setting success message
-----------------------

<pre class="prettyprint">
public Result flashSuccess(FlashScope flashScope) {

    flashScope.success("messageKeySuccess");

    return Results.html();

}
</pre>

You can then display the flash message in your view like that:
<pre class="prettyprint">
&lt;h2&gt;This page displays a flash cookie.&lt;/h2&gt;
&lt;p&gt;
&lt;#if flash.success??&gt;
        Success cookie: &lt;p class=&quot;success&quot;&gt;${flash.success}&lt;/p&gt;
&lt;/#if&gt;
&lt;/p&gt;
</pre>

<code>if flash.success??</code> will allow you to skip the if block when 
the flash.success message is not set. Therefore it will not be displayed to your
user. Usually you want to use those messages when errors and success messages 
from one page (hence controller method) to a another.

Please refer to the "HTML templating" section for more information regarding flash
scope and HTML templates.


Setting error message
---------------------

<pre class="prettyprint">
public Result flashError(FlashScope flashScope) {

    flashScope.error("messageKeyError");

    return Results.html();

}
</pre>


FlashScope and i18n
-------------------

If you are using the default HTML rendering engine Freemarker you should use keys 
for your flash message. Freemarker will automatically 
translate your key into the correct message of the language your user is requesting.

If your message contains placeholders you have to prepare the messages inside
your controller:

<pre class="prettyprint">
public Result flashSuccess(FlashScope flashScope, Context context) {

    Result result = Results.html();

    Optional&lt;String&gt; flashMessage = messages.get("flashSuccess", context, Optional.of(result), "PLACEHOLDER");
    if (flashMessage.isPresent()) {
        flashScope.success(flashMessage.get());
    }

    flashScope.discard();
    return result;

}
</pre>