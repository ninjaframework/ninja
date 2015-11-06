Using the cache
===============


Introduction
------------

Ninja supports Memcached (http://www.danga.com/memcached/) as primary caching layer. 
In standalone mode Ninja uses EhCache (http://ehcache.org/) as default implementation.

In production on multiple machines always use Memcached. On a single machine and when developing 
your application it is fine to use EhCache. 
If you are running EhCache on a cluster with multiple machines - your machines will have a state that 
is not compatible with the way Ninja works.

<div class="alert alert-info">
Using a cache is one of the best ways to improve the performance of your application.
But always keep in mind that a cache can fail at any point in time. Design
your application accordingly!
</div>


Using NinjaCache
--------------------------

Using Ninja's caching facilities is straightforward. You inject NinjaCache into the classes that
want to use caching and you are ready to go.

<pre class="prettyprint">
@Inject 
NinjaCache ninjaCache;

public Result allPosts() {

    List&lt;Post&gt; posts = ninjaCache.get(&quot;posts&quot;, List.class);
    if(products == null) {
        posts = postDao.findAll();
        ninjaCache.set(&quot;posts&quot;, posts, &quot;30mn&quot;);
    }

    return Results.html().render(posts);

}
</pre>

NinjaCache provides a range of methods to store, retrieve and manipulate data in your cache.

Some methods have a duplicate "safe" method (
eg. <code>ninjaCache.safeDelete(...)</code> vs. <code>ninjaCache.delete(...)</code>). 
The difference is that the methods prefixed with "safe" issue a blocking call 
waiting for the call to be successful, 
while the unprefixed methods issue a fire-and-forget call that does not guarantee 
that call succeeds. You should prefer the non-safe methods as they do not block your application.


Configuring Memcached
---------------------

To use Memcached you have to add the following configuration variables to your application.conf file:

<pre class="prettyprint">
cache.implementation=ninja.cache.CacheMemcachedImpl

memcached.host=127.0.0.1:11211

// user and password are optional
memcached.user=USER          
memcached.password=PASSWORD        
</pre>

When you are using multiple Memcached instances you can specify them via:

<pre class="prettyprint">
memcached.host=127.0.0.1:11211 127.0.0.1:11212 127.0.0.1:11213
</pre>


As usual you can prefix your variables to use Memcached in production and EhCache while developing
and testing:

<pre class="prettyprint">
%prod.cache.implementation=ninja.cache.CacheMemcachedImpl
</pre>
