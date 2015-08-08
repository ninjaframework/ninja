Basic concepts: route - controller - view
===========================================

A route
-------

The route file is the entry point of every HTTP request. Somehow there must be 
mapping between a URL like /index.html or /person.json to something inside
your application that actually does something. And that is defined in the route
file.

By conventions every Ninja application contains the following 
Java file: <code>conf/Routes.java</code>.
<code>Routes.java</code> contains all the routes for your application.

A minimal route file looks like:

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {

        router.GET().route("/").with(ApplicationController.class, "index");

    }
}
</pre>

This just means that a request to <code>/</code> will be handled by a class called 
<code>ApplicationController</code> and its method <code>index</code>.

... but how does that <code>ApplicationController</code> look like?

A controller
------------

Controllers are just simple Java classes and should be placed under the package 
<code>controllers</code>.

<pre class="prettyprint">
package controllers;

@Singleton
public class ApplicationController {       

    public Result index() {
        return Results.html();

    }
}
</pre>

Controller methods always return a <code>Result</code>. 
<code>Results</code> (with "s" at the end) is just 
a little helper that lets you create results easily. 
In the case of the application controller the result is an HTML response.


Now we got one side of the equation - 
from Ninja to the routes to our controller. But how does Ninja generate the HTML
ouput?


A view
------

Views are declared inside package <code>views</code>. 
By convention views for controllers are always stored in
<code>views/CONTROLLER_NAME/METHOD_NAME</code>. 
In our case the view will be retrieved from 
<code>views/ApplicationController/index.ftl.html</code>.

A really simple view can look like:

<pre class="prettyprint">
&lt;html&gt;
    &lt;body&gt;
        &lt;h1&gt;Hello world&lt;/h1&gt;
    &lt;/body&gt;
&lt;/html&gt;   
</pre>


Conclusion
----------

This is the basic concept of Ninja, and this is all you need to start your own 
application. The basic flow of operations of Ninja is always <code>Route => Controller => View</code>.

Of course views can not only render HTML, but also JSON or XML. And you can of course
render models with your views. More about that in the following sections of
the documentation.





