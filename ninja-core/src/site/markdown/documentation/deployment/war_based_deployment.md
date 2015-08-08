War based deployment with Java application servers
==================================================

To generate a nice war file from your application make sure you have the
following dependency declared in your project's pom.xml:

<pre class="prettyprint">
&lt;dependency&gt;
    &lt;groupId&gt;org.ninjaframework&lt;/groupId&gt;
    &lt;artifactId&gt;ninja-servlet&lt;/artifactId&gt;
    &lt;version&gt;X.X.X&lt;/version&gt;
&lt;/dependency&gt;   
</pre>

Also make sure that ninja-standalone is NOT declared as dependency.

Then you can run...

<pre class="prettyprint">
mvn package
</pre>

...and get a  nice <code>war</code> file in the subfolder /target of your project. That war file is compatible with almost all
war containers out there. Jetty and Tomcat are two of them, but you can also deploy to Glassfish and JBoss.

<div class="alert alert-info">
Make sure you ran "mvn ninja:run" at least one time. 
This will generate a unique secret in application.conf that will 
be used to sign your application's sessions.
</div>

Advanced deployment scenario
----------------------------

With [Tomcat's hot deployment](http://www.mulesoft.com/tomcat-deploy) function 
you can simply take the war file produced by Ninja and drop it into the Tomcat's webapp folder.
Tomcat automatically detects the new file, stops the old application and starts the new one.


The very same mechanism works nicely when Tomcat is running in a cluster
configuration. Simply drop the new war file into the webapp folder and that Tomcat instance will tell all
cluster Tomcat's about the file. Subsequently the cluster changes to that new version of your application.

You can easily automate that process by using the [maven cargo plugin](http://cargo.codehaus.org/Maven2+plugin). 
That makes it really easy to  get a simple Jenkins based continuous deployment up and running.

That's the power of following well proven standards. Ninja - and the apps you are 
building with Ninja - are standing on the shoulder of giants.
