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

Results with the "s" at the end is a convenience class that allows you to generate a configured result.
For instance Results.html() will tell Ninja to render a html page. There is also Results.json().

By convention controllers live in the package "controllers".


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

We then of course want to know **id**, **email** and the value of the **debug** query parameter.

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

Method index now gets the contents of the path parameters and all query parameters.


A note on encoding / decoding
-----------------------------

Encoding / Decoding of Urls is not as easy as you think it is. Ninja tries to simplify everything
as much as possible, but as user of the Api you have to know what you are submitting to Ninja.

We recommend the following [excellent article from Lunatech](http://www.lunatech-research.com/archives/2009/02/03/what-every-web-developer-must-know-about-url-encoding) 
before you use encoding / decoding actively in your application.

Let's reconsider the controller method from above:

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

You can expect that String **id** and String **debug** are both correctly decoded values. **BUT** This assumes that
you are encoding the values correctly on the client side. And encoding is different for
query parameters or stuff in the path. And not - do not even think about using URLEncoder for encoding urls. This
is wrong.

Simple example that outlines some of the difficulties:
Think of a route "/user/{id}/userDashboard".

Let's say your **id** is "rootuser/domain". If you do not encode the slash in the middle you end up with a
url like /user/rootuser/domain/userDashboard. And the this url does not match the route
because of the "/".

Therefore you have to encode your id correctly. In that case it would be: rootuser%2Fdomain.
When the user then visits /user/rootuser%2Fdomain/userDashboard the route matches and
a @PathParam("id") would then be rootuser/domain as it is decoded by Ninja.

In principle it is really simple. But it is even simpler to mess encoding / decoding up.
The article from Lunatech mentioned earlier is awesome and explains everything.

Injecting stuff
---------------

Ninja can inject a lot of stuff into your methods. And you can customize that easily yourself by using so
called ArgumentMatchers.

Have a look at that method:

<pre class="prettyprint">
    package controllers;

    @Singleton
    public class ApplicationController {
        
        public Result index(
                @PathParam("id") String id, 
                @PathParam("email") String email, 
                @Param("debug") String debug,
                Context context,
                MyObject myObject) {
                
            //do something with the parameters...
        }

    }
</pre>

Ninja can not only inject PathParam and Param objects. But also the Context.

Even better: If there is a object Ninja does not know Ninja tries to parse that object into
a Java class. This is done by content negotiation. Therefore you don't have to worry if
input is for instance xml or json. You simply get a parsed object.


Rendering html
--------------

Rendering by convention has been already explained in the getting started part. But in short consider the
following class:

<pre class="prettyprint">
    package controllers;

    public class ApplicationController {       
    
        public Result index() {
            return Results.html();
    
        }
    }
</pre>

This renders by convention a view in directory **views/ApplicationCrontoller/index.ftl.html**.

But you can specify your own html view, too:

<pre class="prettyprint">
    package controllers;

    public class ApplicationController {       
    
        public Result index() {
            return Results.html().template("views/AnotherController/anotherview.ftl.html");
    
        }
    }
</pre>

In conclusion you can use convention over configuration in most cases - but you can also specify
a view to render explicitly.


Rendering Json
--------------

Consider this simple model class:

<pre class="prettyprint">
    package models;

    public class Person {       
    
        String name;
    }
    
</pre>

And this controller:

<pre class="prettyprint">
    package controllers;

    public class ApplicationController {       
    
        public Result index() {
        
            Person person = new Person();
            person.name = "John Johnson";
        
            return Results.json().render(person);
    
        }
    }
</pre>

This controller will produce a nicely formatted json output for you. Under the hood
Ninja uses Gson to transform arbitrary objects into Json string.



Advanced topic: Defining your own argument matcher
--------------------------------------------------

TODO

