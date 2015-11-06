Ninja standalone
================

Introduction
------------

Ninja features a standalone mode where you do not need a separate servlet container.
The standalone mode enables you to package your application along with 
a fast, embedded HTTP server (Jetty in that case).  There are several options for
packaging your application using standalone mode.

Option 1 - Uber/Fat Jar using Assembly Maven Plugin
---------------------------------------------------

This allows you to drop that self-executing file to your server run your application 
with zero external dependencies. 
Due to Ninja's client-side sessions it becomes really simple to have one 
reverse proxy in front of many Ninja instances.

In order to do so you have to first of all add ninja-standalone as dependency to your pom.xml:

<pre class="prettyprint">
&lt;dependency&gt;
    &lt;groupId&gt;org.ninjaframework&lt;/groupId&gt;
    &lt;artifactId&gt;ninja-standalone&lt;/artifactId&gt;
    &lt;version&gt;X.X.X&lt;/version&gt;
&lt;/dependency&gt;
</pre>

Don't forget to change packaging value from _war_ to _jar_.

<pre class="prettyprint">
&lt;packaging&gt;jar&lt;/packaging&gt;
</pre>

Packaging the application is done via the maven shade plugin. Add the following snipplet
to your pom.xml.

<pre class="prettyprint">
&lt;plugin&gt;
  &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
  &lt;artifactId&gt;maven-shade-plugin&lt;/artifactId&gt;
  &lt;version&gt;2.2&lt;/version&gt;
  &lt;configuration&gt;
    &lt;createDependencyReducedPom&gt;true&lt;/createDependencyReducedPom&gt;
    &lt;filters&gt;
      &lt;filter&gt;
        &lt;artifact&gt;*:*&lt;/artifact&gt;
        &lt;excludes&gt;
          &lt;exclude&gt;META-INF/*.SF&lt;/exclude&gt;
          &lt;exclude&gt;META-INF/*.DSA&lt;/exclude&gt;
          &lt;exclude&gt;META-INF/*.RSA&lt;/exclude&gt;
        &lt;/excludes&gt;
      &lt;/filter&gt;
    &lt;/filters&gt;
  &lt;/configuration&gt;
  &lt;executions&gt;
    &lt;execution&gt;
      &lt;phase&gt;package&lt;/phase&gt;
      &lt;goals&gt;
        &lt;goal&gt;shade&lt;/goal&gt;
      &lt;/goals&gt;
      &lt;configuration&gt;
        &lt;transformers&gt;
          &lt;transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/&gt;
          &lt;transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer"&gt;
            &lt;mainClass&gt;ninja.standalone.NinjaJetty&lt;/mainClass&gt;
          &lt;/transformer&gt;
        &lt;/transformers&gt;
      &lt;/configuration&gt;
    &lt;/execution&gt;
  &lt;/executions&gt;
&lt;/plugin&gt;
</pre>

Whenever you build your application on the command line this will generate a fat jar
inside your target directory. Usually that file is named roughly MY-APPLICATION-jar-with-dependencies.jar

You can generate the fat jar by calling

<pre class="prettyprint">
mvn clean compile package
</pre>

Running the fat jar (and your app) is as simple as calling:

<pre lass="prettyprint">
java -Dninja.port=9000 -jar MY-APPLICATION-jar-with-dependencies.jar
</pre>

Option 2 - Packaged using Stork Maven Plugin
--------------------------------------------

If you'd like to run your application as a proper daemon on Unix/Mac or as a service
on Windows, another option is using the [Stork Maven Plugin](https://github.com/fizzed/java-stork).

Stork is a collection of utilities for optimizing your "after-build" workflow by
filling in the gap between Maven and eventual app execution. In just a couple
quick steps, stork will package your Ninja app into a professional .tar.gz
file ready for deployment.

Here is an [article describing how Ninja apps can be packaged with Stork](http://fizzed.com/blog/2015/01/using-stork-deploy-production-ninja-framework-app).

Here is a [demo application that integrates with Stork](https://github.com/fizzed/java-ninja-stork-demo).

Command-line / system property configuration
--------------------------------------------

Regardless of which option you choose, you can customize your application startup
via the following command line parameters:

 * <code>ninja.port</code> allows you to select the port on which your 
   application is starting (8080 by default).
 * <code>ninja.host</code> allows you to select the host on which your 
   application will bind (any/0.0.0.0 by default).
 * <code>ninja.idle.timeout</code> allows you to specify the timeout period after which 
   idle HTTP connections will be closed. (in msec, 30000 by default)
 * <code>ninja.mode</code> allows you to select the mode (test, dev, prod) 
   of your application (prod by default).
 * <code>ninja.external.configuration</code> allows you to add an external application
   configuration (eg -Dninja.external.configuration=conf/production.conf).
   Please note that even with an external configuration, if you included a
   <code>conf/application.conf</code> on your classpath (e.g. in your app jar)
   then it will be loaded first, followed by the external configuration file.
   By using both files, you'll only need to worry about overriding values --
   which comes in handy when running in test/production environments.
 * <code>ninja.context</code> allows you to add a context prefix to your application
   (eg -Dninja.context=/your_context_path).
 * <code>ninja.jetty.configuration</code> allows you to supply a comma-delimited
   list of <code>jetty.xml</code> configuration files to configure the server.
   Anything Jetty can do is now available. Please note that if this property is
   supplied then the host/port property is skipped and you'll need to configure
   that in the <code>jetty.xml</code> configuration file.
   (eg -Dninja.jetty.configuration=jetty.xml,jetty-ssl.xml) Please refer to 
   Jetty distribution for example config files.  For each jetty configuration file
   the local filesystem is searched first, followed by the classpath, and if
   multiple files are included then each one is applied in order.


Init.d script to run Ninja as standalone service on Linux
---------------------------------------------------------

If you use option #1 and want to register your Ninja standalone application on
your Linux box (Debian, Ubuntu) you can use and adapt the following script. The
script should be copied at /etc/init.d/ninja and can be run via <code>service ninja start</code>.

<pre class="prettyprint">
#!/bin/bash
# chkconfig: 345 20 80
# description: Ninja start/shutdown script
# processname: java
#
# Installation:
# copy file to /etc/init.d
# chmod +x /etc/init.d/ninja

# chkconfig --add /etc/init.d/ninja
# chkconfig ninja on
#     OR on a Debian system
# sudo update-rc.d ninja defaults

#
# Usage: (as root)
# service ninja start
# service ninja stop
# service ninja status
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

# Path to the application
APPLICATION_NAME=my-ninja-service
APPLICATION_PATH=/srv/ninja_applications/${APPLICATION_NAME}
APPLICATION_JAR=my-ninja-service-1.0-SNAPSHOT-jar-with-dependencies.jar

PID_FILE=/var/run/${APPLICATION_NAME}.pid
PORT=9013

# Path to the JVM
JAVA_BIN=/opt/java-oracle/jdk1.7.0/bin/java
PARAMS="-Dninja.port=${PORT} -jar ${APPLICATION_PATH}/${APPLICATION_JAR}"

# User running the Ninja process
USER=ninja

RETVAL=0

start() {
    if [ -f ${PID_FILE} ]; then
        echo "Ninja application ${APPLICATION_NAME} already running"
    else
        DAEMON_START_LINE="start-stop-daemon --chdir=${APPLICATION_PATH} --make-pidfile --pidfile ${PID_FILE} --chuid ${USER} --exec ${JAVA_BIN} --background --start -- ${PARAMS}"
        ${DAEMON_START_LINE}
        RETVAL=$?
        echo -n "Starting Ninja Application: ${APPLICATION_NAME}... "


            if [ $RETVAL -eq 0 ]; then
                echo " - Success"
            else
                echo " - Failure"
            fi
        echo
    fi
    echo

}
stop() {
    kill -9 `cat ${PID_FILE}`
    RETVAL=$?
    rm -rf ${PID_FILE}
    echo -n "Stopping Ninja application: ${APPLICATION_NAME}"

    if [ $RETVAL -eq 0 ]; then
        echo " - Success"
    else
        echo " - Failure"
    fi
        echo
    }

status() {
    if [ -f ${PID_FILE} ]; then
        echo "Ninja application ${APPLICATION_NAME} running"
    else
        echo "Ninja application ${APPLICATION_NAME} not running"
    fi
    echo

}

clean() {
        rm -f ${PID_FILE}
}

case "$1" in
    start)
    start
    ;;
    stop)
    stop
    ;;
    restart|reload)
    stop
    sleep 10
    start
    ;;
    status)
    status
    ;;
    clean)
    clean
    ;;
*)
echo "Usage: $0 {start|stop|restart|status}"
esac
</pre>