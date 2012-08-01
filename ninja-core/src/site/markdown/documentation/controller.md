Controller
==========

Controller are the "C" in the MVC paradigm.

As almost everything else in Ninja a controller in Ninja is just a simple 
Java class that follows some little conventions.

A really simple controller
--------------------------

A really simple controller would look like:

<pre class="prettyprint">  
	package controllers;

	@Singleton
	public class ApplicationController {
		
		public Result index() {
		
			return Results.html();
		}


	}
</pre>

The important conventions are that a controller method must return a "Result". A result
is in short just an object that holds response codes, content to render, headers and cookies
that should be returned to the visitors of your website.

By convention we put controllers into package "controllers".


Getting parameters into your controllers
----------------------------------------

A controller usually not only renders stuff, but also takes some inputs and does something with them.

Let's say we got a route like that:
<pre class="prettyprint">  
	router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
</pre>

... and the user visits that Url...

<pre class="prettyprint">  
	/user/12345/my@email.com/userDashboard?debug=false
</pre>

We then of course want to know id, email and the value of the debug query parameter.

In Ninja this is really simple and controller that can do so looks like:

<pre class="prettyprint">  
	package controllers;

	@Singleton
	public class ApplicationController {
		
		public Result index(
				@PathParam("id") String id, 
				@PathParam("email") String email, 
				@Param("debug") String debug) {
				
			//do something with the parameters...
		}


	}
</pre>

Method index now gets the contents of the path params and all query parameters.


A note on encoding / decoding
-----------------------------

Encoding / Decoding of urls is not as easy as you think it is. Ninja tries to simplify everything
as much as possible, but as user of the Api you have to know what you are submitting to Ninja.

We recommend the following [excellent article from Lunatech](http://www.lunatech-research.com/archives/2009/02/03/what-every-web-developer-must-know-about-url-encoding) 
before you use encoding / decoding actively in your application.

In short: If you are using a controller like that:

<pre class="prettyprint">  
	package controllers;

	@Singleton
	public class ApplicationController {
		
		public Result index(
				@PathParam("id") String id, 
				@PathParam("email") String email, 
				@Param("debug") String debug) {
				
			//do something with the parameters...
		}

	}
</pre>

You can expect that String id and String debug are both correctly decoded values. This assumes that
you are encoding the values correctly on the client side. And encoding is different for
query parameters or stuff in the path.

Simple example that outlines some of the difficulties:
Think of a route "/user/{id}/userDashboard".

Let's say your "id" is "rootuser/domain". If you do not encode the slash in the middle you end up with a
Url like /user/rootuser/domain/userDashboard. And the this url does not match the route
because of the "/".

Therefore you have to encode your id correctly. In that case it would be: rootuser%2Fdomain.
When the user then visits /user/rootuser%2Fdomain/userDashboard the route matches and
a @PathParam("id") would then be rootuser/domain as it is decoded by Ninja.

In principle it is really simple. But it is even simpler to mess encoding / decoding up.
The [Lunatech article explains all pitfalls nicely](http://www.lunatech-research.com/archives/2009/02/03/what-every-web-developer-must-know-about-url-encoding).


Injecting stuff
---------------
TODO


Rendering stuff
---------------
TODO


Validation
----------
TODO


