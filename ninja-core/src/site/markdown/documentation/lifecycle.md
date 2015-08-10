Lifecycle
=========

Introduction
-------------

If we are talking about services we also have to talk about priority startup and shutdown.
You want your services being started and stopped in an orderly manner.

You can use Ninja's <code>@Start</code> and <code>@Dispose</code> annotations to do so.



Example
-------

The class and method then looks like:

<pre class="prettyprint">
@Singleton
public class MyService {

    @Start(order = 90)
    public void startService() {
        //do something       
    }

    @Dispose(order = 90)
    public void stopService() {
       //do something
    }

    public Result getCount(Context ctx) {
        return Results.json(count.get());
    }
}    
</pre>

Don't forget to bind the class explicitly inside conf/Module.java

<pre class="prettyprint">
public class Module extends AbstractModule {

    protected void configure() {

        bind(MyService.class);

    }
}
</pre> 

By that Ninja will start MyService and also stop it nicely.



Starting and stopping services in order
---------------------------------------

The order in which it should be started, higher meaning later. 
While apps are free to use any ordering system they wish, the following convention is recommended:

 * 10 - Services that connect to resources and do not depend on other services, for example, database connections
 * 20-80 - Services that depend on resources, but don't actually start the app doing its core functions
 * 90 - Services that start the app doing its core functions, for example, listen on queues, listen for HTTP, start scheduled services


