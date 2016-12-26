The MVC pattern
===============

Ninja is built around the MVC pattern. MVC stands for Model View Controller. In
essence that means that each layer of logic is cleanly separated.

Your first controller
---------------------

A controller resides under the package *controllers*.

Controllers are just simple Java classes. 

<pre class="prettyprint">
public class ApplicationController {       

    public Result index() {
        return Results.html();

    }
}
</pre>

Controller methods always return a "Result". Results is just a little helper that lets you create
results easily. In the case of the application controller the result is a html response.

The controller per se does not do anything. We need to tell Ninja that the ApplicationController exists.


Your first route
----------------

This is done by declaring a file called *Routes.java* in the package *conf*.

A minimal route file looks like:

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {

        router.GET().route("/").with(ApplicationController::index);

    }
}
</pre>
    
The route is loaded by a convention right at the start of Ninja. It simply tells Ninja that requests to /
will be handled by the ApplicationController.class and the method "index".

Now we got one side of the equation - from Ninja to the routes to our controller. But what about the output?


Your first view
---------------

Views are declared inside the package *views*. By convention views for controllers are always stored in
views/*CONTROLLER_NAME*/*METHOD_NAME*. In our case the view will be retrieved from views/ApplicationController/index.ftl.html.

These views are just simple templates. You can enter simple html. But you can also use extended features as the templating
is built around freemarker templates.

Simply put a general html page into the view:


<pre class="prettyprint">
&lt;html&gt;
    &lt;body&gt;
        &lt;h1&gt;Hello world&lt;/h1&gt;
    &lt;/body&gt;
&lt;/html&gt;   
</pre>

The result
----------
Voila! you got your first running Ninja application. We defined a route, a controller and a simple view. That's all. Ninja
retrieves everything using some conventions you already saw. Routes are under conf.Routes.java. views in a directory
named after their controller. 





