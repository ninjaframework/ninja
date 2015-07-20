Filtering
=========

Introduction
------------

Powerful powerful powerful. This is what filters are.

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


Defining filters in Routes.java
-------------------------------

You can also define filters in the central route file, you can simply add then in
the route definition:

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
        
        // a GET request to "index" will be handled by a class called
        // "AppController" its method "index".
        router.GET().route("/index").with(AppController.class, "index").filteredWith({ SecureFilter.class });

        ...

    }
}
</pre>

In that case, the filter will be executed before any filter defined in the controller's class or method.


Global filters
--------------

But much better, if you want to have a filter for all routes, you can simply
define it as a global filter:

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
    
        router.addGlobalFilters({ SecureFilter.class });
        
        // a GET request to "index" will be handled by a class called
        // "AppController" its method "index".
        router.GET().route("/index").with(AppController.class, "index");

        ...

    }
}
</pre>


BasicAuthFilter
---------------

Ninja ships with an implementation of HTTP Basic Authentication.  You need to bind a 
`UsernamePasswordValidator` in your `conf.Modules` class and then annotate your secured
controllers or controller methods with `@FilterWith(BasicAuthFilter)`.

<pre class="prettyprint">
public class Module extends AbstractModule {

    @Override
    protected void configure() {

        // bind a UsernamePasswordValidator
        bind(UsernamePasswordValidator.class).toInstance(new UsernamePasswordValidator() {

            @Override
            public boolean validateCredentials(String username, String password) {
                return "user".equals(username) && "password".equals(password);
            }
        });
    }
}

public MyController {

    @FilterWith(BasicAuthFilter.class)
    public Result secureMethod(Context context) {
        // do something
    }
    
}    
</pre>


VersionCacheFilter
------------------

By default, Ninja's default HttpCacheToolkit allows caching by the browser of all
resources like assets, depending on cache settings (see [Static assets](static_assets.html)).

The problem for applications you deploy frequently (like in "devops"), is that
new javascript and css files will not be visible to the browser until the cache timeout
is reached.

To resolve this, the solution is to add into the url a code called "version", different
at each application startup, for all resources we allow to cache.

To use this filter you need to declare it as a global filter:

<pre class="prettyprint">
public class Routes implements ApplicationRoutes {

    @Override
    public void init(Router router) {
    
        router.addGlobalFilters({ VersionCacheFilter.class });
        
        // a GET request to "index" will be handled by a class called
        // "AppController" its method "index".
        router.GET().route("/index").with(AppController.class, "index");

        ...

    }
}
</pre>

then add the version in your controllers:

<pre class="prettyprint">
package controllers;

@Singleton
public class AppController {

    @Inject
    VersionCacheFilter versionCacheFilter;

    public Result index() {
    
        String version = versionCacheFilter.getVersion();
        return Results.html().render("version", version);
        
    }

}
</pre>

and finally use it in your templates, at any place in the urls, like this:

<pre class="prettyprint">
&lt;link href=&quot;/${contextPath}/${version}/${assetsAt(“css/custom.css”)}&quot; rel=&quot;stylesheet&quot;&gt;
</pre>
