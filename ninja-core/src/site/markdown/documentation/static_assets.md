Static assets
==============

Intro
-----

By default you can put static assets into the <code>src/main/java/assets</code> folder. Usually you want to put css files, js files, images and so on into that folder.

You can then reference those files in your html templates via (for instance) 
    
    <img src="assets/myimage.png" />

Please note that "assets" is a convention you should follow.


Caching
-------

Static assets do not change frequently. Therefore Ninja supports two browser based caching facilities:
 
 * ETags
 * Cache-Control 
 
Ninja handles caching automatically. It will just work. Well. To make developing easy caching is disabled
by default in test and dev mode.


Settings
--------

You can control caching via two parameters in your application.conf file:
 
 * <code>http.useETag</code> (true by default)
 * <code>http.cache_control</code> (3600 by default)
 
<code>http.useEtag</code> will let you turn on and off etag based caching of assets. 
<code>http.cache_control</code> will set the maxAge=XXX cache-control header.


Webjars
-------

The webjars project (http://www.webjars.org/) started by James Ward is an excellent initiative that
unites good old Java dependency management with web libraries like bootstrap.

That means that you can for instance include bootstrap into your project via:

    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>bootstrap</artifactId>
        <version>2.1.1</version>
    </dependency>

The dependency is of course transitive, and will also pull in jQuery (needed by bootstrap). No more
copying of dependencies into your assets folder needed.

You can then reference this library in your templates then by including 

    <link href="assets/webjars/bootstrap/2.1.1/css/bootstrap.min.css" rel="stylesheet">
    
The important part is the "webjars" part after the assets subdirectory. But webjars provides
a lot more - from Angular to Ember to jQuery. Simply check out their website.

<b>Advanced</b> Actually webjars does nothing magic. It simply uses a Java Servlet 3.x convention that allows to reference
and arbitrary static resources of a libraries' META-INF/resources folder in your application. Ninja
fully supports that and makes the content of your META-INF/resources available under the assets subdirectory.
 
 
 
 
 