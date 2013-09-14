Deployment
==========

You just developed a great application - and now you want to host it somewhere.
Fortunately, Ninja is built on standard technologies. 

Therefore you got a myriad of great options for deployment.


War based deployment with Java application servers
--------------------------------------------------

When you run

    > mvn package

You get a <code>war</code> file in the subfolder /target of your project. That war file is compatible with almost all
war containers out there. Jetty and Tomcat are two of them, but you can also deploy to Glassfish and JBoss.

(Make sure you ran at least once "mvn jetty:run" which generates a unique secret in application.conf
that will be used to sign your application's sessions).

Just a quick example: With [Tomcat's hot deployment](http://www.mulesoft.com/tomcat-deploy) function 
you can simply take the war file produced by Ninja and drop it into the tomcat webapp folder.
Tomcat automatically detects the new file, stops the old application and starts the new one.

It can't get simpler. But better. The very same mechanism works nicely when Tomcat is running in a cluster
configuration. Simply drop the new war file into the webapp folder and that Tomcat instance will tell all
cluster Tomcat's about the file. Subsequently the cluster changes to that new version of your application.

You can easily automate that process by using the [maven cargo plugin](http://cargo.codehaus.org/Maven2+plugin). 
That makes it really easy to  get a simple Jenkins based continuous deployment up and running.

That's the power of following well proven standards. Ninja - and the apps you are building with Ninja - 
is standing on the shoulder of giants.


Standalone Ninja
----------------

Ninja also features a standalone mode where you do not a servlet containers. The standalone mode
enables you to run one command and get one self executing file that contains Ninja, your application
and a server (Jetty in that case) that will run your application.

This allows to simply drop that self-executing file to your server and automate that in a really simple fashion.
Due to Ninja's client-side sessions it becomes really simple to have one reverse proxy in front of
many Ninja instances.

In order to do so you have to first of all add ninja-standalone as dependecy to your pom.xml:

<pre>
    &lt;dependency&gt;
        &lt;groupId&gt;org.ninjaframework&lt;/groupId&gt;
        &lt;artifactId&gt;ninja-standalone&lt;/artifactId&gt;
        &lt;version&gt;X.X.X&lt;/version&gt;
    &lt;/dependency&gt;   
</pre>        

Packaging the applciation is done via the maven assembly plugin. Add the following snipplet
to your pom.xml.

<pre>
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

If you skip the section called "excutions" in the maven-assembly-plugin you can generate
the fat jar by calling

<pre>
    mvn clean compile assembly:single
</pre>

You can run the fat jar by calling java:

<pre>
    java    -Dninja.port=9000 
            -Dninja.mode=prod
            -jar MY-APPLICATION-jar-with-dependencies.jar
</pre>

Two parameters are important. First <code>ninja.mode</code> will allow you to set the mode of the Ninja
application you are running. Second <code>ninja.port</code> allows you to select the port on which your
application is starting.


Deployment on Google App Engine
-------------------------------

[Google's App Engine] (https://developers.google.com/appengine/) is a great hosting service provided by Google.
The really cool feature is it scales automatically. Deploy and never worry about any scaling issues - it is really that simple.

Ninja is fully compatible to the App Engine via the ninja-appengine module.

All informations you need are at:

    https://github.com/ninjaframework/ninja-appengine/


Heroku support
--------------

Ninja is fully supported by Heroku. Check out the blog post at:
http://ars-codia.raphaelbauer.com/2013/07/running-ninja-web-framework-apps-on.html

