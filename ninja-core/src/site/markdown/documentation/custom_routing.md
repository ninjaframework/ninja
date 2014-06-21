Custom routing
==============

In many projects Ninja's default routing is just fine. As well as the way
Ninja handles and reacts to errors and exception.

But sometimes you need just more flexibility and control. Let's say you want
to add extensive reporting to your application. In effect you want to report
and log all requests, all error - everything that's going on inside your application.

You can try to tackle the issue by using a Filter. But there's a more powerful
way.

Center stage for <code>conf.Ninja</code>.

Simple and straight forward
---------------------------

The whole request cycle - and the response to errors and exceptions
is handled by Ninja itself by default. But by 
creating a class at location <code>conf.Ninja</code>
you can modify, extend and tweak the default handling.

The best way to start is to extend <code>ninja.NinjaDefault</code>. NinjaDefault
is the default class handling all events - and therefore you can simply
overwrite the behavior you want to tweak without writing much code.

<pre class="prettyprint">
package conf;

public class Ninja extends NinjaDefault {

    @Inject
    CustomReportingMachine customReportingMachine;
    
    @Override
    public void onRouteRequest(Context.Impl context) {
        customReportingMachine.report(context);
        super.onRouteRequest(context);
    }
    
}
</pre>

In the case above you can see that we are injecting a customReportingMachine
that can log every request. We then simply call super.onRouteRequest(context);
and continue the route handling as usual.

You can of course overwrite all essential methods Ninja uses during the
request lifecycle. From onRouteRequest(...) to onError(...) to onFrameworkStop(...)
and more.


All extension points of ninja.Ninja
-----------------------------------

NinjaDefault is really nice, but you can also start from scratch and customize
everything yourself. The only thing to keep in mind is that your <code>conf.Ninja</code> class 
has to extend <code>ninja.Ninja</code> (That's the interface from the ninja framework).

<pre class="prettyprint">
package conf;

public class Ninja implements ninja.Ninja {

    /**
	 * When a route is requested this method is called.
	 */
	void onRouteRequest(Context.Impl context);
    
    /**
     * Should handle cases where an exception is thrown
     * when handling a route that let to an internal server error.
     * 
     * Should lead to a html error 500 - internal sever error
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    void onError(Context context, Exception exception);
    
    /**
     * Should handle cases where the client sent strange date that
     * led to an error.
     * 
     * Should lead to a html error 400 - bad request
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    void onBadRequest(Context context, Exception exception);
    
    /**
     * Should handle cases where no route can be found for a given request.
     * 
     * Should lead to a html error 404 - not found
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    void onNotFound(Context context);

    /**
     * Invoked when the framework starts. Usually inits stuff like the scheduler
     * and so on.
     */
    void onFrameworkStart();

    /**
     * Invoked when the server hosting Ninja is being stopped. Usually
     * shuts down the guice injector and stopps all services.
     */
    void onFrameworkShutdown();

}
</pre>

<code>ninja.Ninja</code> nicely shows all options you have to tweak the
request lifecycle. It does not really matter if you implement <code>ninja.Ninja</code> yourself
or if you simply extend <code>ninja.NinjaDefault</code>. 
But in general extending <code>ninja.NinjaDefault</code> is a very good starting point.