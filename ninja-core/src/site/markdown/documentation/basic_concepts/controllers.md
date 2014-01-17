Controller
==========

Introduction
------------
Controllers are the "C" in the MVC paradigm. Controllers will do actual stuff.
They get requests or part of requests, retrieve stuff from databases and
cause Ninja to return certain results.

A really simple controller
--------------------------

A basic controller looks like:

<pre class="prettyprint">

	package controllers;

	@Singleton
	public class AppController {
		
		public Result index() {
		
			return Results.html();
		}


	}

</pre>

The important conventions are that a controller method must return a <code>Result</code>. 
A result is in short just an object that holds response codes, 
content to render, headers and cookies that should be returned to the visitors of your website.

<code>Results</code> with the "s" at the end is a convenience class 
that allows you to generate a preconfigured <code>Result</code>.
For instance <code>Results.html()</code> will tell Ninja to render a html page. 
But you can also render json and other results.

By convention controllers live in the package "controllers".


Getting parameters into your controllers
----------------------------------------

A controller usually not only renders stuff, 
but also takes some inputs and does something with them.

You can get parts of the url inside your controller via two annotations:
<code>@Param</code> and <code>@PathParam</code>. Via @PathParam you can get 
variable parts of a route (described in more details in the routing section).
And @Param allows you to get Query or Form parameters.

Let's say and the user visits the following Url...

<pre class="prettyprint">
/user/12345/my@email.com/userDashboard?debug=false
</pre>

... and we got a route definition like that:

<pre class="prettyprint">
router.GET().route("/user/{id}/{email}/userDashboard").with(AppController.class, "userDashboard");
</pre>

We can then get all variable parts of this url via the following method controller
definition:

<pre class="prettyprint">
package controllers;

@Singleton
public class AppController {

    public Result userDashboard(
            @PathParam("id") String id, 
            @PathParam("email") String email, 
            @Param("debug") String debug) {

        //do something with the parameters...

    }

}

</pre>


More on objects Ninja can automatically provide you at controller level
-----------------------------------------------------------------------

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
The context is a request scoped object that holds all informations of the current
request - parameters, headers and so on...

Even better: Ninja will parse an arbitrary object given in the method.
In the above case MyObject will be automatically parsed by Ninja. The way
it will parsed (Json, Xml, PostForm) will be determined via the content type request header.

Therefore you don't have to worry if
input is for instance Xml or Json. You simply get a parsed object.


## Ninja and content negotiation

In the last section we have already seen that parsing of objects works via content 
negotiation.

Rendering of objects works the same way and evaluates the Accept header for that.

Therefore a controller method like...

<pre class="prettyprint">

    public void Result getUser() {
        User user = userDao.getUser();
        return Results.ok().render(user);
    }

</pre>

...will render xml when the request accept header is "application/xml" or json when
the accept header is "application/json".

While content negotiation is pretty cool it sometimes breaks down in
reality. 

Especially if clients send strange headers or if you want to
be sure that a certain controller method always renders a return format. 

Then you can simply call use <code>return Results.json().render(user)</code> or 
<code>return Results.xml().render(user)</code> to enforce the rendering of a certain return format.




Reverse routing
---------------

Let's say you want to know the final route of a class ApplicationController.class, method "index". Assume
that the original raw route looked like a simple <code>"/"</code>.

You can get the final url by injecting the router into your controller and then calling getReverseRoute

<pre class="prettyprint">

    @Inject
    Router router;
        
    ...
        
    public void myMethod() {
        
        // will result into "/"
        String generatedReverseRoute 
            = router.getReverseRoute(
                ApplicationController.class, 
                "index");
        
        ...
    }    
   
</pre>

Now consider a more complex example. Say the original raw route contained placeholders on the following form:
<code>/user/{id}/{email}/userDashboard</code>. You can now ask the router for the final url, but you must
provide a map containing mappings from placeholders to final values. If the key cannot be found as
placeholders their value will be added as query parameters:


<pre class="prettyprint">

    @Inject
    Router router;
    
    ...
    
    public void myMethod() {
    
        map = Maps.newHashMap();
        map.put("id","myId");
        map.put("email","myEmail");
        map.put("paging_size","100");
        map.put("page","1");
    
        // this will result into "/user/myId/myEmail/userDashboard?paging_size=100&page=1"
        String generatedReverseRoute 
            = router.getReverseRoute(
                ApplicationController.class, 
                "userDashboard", 
                map);

        ...
    } 
      
</pre>


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

This renders by convention a view in directory **views/ApplicationController/index.ftl.html**.

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

This controller will produce a nicely formatted Json output for you. Under the hood
Ninja uses Jackson to transform arbitrary objects into Json.


Rendering JsonP
--------------

Use the same approach as for Json to produce JsonP (Json wrapped by Javascript function call):

<pre class="prettyprint">

    Results.jsonp().render(person);

</pre>

Parameter named **callback** should hold the name of the Javascript function or 
object path with function name (for example, **?callback=MyApp.Path.myCallback123**):

<pre class="prettyprint">

    MyApp.Path.myCallback123({'response': 'data'})

</pre>

The name of the GET parameter ("callback" is default) can be changed 
by **ninja.jsonp.callbackParameter** property in **application.conf**.


Rendering Xml
--------------

This is almost exactly the same as rendering Json.
Let's use again a simple Person Pojo:

Consider this simple model class:

<pre class="prettyprint">

    package models;

    public class Person {       
    
        String name;
    }
    
</pre>

Rendering is done by using Results.xml.render(...) like so:

<pre class="prettyprint">

    package controllers;

    public class ApplicationController {       
    
        public Result index() {
        
            Person person = new Person();
            person.name = "John Johnson";
        
            return Results.xml().render(person);
    
        }
    }

</pre>

As in the case we are using Jackson under the hood the does the transformation work.




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

