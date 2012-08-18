Filter
======

Powerful powerful powerful. This is what filers are.

Let's consider a simple example. Let's say you want to implement authentication in your application.

First step is to annotate our method with a <code>@FilterWith</code> annotation:


<pre class="prettyprint">
    @FilterWith(SecureFilter.class)
    public Result secureIndex() {    
        /// do something
    }    
</pre>

Ninja will then execute the filter before the method *secureIndex* will be called.

The SecureFilter.class class itself looks like:

<pre class="prettyprint">
    public class SecureFilter implements Filter {

        /** If a username is saved we assume the session is valid */
        public final String USERNAME = "username";

        @Override
        public Result filter(FilterChain chain, Context context) {

            // if we got no cookies we break:
            if (context.getSessionCookie() == null
                    || context.getSessionCookie().get(USERNAME) == null) {
            
            
                return Results.forbidden().html().template("/views/forbidden403.ftl.html");

            } else {
                return chain.next(context);
            }

        }
    }
</pre>


The securefilter looks into the session and tries to get a variable called "username". If it can do so
we can assume the user has been authenticated by us. The filter then simply calls the next filer in the chain (yes
there can be more than one filter - see below).

<pre class="prettyprint">
    return chain.next(context);
</pre>

However - if the variable *username* is not there we will immediatley break the filter chain and will never
call the method itself. It instead will return a forbidden status code and render a forbidden view.

<pre class="prettyprint">
    return Results.forbidden().html().template("/views/forbidden403.ftl.html");
</pre>


Chaining filters
----------------

Filters are really powerful. And you can use more than one filter as a chain. Consider the following method:

<pre class="prettyprint">
    @FilterWith({
        LoggerFilter.class, 
        TeaPotFilter.class})
    public Result teapot(Context context) {
        // do something
    }    
</pre>

This method will first call the LoggerFilter and then the TeaPotFilter. Each of the individual methods can
break the chain or even alter the result and the context.

