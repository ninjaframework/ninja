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
 
 
 
 
 