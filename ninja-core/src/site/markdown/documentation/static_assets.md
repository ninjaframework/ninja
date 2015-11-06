Static assets
==============

Intro
-----

Ninja provides a special controller that helps to serve static assets: 
<code>AssetsController</code>. <code>AssetsController</code> 
serves static assets (like CSS, JavaScript and images) 
from the <code>src/main/java/assets</code> folder. 
It provides support for ETags and caching out of the box.

<div class="alert alert-info"><code>AssetsController</code> will serve all assets
below your src/main/java/assets folder. Therefore you have to make sure
that no sensible information is available in that folder and its subfolders.</div>

Serving static assets from a subdirectory
-----------------------------------------

You can enable the AssetsController via the following route:

<pre class="prettyprint">
router.GET().route("/assets/{fileName: .*}").with(AssetsController.class, "serveStatic");
</pre>

If a website references an image at <code>&lt;img src=&quot;/assets/images/logo.png&quot; /&gt;</code>
Ninja will serve the file at <code>src/main/java/assets/images/logo.png</code>. (Usually this
happens inside a jar file - but for clarity we used <code>src/main/java</code>).

Serving static files from root
------------------------------

You can also serve specific files from your assets directory. A usecase is robots.txt that
should be available at <code>myserver.com/robots.txt</code>.

To that end you can use the following route:

<pre class="prettyprint">
router.GET().route("/robots.txt").with(AssetsController.class, "serveStatic");
</pre>

This route will serve your static robots.txt from <code>src/main/java/assets/robots.txt</code>.


WebJars
-------

The WebJars project (http://www.webjars.org/) started by James Ward is 
an excellent initiative that unites good old Java dependency management 
with static web libraries like Bootstrap.

That means that you can for instance include Bootstrap into your project via:

<pre class="prettyprint">
&lt;dependency&gt;
    &lt;groupId&gt;org.webjars&lt;/groupId&gt;
    &lt;artifactId&gt;bootstrap&lt;/artifactId&gt;
    &lt;version&gt;2.1.1&lt;/version&gt;
&lt;/dependency&gt;
</pre>

The dependency is of course transitive, and will also pull in jQuery (needed by Bootstrap). 
That way copying of dependencies into your assets folder is no longer needed.

In order to activate support for WebJars you need to add the following route to
your project:

<pre class="prettyprint">
router.GET().route("/webjars/{fileName: .*}").with(AssetsController.class, "serveWebJars");
</pre>

You can reference Bootstrap from your HTML pages via the following URL:

<pre class="prettyprint">
&lt;link href=&quot;/webjars/bootstrap/3.3.4/css/bootstrap.min.css&quot; rel=&quot;stylesheet&quot;&gt;
</pre>

And Bootstrap is only an example. There are a lot more WebJars available at your
disposal: jQuery, Ember.js, AngularJS and much more. 
And everything without the need for downloading and updating stuff inside 
your assets directory.

<div class="alert alert-info">
Actually WebJars do nothing magic. 
It simply uses a Java Servlet 3.x convention that allows to reference
and arbitrary static resources of a libraries' META-INF/resources folder in your 
application.
</div>
 
 
 
 
Caching
-------

Static assets do not change frequently. Therefore Ninja supports two browser based caching facilities:
 
 * ETags
 * Cache-Control 
 
Ninja handles caching automatically. It will just work. Well. To make developing easy caching is disabled
by default in test and dev mode.


Caching settings
----------------

You can control caching via two parameters in your application.conf file:
 
 * <code>http.useETag</code> (true by default)
 * <code>http.cache_control</code> (3600 by default)
 
<code>http.useEtag</code> will let you turn on and off ETag based caching of assets. 
<code>http.cache_control</code> will set the maxAge=XXX cache-control header.
