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


Changing the default views
--------------------------

The default views for errors can be found here:

 * <code>views/system/400badRequest.ftl.html</code>
 * <code>views/system/404notFound.ftl.html</code> (If a route cannot be found).
 * <code>views/system/403forbidden.ftl.html</code> (If a route cannot be found).
 * <code>views/system/500internalServerError.ftl.html</code>

You can overwrite the views by creating the appropriate files in your application
at the very same locations (<code>views/system/...</code>).

This allows you to use your own styling and messages for the error views.


More
----

If you want to tweak Ninja's error handling even more have a look at "custom routing".
That's an advanced topic and explains <code>conf.Ninja</code> and its possibilities.