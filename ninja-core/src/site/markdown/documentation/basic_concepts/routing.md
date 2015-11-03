Routing
=======

Ninja features <b>one central route file</b>. 
We think this is important, because you immediately
can see  what routes your application provides. It especially 
facilitates designing a nice RESTful application and API, because you have a good 
overview what is going on.

Basics
------

The route file is a plain old Java file living at <code>conf/Routes.java</code>.

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


The <code>init(...)</code> method provides us with the <code>Router</code> and router allows us to
define what happens for GET, POST, PUT, OPTIONS, HEAD and DELETE requests.

And if you want to route an HTTP method not yet supported by Ninja out of
the box you can always use
<code>route.METHOD("MY_CUSTOM_HTTP_METHOD").route(...)...</code>.

<div class="alert alert-info">
Routes are matched top down. If an incoming request potentially maps to
two routes, the route defined first is executed.
</div>


With result directly
--------------------
You can use a Result object to render a static page simply without any controller.

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        // a GET request to "/" will be redirect to "/dashboard"
        router.GET().route("/").with(Results.redirect("/dashboard"));
        
        // show a static page
        router.GET().route("/dashboard").with(Results.html().template("/dashboard.html"));

        ...

    }
}
</pre>




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
executed. In our case method <code>serveDigits</code>.

More on regular expressions: 
http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html


Getting variable URL parts into your controller method
------------------------------------------------------

A controller usually not only renders stuff, 
but also takes some inputs and does something with them.

Let's assume we got a request like that:
<pre class="prettyprint">
GET /user/12345/my@email.com/userDashboard
</pre>

Looks like a GET request to the userDashboard. The request seems to contain
an <code>id</code> and an <code>email</code>. As application developer you want to have a convenient
way to get <code>id</code> and <code>email</code> inside your controller. Ninja 
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
variable part: <code>([^/]*)</code>. That means that variable parts match arbitrary
characters but do not span over any path separators "/". If you need more control
please you'll find more in the next section.
</div>


Regular expressions in variable route parts
-------------------------------------------

Ninja allows you to specify regular expressions 
that will be used to match arbitrary parts of your URL.

The syntax is <code>{PRAMETER_NAME: REGEX}</code> (with a whitespace after the ":").

For instance a route like <code>/assets/{fileName: .*}</code> will match everything
after <code>/assets/</code>. Imagine a request to <code>/assets/css/app.css</code>. 
This request will be handled by the route and accessing the path parameter 
<code>fileName</code> will return <code>css/app.css</code>.

Routes can contain multiple variable parts with regular expressions.

For example, for a request to <code>/categories/1234/products/5678</code>, where category is
expected to be an integer value and product to be either integer or string value, 
you can define routes like that:

<pre class="prettyprint">
router.GET().route("/categories/{catId: [0-9]+}/products/{productId: [0-9]+}").with(ProductController.class, "product");
router.GET().route("/categories/{catId: [0-9]+}/products/{productName: .*}").with(ProductController.class, "productByName");
</pre>

The request above will be handled by the first route, and request to <code>/categories/1234/products/mouse</code>
will be handled by the second route.

Values of variable parts of a route are injected into our controller (explained above)
and are implicitly validated with regular expressions.
So our controller for routes above would be like that (look at <code>@PathParam</code> argument types):
<pre class="prettyprint">
package controllers;

@Singleton
public class ProductController {

    public Result product(
            @PathParam("catId") int catId, 
            @PathParam("productId") int productId) {

        // find product by id in given category 
    }

    public Result productByName(
            @PathParam("catId") int catId, 
            @PathParam("productName") String productName) {

        // find product(s) by name in given category 
    }
}
</pre>

Note at how regular expressions in routes can be used to validate path parameters and
to define fine grained routing.

You can use any Java compliant regular expressions for matching. Please refer to
the official documentation at http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html.


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


Reverse routing
---------------

Let's say you want to know the final route of a class ApplicationController.class, method "index". Assume
that the original raw route looked like a simple <code>"/"</code>.

You can get the final URL by injecting the router into your controller and then calling getReverseRoute

<pre class="prettyprint">
@Inject
Router router;

...

public void myMethod() {

    // will result into "/"
    String generatedReverseRoute 
        = router.getReverseRoute(ApplicationController.class, "index");
    ...

}      
</pre>

Now consider a more complex example. Say the original raw route contained placeholders on the following form:
<code>/user/{id}/{email}/userDashboard</code>. You can now ask the router for the final URL, but you must
provide a map containing mappings from placeholders to final values. If the key cannot be found as
placeholders their value will be added as query parameters:


<pre class="prettyprint">
@Inject
Router router;

...

public void myMethod() {

    map = Maps.newHashMap();
    map.put(&quot;id&quot;,&quot;myId&quot;);
    map.put(&quot;email&quot;,&quot;myEmail&quot;);
    map.put(&quot;paging_size&quot;,&quot;100&quot;);
    map.put(&quot;page&quot;,&quot;1&quot;);

    // this will result into &quot;/user/myId/myEmail/userDashboard?paging_size=100&amp;page=1&quot;
    String generatedReverseRoute 
        = router.getReverseRoute(
            ApplicationController.class, 
            &quot;userDashboard&quot;, 
            map);

    ...
}      
</pre>


A note on encoding / decoding
-----------------------------

Encoding / Decoding of URLs is not as easy as you might think it is. Ninja tries to simplify everything
as much as possible, but as a user of the API you have to know what you are 
submitting to Ninja.

We recommend the following [excellent article from Lunatech](http://blog.lunatech.com/2009/02/03/what-every-web-developer-must-know-about-url-encoding)
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

You can expect that String **id** and String **debug** are both correctly decoded values. 
**BUT** This assumes that
you are encoding the values correctly on the client side. 
And encoding is different for
query parameters or stuff in the path. Do not even think about using 
URLEncoder for encoding URLs. This is wrong.

Simple example that outlines some of the difficulties:
Think of a route "/user/{id}/userDashboard".

Let's say your **id** is "rootuser/domain". If you do not encode the slash in 
the middle you end up with a URL like /user/rootuser/domain/userDashboard. 
And the this URL does not match the route
because of the "/".

Therefore you have to encode your id correctly. In that case it would be: rootuser%2Fdomain.
When the user then visits /user/rootuser%2Fdomain/userDashboard the route matches and
a @PathParam("id") would then be rootuser/domain as it is decoded by Ninja.

In principle it is really simple. But it is even simpler to mess encoding / decoding up.
The article from Lunatech mentioned earlier is awesome and explains everything.

## JAX-RS-style Annotated Routes in Ninja (optional)

*Ninja-jaxy-routes* allows you to register your routes using annotations similar to JAX-RS.

You may use the standard Ninja route registration in combination with this route builder or you may replace all your route registrations with annotations.

**NOTE:** Your annotated controllers must be located somewhere within your application's configured controller package or a subpackage thereof.

### Add the ninja-jaxy-routes dependency

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-jaxy-routes</artifactId>
        <version>${ninja.version}</version>
    </dependency>

### Initialize `JaxyRoutes` in your `conf.Routes` class.

    @Inject
    JaxyRoutes jaxyRoutes;
    
    @Override
    public void init(Router router) {
    
        jaxyRoutes.init(router);
        
    }

### Annotate Your Controllers

Now you are ready to start annotating your controllers.

#### Paths

*Ninja-jaxy-routes* supports multiple `@Path` specs per controller method and also supports controller class inheritance.

The following example will register two **GET** routes `/base/middle/app/get` and `/base/middle/app/retrieve` for the same controller method. 

    @Path("/base")
    class Base {
    }
    
    @Path("/middle")
    class Middle extends Base {
    }

    @Path("/app")
    class App extends Middle {
    
        @Path({"/get", "/retrieve"})
        @GET
        Result get() {
            return Results.text().renderRaw("Yahoo!"); 
        }        
    }

If the `Base` and `Middle` parent classes had each specified multiple paths, all permutations of the complete routes would be registered too.


#### HTTP Methods

By default, all routes are assumed to be **GET** routes unless they are specifically annotated.

The following common HTTP method annotations are available:

- `@DELETE`
- `@GET`
- `@HEAD`
- `@OPTIONS`
- `@PATCH`
- `@POST`
- `@PUT`

If the built-in methods are insufficient, you may implement your own custom HTTP methods:

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @HttpMethod("CUSTOM")
    public @interface CUSTOM {
    }

#### Registration Order

Since your controllers and methods are reflectively loaded we can not expect a predictable route registration order from the JVM.

To compensate for this, you may specify the `@Order` annotation on a controller method to dictate the ordering of routes.

1. It is not necessary to specify an `@Order` for every method.
2. Lower numbers are registered first, higher numbers later.
3. If two methods have the same order, the registration order is determined by String comparison of the complete method address (controller+method).

Here is an example of specifying an `@Order`.

    @Path({"/get", "/retrieve"})
    @GET
    @Order(10)
    Result something() {
    }

#### Runtime Mode Inclusions/Exclusions

You may include/exclude routes based on the Ninja runtime mode.

1. If no mode annotations are specified, then the route is available in all modes.
2. You may specify multiple mode annotations on a controller method.

- `@Dev`
- `@Prod`
- `@Test`

Here is an example of specifying `@Dev` and `@Test`.

    @Path("/diagnostics")
    @GET
    @Dev @Test
    Result diagnostics() {
    }


#### NinjaProperties Inclusions/Exclusions

It is also possible to include/exclude a route based on a NinjaProperties key.

If the key does not exist in your runtime config, the route is not registered.

    @Path("/sneaky")
    @GET
    @Requires("sneaky.key")
    Result somethingSneaky() {
    }
