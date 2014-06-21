Error handling
==============

Even the most simplistic applications have to handle errors some way or the other.
Ninja supports you by providing a default mechanism to throw exceptions and
handle them via html views.

Ninja's exceptions
------------------

Ninja provides two types of exceptions: <code>BadRequestException</code> and 
 <code>InternalServerErrorException</code>.

A  <code>BadRequestException</code> should signal an faulty request by the error. It is
similar in mindset as a html error 400.

An <code>InternalServerErrorException</code> signals that something went wrong
inside your applications - pretty much like a html error 500.

Both exceptions are unchecked, and you can wrap your own exceptions inside them.
For instance if you encounter an  <code>IOException</code> you can rethrow that exception
by calling  <code>throw new InternalServerErrorException(ioException)</code>.

At the very end of the request handling Ninja will detect the exception and
render an appropriate error page.


Errors and content negotiation
------------------------------

The representation of the error to the user is based on content negotiation. Ninja
by default properly communicates errors in html, json and xml.


Html error representation
-------------------------

The default html views for errors can be found here:

 * <code>views/system/400badRequest.ftl.html</code>
 * <code>views/system/404notFound.ftl.html</code> (If a route cannot be found).
 * <code>views/system/403forbidden.ftl.html</code> (If a route cannot be found).
 * <code>views/system/500internalServerError.ftl.html</code>

You can overwrite the views by creating the appropriate files in your application
at the very same locations (<code>views/system/...</code>).

This allows you to use your own styling and messages for the error views.


Json and Xml error representations
----------------------------------

Json and Xml errors will both be rendered by content negotiation and their
default template rendering engines. Errors thus will be rendered as Json or Xml if
the user sends header Accept: application/json or Accept: application/xml. 

The error itself based on ninja.util.Message which contains one field called "text".

By default the Json error as Message will look like:
<pre class="prettyprint">
{
    "text": "Oops. The requested route cannot be found."
}
</pre>

And Xml Message looks like:
<pre class="prettyprint">
&lt;Message xmlns=&quot;&quot;&gt;
    &lt;text&gt;Oops. The requested route cannot be found.&lt;/text&gt;
&lt;/Message&gt;
</pre>


Internationalization of errors
------------------------------

There are basic default error messages defined. You can define your own and 
translate them by adding the following keys to you
 <code>conf/messages.properties</code> files:

* Bad request: <code>ninja.system.bad_request.text</code>
* Internal server error: <code>ninja.system.internal_server_error.text</code>
* Route not found: <code>ninja.system.not_found.text</code>

Keys and default values are defined in <code>ninja.NinjaConstant</code>.

More
----

If you want to tweak Ninja's error handling even more have a look at "custom routing".
That's an advanced topic and explains <code>conf.Ninja</code> and its possibilities.