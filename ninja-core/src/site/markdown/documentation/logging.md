Logging
=======

Ninja uses the Logback via slf4j as logging library. 

Configuration of Logback inside Ninja
-------------------------------------

Usually you want to use different logging settings when running in test, dev 
or on production.

There are three main ways how you can configure Logback.

### #1 Using logback.xml

By default Logback will look for a file called logback.xml in the root of
your application. If it finds one it will intialize the logging system
accordingly. This approach is okay for simple setups.

### #2 Using Java system property to specify link to logback.xml

Logback evaluates a Java system property named <code>logback.configurationFile</code>.
This approach is handy if you are using Ninja's standalone mode:

<pre class="prettyprint">
java -Dlogback.configurationFile=/srv/conf/logback.xml -jar ninja-application.jar
</pre>

This allows you to use one logging configuration for all your instances.
More about that approach here: http://logback.qos.ch/manual/configuration.html


### #3 Using application.conf to switch configurations

If you cannot or do not want to use external Java system properties you can
use application.conf and the key <code>logback.configurationFile</code> to specify
the location of the logging file. Ninja will pick up the file and configure
Logback accordingly.

<pre class="prettyprint">
# An example for application.conf based configuration of logback
%prod.logback.configurationFile=logback_prod.xml   # will be used in production
%dev.logback.configurationFile=logback_dev.xml     # will be used in dev mode
</pre>

Ninja will look for specified files in three places in the following order:

 * On the classpath
 * On the filesystem
 * and if the "file" is a http url it will try to load the configuration from there.

<div class="alert alert-info">
Using the Java system property <code>-Dlogback.configurationFile</code> to configure
logging will always override all settings in application.conf.
</div>

Quick intro to Logback
----------------------

Ninja provides you with a simple foundation to use Logback. 
And this is perfect, because logback has everything you need to configure
logging - even for the largest systems you can image.

The best way to configure Logback is to follow the excellent guide 
at: http://logback.qos.ch/manual/configuration.html

But just in case you were wondering how such a logback.xml file can look like:

<pre class="prettyprint">
&lt;configuration&gt;

  &lt;appender name=&quot;FILE&quot; class=&quot;ch.qos.logback.core.FileAppender&quot;&gt;
    &lt;file&gt;myApp.log&lt;/file&gt;

    &lt;encoder&gt;
      &lt;pattern&gt;%date %level [%thread] %logger{10} [%file:%line] %msg%n&lt;/pattern&gt;
    &lt;/encoder&gt;
  &lt;/appender&gt;

  &lt;appender name=&quot;STDOUT&quot; class=&quot;ch.qos.logback.core.ConsoleAppender&quot;&gt;
    &lt;encoder&gt;
      &lt;pattern&gt;%msg%n&lt;/pattern&gt;
    &lt;/encoder&gt;
  &lt;/appender&gt;

  &lt;root level=&quot;debug&quot;&gt;
    &lt;appender-ref ref=&quot;FILE&quot; /&gt;
    &lt;appender-ref ref=&quot;STDOUT&quot; /&gt;
  &lt;/root&gt;
&lt;/configuration&gt;
</pre>

By the way - you can also write logback configuration files in groovy. 

