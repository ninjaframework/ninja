Routing
=======

Ninja features <b>one central route file</b>. 
We think this is important, because you
can see immediately what routes your application provides. It especially 
facilitates designing a nice restful application and Api, because you have a good 
overview what is going on.

Basics
------

The route file is a plain old java file living at <code>conf/Routes.java</code>.

<code>Routes.java</code> implements the interface <code>ApplicationRoutes.java</code>. 
This interface defines a method called <code>public void init(Router router) {...}</code>
which allows us to map incoming requests to controllers and their methods.

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        // a GET request to "index" will be handled by a class called
        // "AppController" its method "index".
        router.GET().route("/index").with(AppController.class, "index");

        ...

    }
}
</pre>


The <code>init</code> method provides us with the router and router allows us to
define what happens for GET, POST, PUT and DELETE requests.

<div class="alert alert-info">
Routes are matched top down. If an incoming request potentially maps to
two routes, the route defined first is executed.
</div>


Regex in your routes
--------------------

Routes can contain arbitrary Java regular expressions. You can use these
regular expressions simply by putting them into the route definition.

Some examples:

<pre class="prettyprint">

// matches for instance "/assets/00012", "/assets/12334", ...
router.GET().route("/assets/\\d*").with(AssetsController.class, "serveDigits");

// matches for instance "/assets/myasset.xml", "/assets/boing.txt", ...
router.GET().route("/assets/.*").with(AssetsController.class, "serveArbitrary");

</pre>

In the first example <code>\\\\d\*</code> tells the router to match digits 
(defined by <code>\\\\d</code>) of arbitrary length (defined by <code>\*</code>). 
In the second example <code>.\*</code> lets the router match arbitrary characters 
(<code>.</code>) of arbitrary length (defined by <code>\*</code>).

This example also shows what happens if two routes match.
For instance a request to <code>/assets/00012</code> is matched by
both route definitions. In that case the first matching route from top will be 
executed it is executed. In our case method <code>serveDigits</code>.

More on regular expressions: 
http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html


Getting variable url parts into your controller method
------------------------------------------------------

A controller usually not only renders stuff, 
but also takes some inputs and does something with them.

Let's assume we got a request like that:
<pre class="prettyprint">
GET /user/12345/my@email.com/userDashboard
</pre>

Looks like a GET request to the userDashboard. The request seems to contain
an <code>id</code> and an <code>email</code>. As application developer you want to have a convenient
way to get <code>id</code> and <code>email</code> inside your controller. To that end Ninja 
allows you to define and name parts of your routes via 
curly braces <code>{...}</code>:

<pre class="prettyprint">
router.GET().route("/user/{id}/{email}/userDashboard").with(ApplicationController.class, "userDashboard");
</pre>

We can then inject <code>id</code> and <code>email</code> into our controller via
annotation <code>@PathParam</code>.

<pre class="prettyprint">
package controllers;

@Singleton
public class AppController {

    public Result userDashboard(
            @PathParam("id") String id, 
            @PathParam("email") String email) {

        //do something with the parameters...
    }


}
</pre>

<div class="alert alert-info">
By default Ninja replaces curly braces with the following regex to match a 
variable part: <code>([^/]*?)</code>. That means that variable parts match arbitrary
characters but do not span over any path separators "/".
</div>


Injecting objects into the Router
---------------------------------

Because the Route.java is just a plain old java file we can also inject
arbitrary stuff into it (via Guice).

A popular use case is to inject ninjaProperties. This allows us to activate / 
deactivate certain routes based on the environment we are in.

The following example shows that a setup route is not available when
running in production:

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {
    
    @Inject
    NinjaProperties ninjaProperties;

    @Override
    public void init(Router router) {
        
        // a GET request to "index" will be handled by a class called
        // "AppController" its method "index".
        router.GET().route("/index").with(AppController.class, "index");

        ...

        // only active when not in production mode:
        if (!ninjaProperties.isProd) {
            router.GET().route("/setup").with(AppController.class, "setup");
        }
    }
}
</pre>