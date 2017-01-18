Cloud Hosting options
=====================

Introduction
------------

Hosting a Ninja application is pretty straight forward. At a very basic level you
only need a Java runtime environment - then you can use Ninja's standalone
mode and you are done.

But sometimes you don't want to host the application yourself and therefore we
have compiled a list of cloud hosting providers that support Ninja.

Google App Engine
-----------------

[Google's App Engine] (https://developers.google.com/appengine/) is a great hosting service provided by Google.
The really cool feature is that it scales automatically. 
Deploy and never worry about any scaling issues - that's the basic promise of the App Engine.

Ninja is fully compatible to the App Engine via the ninja-appengine module.

Please follow the guide at: https://github.com/ninjaframework/ninja-appengine/

Heroku support
--------------

Ninja is fully supported by [Heroku] (http://www.heroku.com). Check out the blog post at:
http://ars-codia.raphaelbauer.com/2013/07/running-ninja-web-framework-apps-on.html

Clever Cloud
------------

One other cloud option is [Clever Cloud] (http://www.clever-cloud.com), a Europe-based PaaS company. 
It supports different Java runtimes, with automatic scaling and cloud-friendly features. 
You can for example deploy your Ninja application directly from a [GitHub] (http://www.github.com) 
public or private repository, by following these simple steps :
 * First, create the file <code>clevercloud/jar.json</code> at the root of your project
<pre class="prettyprint">
  {
    "build": {
      "type": "maven", 
      "goal":"package -DskipTests"
    }, 
    "deploy": {
      "type": "jar", 
      "jarName": "target/yourApplicationArtifactId-1.0-SNAPSHOT.jar"
    }
  }
</pre>
 * Log in on Clever Cloud using your GitHub account
 * Click on "Add an application" and select the right GitHub repository
 * Select the "Java+Jar" runtime and fill in your application properties (name...)

That's enough to deploy a first instance of your application. And each time 
you will publish modifications on the repository, it will run the maven <code>package</code> 
command and use the generated fat Jar as an executable.

You can also add a database (PostgreSQL, MySQL...) to your application, by "Add an add-on" feature. 
Link it to your application and then use the add-on environment variables in your <code>conf/application.conf</code> 
file(using ${env:XXX} format). Some other add-ons are availalble, like an S3 file storage.

More
----

Ninja most likely runs on almost every Cloud hosting provider out there. Don't
hesitate to share your experiences with Ninja users and augment this page with
your knowledge.
