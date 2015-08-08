Configuration and modes
=======================

Configuration
-------------

By convention Ninja will look for a configuration file at <code>conf/application.conf</code>.

<code>application.conf</code> is a simple properties file with keys and values. "#" is a comment.

<pre class="prettyprint">
# an arbitrary comment
application.name=Ninja demo application
application.cookie.prefix=NINJA
</pre>

Like any other Ninja file it is encoded in UTF-8. Make sure your editor reads and
saves the file in the correct encoding. 

Ninja properties can do much more than traditional Java properties. For instance
it is possible to reference other properties by their name and combine them.

<pre class="prettyprint">
serverName=myserver.com
serverPort=80
fullServerName=${serverName}:${serverPort}
</pre>
    
fullServerName references the previously set values of the keys and will return myserver.com:80.

Inside your application there are two basic ways to access the properties. 

First way is to use <code>NinjaProperties.get(...)</code>. You have to inject NinjaProperties first, then you
can use all sorts of methods to retrieve properties.

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
ApplicationController(@Named("fullServerName") String fullServerName) {
    ... do something ...
}
</pre>

By the way. And this is really important: Properties are read and managed by the excellent Apache Configurations library. Please
refer to [their manual](http://commons.apache.org/configuration/) for even more information on advanced usage.


Configuring the modes
---------------------

Ninja uses three predefined modes (<code>test</code>, <code>dev</code> 
and <code>prod</code> as defined in <code>NinjaConstant.java</code>). 
If no mode is set explicitly Ninja will use the <code>prod</code> mode. That means if you 
deploy Ninja via a war file to an arbitrary servlet container Ninja will use the prod
mode.

Ninja's SuperDevMode will set the mode to <code>dev</code>. And Ninja's
testcases will use mode <code>test</code> by default.

You can set the
modes by setting the Java system property <code>ninja.mode</code>.

<pre class="prettyprint">
On the command line:
> java -Dninja.mode=dev

Or programmatically:
System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_DEV)
</pre>

Make sure that you set the system property early so that Ninja 
knows its mode from the very beginning.


These modes are handy if you want to define different properties for different environments.
You may want to use database1 when running tests and database2 when developing and database3
when running in production.

Ninja supports that use case by using a mode prefix prior to your property key.

<pre class="prettyprint">
database.name=database_production   # will be used when no mode is set (or prod)
%prod.database.name=database_prod   # will be used when running in prod mode
%dev.database.name=database_dev     # will be used when running in dev mode
%test.database.name=database_test   # will be used when running in test mode
</pre>

The convention is to use a "%" and the name of the mode followed by ".".

Disabling diagnostic mode
-------------------------

As of version 4.0.7, Ninja features a new diagnostic extension to dev mode where
detailed diagnostic error pages are returned from <code>ninja.NinjaDefault</code>
rather than the typical Result which pulls a template from <code>system/views</code>.

For example, in versions prior to 4.0.7, if your controller class method threw
an <code>Exception</code> then the default behavior of <code>ninja.NinjaDefault</code>
was to catch it in the <code>onException</code> method and return a Result using
the template <code>views/system/500internalServerError.ftl.html</code>. The
default system views are basic and more intended for use in production than
during development.

As of version 4.0.7, the default behavior of all the methods in
<code>ninja.NinjaDefault</code> are to first check if dev mode is on and if 
<code>NinjaProperties.areDiagnosticsEnabled()</code> is true.  If both are true
then a <code>Result</code> is returned with the renderable set to a
<code>DiagnosticError</code> instance.  Since <code>DiagnosticError</code>
implements <code>Renderable</code> -- it knows how to render itself to the output
stream and it will bypass the <code>TemplateEngine</code>.

Diagnostic mode is disabled automatically in PROD/TEST modes, but it can also be
disabled in DEV mode. Simply add the following to your <code>application.conf</code>:

    application.diagnostics=false

Retaining diagnostic mode if you provide conf.Ninja
---------------------------------------------------

If you provide your own conf.Ninja and extend ninja.NinjaDefault, then you will
likely lose diagnostic mode for the particular methods you override.  You can
retain the feature, by calling the super method and conditionally returning
its result, rather than your own.

<pre class="prettyprint">
public class Ninja extends NinjaDefault {

    @Override
    public Result getInternalServerErrorResult(Context context, Exception exception) {
        if (isDiagnosticsEnabled()) {
            return super.getInternalServerErrorResult(context, exception);
        }

        // your impl only active in test/prod or dev (with diagnostic disabled)
    }

}
</pre>

Configuring application's base package
------------------------------------

If you'd like to keep all Java code in specific package you can define
<pre class="prettyprint">
application.modules.package=com.someorganinization.somepackage
</pre>

In this case you must put your Routes at<br>
<code>com.someorganization.somepackage.conf.Routes.java</code>

and Guice application configuration modules at<br>
<code>com.someorganization.somepackage.conf.Module.java</code>
<code>com.someorganization.somepackage.conf.ServletModule.java</code>
accordingly.


External configuration for deployment
-------------------------------------

When running on a server you may want to use a completely different configuration.
This can be accomplished by setting a Java system property:

<pre class="prettyprint">
java -Dninja.external.configuration=conf/production.conf
</pre>

This tells Ninja to load conf/production.conf. It will also load conf/application.conf as usual, 
but values in conf/production.conf will overwrite values in conf/application.conf.

That way you can manage a production configuration separately from
your project. You may want to do this for instance when your server secret should only
be available to a certain set of people and not the world. Or if your cloud hoster uses
a completely different configuration from prod, test or dev.

Ninja tries to load the file specified in <code>ninja.external.configuration</code> 
from several locations:

It tries to load in the following order:

* From a URL.
* From an absolute file path.
* From a relative file path.
* From the user's home dir.
* From the classpath.

Ninja uses the excellent Apache Configurations library to do the loading. Please refer to
[their manual](http://commons.apache.org/configuration/userguide/howto_filebased.html#Loading) for more information.

Hot-reloading external configuration
------------------------------------

By default Ninja does not reload your external configuration. However for some installations it may be very
useful to hot-reload this config instead of restarting your application.

    java -Dninja.external.reload=true -Dninja.external.configuration=conf/production.conf

This tells Ninja to reload your configuration if it is modified during runtime.