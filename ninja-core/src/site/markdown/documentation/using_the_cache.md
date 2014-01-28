Using the cache
===============


Introduction
------------

Ninja supports Memcached (http://www.danga.com/memcached/) as primary caching layer. 
In standalone mode Ninja uses EhCache (http://ehcache.org/) as default implementation.

In production on multiple machines always use Memcached. On a single machine and when developing 
your application it is fine using EhCache. 
If you are running EhCache on a cluster with multiple machines - your machines will have a state what 
is not compatible with the way Ninja works.

<div class="alert alert-info">
Using a cache is one of the best ways to improve the performance of your application.
But always keep in mind that a cache can fail at any point in time. Design
your application accordingly!
</div>


Usage of Ninja's Cache Api
--------------------------

Using Ninja's caching facilities is straight forward. You inject NinjaCache into the classes that
want to use caching and you are ready to go.

<pre class="prettyprint">
@Inject 
NinjaCache ninjaCache;

public Result allPosts() {

    List<Post> posts = ninjaCache.get("posts", List.class);
    if(products == null) {
        posts = postDao.findAll();
        ninjaCache.set("posts", posts, "30mn");
    }

    return Results.html().render(posts);

}
</pre>

NinjaCache provides a range of methods to store, retrieve and manipulate data in your cache.

Some methods have a duplicate "safe" method (
eg. <code>ninjaCache.safeDelete(...)</code> vs. <code>ninjaCache.delete(...)</code>). 
The difference is that methods prefixed with "safe" issue a blocking call 
waiting for the call to be successful, 
while note prefixed methods issue a fire and forget call that does not guarantee 
that call succeeds.  
You should prefer non-safe methods as they do not block your application.


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

When you are using multiple memcached instances you can specify them via:

<pre class="prettyprint">
memcached.host=127.0.0.1:11211 127.0.0.1:11212 127.0.0.1:11213
</pre>


As usual you can prefix your variables to use memcached in production and ehcache while developing
and testing:

<pre class="prettyprint">
%prod.cache.implementation=ninja.cache.CacheMemcachedImpl
</pre>
