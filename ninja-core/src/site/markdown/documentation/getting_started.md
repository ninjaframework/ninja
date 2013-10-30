Quickstart
==========

The best way to start a Ninja project is by calling the following command:

<pre class="prettyprint">
    mvn archetype:generate -DarchetypeGroupId=org.ninjaframework -DarchetypeArtifactId=ninja-servlet-jpa-blog-archetype
</pre>

Please enter sensible values for "groupId" and "artifactId" and let Maven generate your first Ninja project.


After finishing the generation cd into your project and execute:
<pre class="prettyprint">
    mvn jetty:run
</pre>

This starts the development webserver. And that's it already. Simply open http://localhost:8080 in your browser and start hacking :)

__Note__: Not sure what "mvn" or Maven is? Check out http://maven.apache.org/guides/getting-started

How to set up your favorite IDE (Eclipse, Netbeans, IntelliJ)
-------------------------------------------------------------

The Ninja application you created above is just a simple plain old Maven project. 
This means you can import the project into any modern IDE. Eclipse, Netbeans, IntelliJ and many more. 

Using an IDE is also the most productive setup for Ninja. 
Change a class in your IDE - then your IDE will recompile the class files, 
jetty (running as jetty:run) will pick up the changes and restart the server.

This is blazingly fast and you can see the results of your 
changes almost immediately at http://localhost:8080.

No more packaging, waiting 30 seconds then deploying to an application server. 
Developing Ninja applications is superfast.

__Note:__ If you are running into PermGen exceptions make sure you set the following maven parameter:

<pre>
export MAVEN_OPTS="-Xms512m -Xmx1024m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled"
</pre>

More information at this excellent blog post: http://java.jiderhamn.se/2011/12/11/classloader-leaks-i-how-to-find-classloader-leaks-with-eclipse-memory-analyser-mat


The MVC pattern
---------------

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





