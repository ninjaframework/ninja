Quickstart for users
====================

The best way to start a Ninja project is by calling the following command:

<pre class="prettyprint">
    mvn archetype:generate -DarchetypeGroupId=org.ninjaframework -DarchetypeArtifactId=ninja-core-demo-archetype
</pre>

Please enter sensible values for "groupId" and "artifactId" and let Maven generate your first Ninja project.

After finishing the generation cd into your project and execute:

<pre class="prettyprint">
    mvn jetty:run
</pre>

This starts the dev webserver. And that's it already. Simply go to http://localhost:8080 and start hacking :)


Preface
-------

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
        
            router.GET().route("/").with(ApplicationController.class, "index");
        
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



    <html>
        <body>
            <h1>Hello world</h1>
        </body>
    </html>    


The result
----------
Voila! you got your first running Ninja application. We defined a route, a controller and a simple view. That's all. Ninja
retrieves everything using some conventions you already saw. Routes are under conf.Routes.java. views in a directory
named after their controller. 





