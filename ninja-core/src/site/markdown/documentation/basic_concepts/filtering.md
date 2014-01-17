Filtering
=========

Introduction
------------

Powerful powerful powerful. This is what filers are.

Let's say you want to implement authorization in your application. Only users
authorized should be able to access a certain route and controller. Otherwise
they should get an error page.

First step is to annotate our controller method with a <code>@FilterWith</code> 
annotation:

<pre class="prettyprint">
@FilterWith(SecureFilter.class)  // Only let authorized users execute the controller method
public Result secureIndex() {    
    /// do something
}    
</pre>

Ninja will execute the filter before the method <code>secureIndex(...)</code>will be called.

The <code>SecureFilter.class</code> itself looks like:

<pre class="prettyprint">
public class SecureFilter implements Filter {

    /** If a username is saved we assume the session is valid */
    public final String USERNAME = "username";

    @Override
    public Result filter(FilterChain chain, Context context) {

        // if we got no cookies we break:
        if (context.getSession() == null
                || context.getSession().get(USERNAME) == null) {


            return Results.forbidden().html().template("/views/forbidden403.ftl.html");

        } else {
            return chain.next(context);
        }

    }
}
</pre>


<code>SecureFilter</code> looks into the session and tries to get a 
variable called "username". If it can do so
we can assume the user has been authenticated by us. Please refer to the sessions
section for more information why that is the case).
The filter then simply calls the next filer in the chain.

<pre class="prettyprint">
return chain.next(context);
</pre>

However - if the variable *username* is not there we will 
immediately break the filter chain and will never
call the method itself. It instead will return a forbidden 
status code and render a forbidden view.

<pre class="prettyprint">
return Results.forbidden().html().template("/views/forbidden403.ftl.html");
</pre>


Chaining filters
----------------

Filters are really powerful. And you can use more than one filter as a chain. 
Consider the following method:

<pre class="prettyprint">
@FilterWith({
    LoggerFilter.class, 
    TeaPotFilter.class})
public Result teapot(Context context) {
    // do something
}    
</pre>

This method will first call the LoggerFilter and then the 
TeaPotFilter. Each of the individual methods can
break the chain or even alter the result and the context.

@FilterWith - class level and inheritance
-----------------------------------------

You can put <code>@FilterWith</code> annotations on method level, but also on class level. 
If you use <code>@FilterWith</code> on class level all methods of this class will
be filtered. 

Sometimes it is also useful to have a <code>BaseController</code> that is annotated with 
<code>@FilterWith</code>. Other controllers extending <code>BaseContoller</code> will automatically
inherit <code>@FilterWith</code>.