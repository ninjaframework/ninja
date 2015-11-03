Custom routing
==============

In many projects Ninja's default routing is just fine. As well as the way
Ninja handles and reacts to errors and exceptions.

But sometimes you need just more flexibility and control. Let's say you want
to add extensive reporting to your application. In effect you want to report
and log all requests, all errors - everything that's going on inside your application.

You can try to tackle the issue by using a Filter. But there's a more powerful
way.

Center stage for <code>conf.Ninja</code>.

Simple and straightforward
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
has to extend <code>ninja.Ninja</code> (That's an interface within the Ninja framework).

<pre class="prettyprint">
package ninja;

public interface Ninja {

	/**
	 * When a route is requested this method is called.
	 */
	void onRouteRequest(Context.Impl context);
    
    /**
     * This result should be used when an error occurs.
     * 
     * @param context The context for this request
     * @param exception The exception to handle. Can be used to customize error message.
     * @return a result you can use to render the error.
     */
    Result onException(Context context, Exception exception);
    
    /**
     * Should handle cases where an exception is thrown
     * when handling a route that let to an internal server error.
     * 
     * Should lead to a html error 500 - internal sever error
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    Result getInternalServerErrorResult(Context context, Exception exception);
    
    /**
     * Should handle cases where the client sent strange date that
     * led to an error.
     * 
     * Should lead to a html error 400 - bad request
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    Result getBadRequestResult(Context context, Exception exception);
    
    /**
     * Should handle cases where no route can be found for a given request.
     * 
     * Should lead to a html error 404 - not found
     * (and be used with the same mindset).
     * 
     * Usually used by onRouteRequest(...).
     */
    Result getNotFoundResult(Context context);
    
    /**
     * Should handle cases where access is forbidden
     * 
     * Should lead to a html error 403 - not found
     * (and be used with the same mindset).
     * 
     * Usually used by SecureFilter for instance(...).
     */
    Result getForbiddenResult(Context context);

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
    
    /**
     * Should be used to render an error. Any errors should be catched
     * and not reported in any way to the request.
     * 
     * For instance if your application catches a sever internal computation
     * error use this method and its implementations to render out
     * an error html page.
     */
    void renderErrorResultAndCatchAndLogExceptions(Result result, Context context);

}
</pre>

<code>ninja.Ninja</code> nicely shows all the options you can tweak regarding the 
request lifecycle. It does not really matter if you implement <code>ninja.Ninja</code> yourself
or if you simply extend <code>ninja.NinjaDefault</code>. 
But in general extending <code>ninja.NinjaDefault</code> is a very good starting point.