Configure your application
==========================

Introduction to application.conf
--------------------------------

By convention Ninja will look for a configuration file at "conf/application.conf".

application.conf is a simple properties file with keys and values. "#" is a comment.

<pre class="prettyprint">
    # an arbitrary content
    application.name=Ninja demo application
    application.cookie.prefix=NINJA
</pre>

Like any other Ninja file it is encoded in Utf-8. Make sure your editor reads and
saves the file in the correct encoding. 

Ninja properties can do much more than traditional Java properties. For instance
it is possible to reference other properties by their name and combine them.

<pre class="prettyprint">
    serverName=myserver.com
    serverPort=80
    fullServerName=${serverName}:${serverPort}
</pre>
    
fullServerName references the previously set values of the keys and will return myserver.com:80.

Inside your application there are two ways to get the properties. 

First way is to use NinjaProperties.get(...). You have to inject NinjaProperties first, then you
can use all sorts of functions to retrieve properties.

<pre class="prettyprint">
    @Inject 
    NinjaProperties ninjaProperties;
    
    public void myMethod() {
        String value = ninjaProperties.get("fullServerName")
        ... do even more...
    }
</pre>

The second way to get properties is to inject them via @Named("fullServerName").

<pre class="prettyprint">
    /** An arbitrary constructor */
    @Inject
    ApplicataionController(@Named("fullServerName") String fullServerName) {
        ... do something ...
    }
</pre>

The properties are read and managed by the excellent Apache Configurations library. Please
refer to [their manual](http://commons.apache.org/configuration/) for even more information on advanced usage.


Configuring the modes
---------------------
Ninja uses three predefined modes (test, dev and prod as defined in NinjaConstant.java). You can set the
modes by setting a Java system property called "ninja.mode".

<pre class="prettyprint">
    On the command line:
    > java -Dninja.mode=dev
    
    Or programmatically:
    System.setProperty(NinjaConstant.MODE_KEY, NinjaConstant.MODE_DEV)
</pre>

Just make sure that you set the system property early so that Ninja knows its mode from the very beginning.


This modes are handy if you want to define different properties for different environments.
You may want to use database1 when running tests and database2 when developing.

Ninja supports that use case by using a mode prefix prior to your property key.

<pre class="prettyprint">
    database.name=database_production   # will be used when no mode is set (or prod)
    %dev.database.name=database_test    # will be used when running in dev mode
    %test.database.name=database_test   # will be used when running in test mode
</pre>

The convention is to use a "%" and the name of the mode followed by ".".


External configuration for deployment
-------------------------------------

When running on a server you may want to use a completely different configuration.
This can be accomplished by setting a Java system property:

<pre class="prettyprint">
    java -Dninja.external.configuration=conf/production.conf
</pre>

This tells Ninja to load conf/application.conf as usual - but also load conf/production.conf.
Keys configured in production.conf will overwrite any keys present in application.conf.

That way you can manage a production configuration separately from
your project. For instance - you may want to do this when your server secret should only
be available to a certain set of people and not the world. 

Just make sure file "conf/production.conf" is on the classpath and in subdirectory conf. Ninja currently does
not read the file from the file system. It is loaded solely from the classpath to
circumvent any security issues with your container.


