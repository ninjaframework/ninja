Error handling
==============

Even the most simplistic applications have to handle errors some way or the other.
Ninja supports you by providing a default mechanism to throw exceptions and
handle them via HTML views.

Ninja's exceptions
------------------

Ninja provides three types of exceptions: 

 * <code>BadRequestException</code> 
 * <code>ForbiddenRequestException</code> 
 * <code>InternalServerErrorException</code>

A <code>BadRequestException</code> should signal a faulty request by the user. It is
similar in mindset as the HTTP error 400.

A <code>ForbiddenRequestException</code> should signal a unauthorized request by the user. It is
similar in mindset as the HTTP error 403.

An <code>InternalServerErrorException</code> signals that something went wrong
inside your application - pretty much like the HTTP error 500.

Both exceptions are unchecked, and you can wrap your own exceptions inside them.
For instance if you encounter an  <code>IOException</code> you can rethrow that exception
by calling  <code>throw new InternalServerErrorException(ioException)</code>.

At the very end of the request handling Ninja will detect the exception and
render an appropriate error page.


Errors and content negotiation
------------------------------

The representation of the error to the user is based on content negotiation. Ninja
by default properly communicates errors in HTML, JSON and XML.


HTML error representation
-------------------------

The default HTML views for errors can be found here:

 * <code>views/system/400badRequest.ftl.html</code>
 * <code>views/system/404notFound.ftl.html</code> (If a route cannot be found).
 * <code>views/system/403forbidden.ftl.html</code> (If a route cannot be found).
 * <code>views/system/500internalServerError.ftl.html</code>
 * <code>views/system/401unauthorized.ftl.html</code> (if an authorization is required)

You can overwrite the views by creating the appropriate files in your application
at the very same locations (<code>views/system/...</code>).

This allows you to use your own styling and messages for the error views. The rendering 
context will contain a `Message` object, containing a `text` description of the error type, 
and an `error` message from the corresponding exception (if any).

You can also change their locations using the following ninja properties:

 * <code>application.views.400badRequest</code>
 * <code>application.views.404notFound</code>
 * <code>application.views.403forbidden</code>
 * <code>application.views.500internalServerError</code>
 * <code>application.views.401unauthorized</code>


JSON and XML error representations
----------------------------------

JSON and XML errors will both be rendered by content negotiation and their
default template rendering engines. Errors thus will be rendered as JSON or XML if
the user sends header Accept: application/json or Accept: application/xml. 

The error itself based on ninja.util.Message which contains one field called "text".

By default the JSON error as Message will look like:
<pre class="prettyprint">
{
    "text": "Oops. The requested route cannot be found.",
    "error": "My exception localized message."
}
</pre>

And XML Message looks like:
<pre class="prettyprint">
&lt;Message&gt;
    &lt;text&gt;Oops. The requested route cannot be found.&lt;/text&gt;
    &lt;error&gt;My exception localized message.&lt;/text&gt;
&lt;/Message&gt;
</pre>


Internationalization of errors
------------------------------

There are basic default error messages defined. You can define your own and 
translate them by adding the following keys to your 
<code>conf/messages.properties</code> files:

* Bad request: <code>ninja.system.bad_request.text</code>
* Internal server error: <code>ninja.system.internal_server_error.text</code>
* Route not found: <code>ninja.system.not_found.text</code>

Keys and default values are defined in <code>ninja.NinjaConstant</code>.

Exception messages can also be defined as keys in your 
<code>conf/messages.properties</code>, while the localized exception 
message will be used if no translation exists. So for 
example <code>throw new BadRequestException("error.invalidData");</code> 
will result in a translation of `error.invalidData` in your error page 
rendering context.

More
----

If you want to tweak Ninja's error handling even more have a look at "custom routing".
That's an advanced topic and explains <code>conf.Ninja</code> and its possibilities.

