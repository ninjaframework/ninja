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
For instance <code>Results.html()</code> will tell Ninja to render an HTML page. 
But you can also render JSON and other results.

By convention controllers live in the package "controllers".


Getting parameters into your controllers
----------------------------------------

A controller usually not only renders stuff, 
but also takes some inputs and does something with them.

You can get parts of the URL inside your controller via three annotations:
<code>@Param</code>, <code>@Params</code> and <code>@PathParam</code>. Via
@PathParam you can get variable parts of a route (described in more details
in the routing section).

@Param allows you to get single-value Query or Form parameters.
@Params allows you to get multi-value Query parameters.

Let's say and the user visits the following URL...

<pre class="prettyprint">
/user/12345/my@email.com/userDashboard?debug=false&filters=new&filters=urgent
</pre>

... and we got a route definition like that:

<pre class="prettyprint">
router.GET().route("/user/{id}/{email}/userDashboard").with(AppController::userDashboard);
</pre>

We can then get all variable parts of this URL via the following method controller
definition:

<pre class="prettyprint">
package controllers;

@Singleton
public class AppController {

    public Result userDashboard(
            @PathParam("id") String id, 
            @PathParam("email") String email, 
            @Param("debug") Optional&lt;String&gt; debug,
            @Param("isAdmin") Boolean isAdmin,
            @Params("filters") String [] filters,
            @Header("user-agent") String userAgent) {

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
            @Param("debug") Optional&lt;String&gt; debug,
            @Param("isAdmin") Boolean isAdmin,
            Context context,
            MyObject myObject) {

        //do something with the parameters...
    }

}
</pre>

Ninja can not only inject @PathParam and @Param objects. But also the Context.
The context is a request scoped object that holds all information of the current
request - parameters, headers and so on...

Even better: Ninja will parse an arbitrary object given in the method.
In the above case MyObject will be automatically parsed by Ninja. The way
it will parsed (JSON, XML, POST form) will be determined via the "Content-Type" request header.

If you are using the POST form way, this object can contains any parameter type listed in the 
<a href="http://www.ninjaframework.org/documentation/basic_concepts/argument_extractors.html">Argument extractors</a> 
chapter (or custom ones, as described in the same chapter). Custom objects contained in this 
arbitrary object can also be parsed if they have a constructor without arguments.

<pre class="prettyprint">
public class Address {
    String street;
}

public class User {
    Address address;
}
</pre>

Assuming your controller method want to receive a User object, your HTML form can send a field 
value for the key <code>address.street</code>.

If your custom object has a date/timestamp field, first make sure that you use the 
<a href="http://docs.oracle.com/javase/7/docs/api/java/util/Date.html">java.util.Date</a> 
type and when you pass some date to the controller pass in the 
<a href="http://joda-time.sourceforge.net/api-release/org/joda/time/format/ISODateTimeFormat.html#localDateOptionalTimeParser%28%29">format</a> accepted by Joda Time library, or implement your own 
<code>ParamParser</code> for a java.util.Date.

## A note about null and Optional

### The default, but deprecated way

In the example above we got two parameters: "debug" and "isAdmin". If both
parameters are not supplied then debug will be Optional.empty() and 
isAdmin will be null.

But null has a lot of problems and Optional is the clean solution to say "The
value may be there - or not". Therefore <b>Ninja will deprecate the usage of null
in controller methods from version 7.0.0 onwards</b>.

Ninja makes the transition smooth by providing a configuration variable that
allows to control what behavior you want to use. By default the value will be false.

<pre>
ninja.strict_argument_extractors=false        # accepts null 
</pre>


### The new way (Default from Ninja 7.0.0)

The new way eliminates the need to check for nulls.

Activate the mode by setting a configuration variable in application.conf: 

<pre>
ninja.strict_argument_extractors=true         # redirects to Bad Request page if parameter is null 
</pre>

Then

* use Optional (eg Optional&lt;String&gt; debug) when the parameter may be supplied by the user - or not.
* don't use Optional (eg Boolean isAdmin) if you expect the parameter to be there.
  If the parameter is not supplied by a user request then Ninja issues a Bad Request. Your controller method
  code will never be executed.

### How to upgrade from the deprecated way to the new way

Make sure you upgrade asap to be compatible with future versions of Ninja.

1) Activate the mode by setting a configuration variable in application.conf: 

<pre>
ninja.strict_argument_extractors=true         # redirects to Bad Request page if parameter is null 
</pre>

2) Then investigate all your controller methods. 

If you do null checks on parameters that get injected into your controller 
then change them to Optional. That way you get rid of all null checks.

Things like @PathParam, Context and Session will never be Optional, because Ninja
is always able to provide them. But things like @Param or custom argument
extractors may well be optional.


## Ninja and content negotiation

In the last section we have already seen that parsing of objects works via content 
negotiation.

Rendering of objects works the same way and evaluates the "Accept" request header for that.

Therefore a controller method like...

<pre class="prettyprint">
public void Result getUser() {
    User user = userDao.getUser();
    return Results.ok().render(user);
}
</pre>

...will render XML when the "Accept" request header is "application/xml" or JSON when
the "Accept" header is "application/json".

While content negotiation is pretty cool it sometimes breaks down in
reality. 

Especially if clients send strange headers or if you want to
be sure that a certain controller method always renders a return format. 

Then you can simply call use <code>return Results.json().render(user)</code> or 
<code>return Results.xml().render(user)</code> to enforce the rendering of a certain return format.




Rendering HTML
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

This renders by convention a view in directory <code>views/ApplicationController/index.ftl.html</code>.

But you can specify your own HTML view, too:

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
