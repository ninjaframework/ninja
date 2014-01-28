Ninja standalone
================

Introduction
------------

Ninja features a standalone mode where you do not need a separate servlet container. 
The standalone mode enables you to run a single maven command and get a self 
executing jar file that contains your application, Ninja and a fast server 
(Jetty in that case).

This allows to drop that self-executing file to your server run your application 
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

Packaging the application is done via the maven assembly plugin. Add the following snipplet
to your pom.xml.

<pre class="prettyprint">
&lt;plugin&gt;
    &lt;artifactId&gt;maven-assembly-plugin&lt;/artifactId&gt;
    &lt;version&gt;2.4&lt;/version&gt;
    &lt;configuration&gt;
        &lt;descriptorRefs&gt;
            &lt;descriptorRef&gt;jar-with-dependencies&lt;/descriptorRef&gt;
        &lt;/descriptorRefs&gt;
        &lt;archive&gt;
            &lt;manifest&gt;
                &lt;mainClass&gt;ninja.standalone.NinjaJetty&lt;/mainClass&gt;
            &lt;/manifest&gt;
        &lt;/archive&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;
</pre>

Whenever you build your application on the command line this will generate a fat jar
inside your target directory. Usually that file is named roughly MY-APPLICATION-jar-with-dependencies.jar

If you skip the section called "executions" in the maven-assembly-plugin you can generate
the fat jar by calling

<pre class="prettyprint">
mvn clean compile assembly:single
</pre>

Running the fat jar (and your app) is as simple as calling:

<pre lass="prettyprint">
java -Dninja.port=9000 -jar MY-APPLICATION-jar-with-dependencies.jar
</pre>

You can customize your application startup via the following command line parameters:

 * <code>ninja.port</code> allows you to select the port on which your 
   application is starting (8080 by default). 
 * <code>ninja.mode</code> allows you to select the mode (test, dev, prod) 
   of your application (prod by default).
 * <code>ninja.external.configuration</code> allows you to add an external application
   configuration (eg -Dninja.external.configuration=conf/production.conf).
 * <code>ninja.context</code> allows you to add a context prefix to your application
   (eg -Dninja.context=/your_context_path).


Init.d script to run Ninja as standalone service on Linux
---------------------------------------------------------

If you want to register your Ninja standalone application on your Linux box (Debian, Ubuntu) you can use
and adapt the following script. The script should be copied at /etc/init.d/ninja and can be run
via <code>service ninja start</code>.

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