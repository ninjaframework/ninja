Deployment
==========

You just developed a great application - and now you want to host it somewhere.
Fortunately, Ninja is built on standard technologies. 

Therefore you got a myriad of great options for deployment.


War based deployment with Java application servers
--------------------------------------------------

When you run

    > mvn war:war

You get a war file in the subfolder /target of your project. That war file is compatible with almost all
war containers out there. Jetty and Tomcat are two of them, but you can also deploy to Glassfish and JBoss.

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


Deployment on Google App Engine
-------------------------------

[Google's App Engine] (https://developers.google.com/appengine/) is a great hosting service provided by Google.
The really cool feature is it scales automatically. Deploy and never worry about any scaling issues - it is really that simple.

Of course Ninja is fully compatible to the App Engine.

You can find a working demo project at: https://github.com/reyez/ninja/tree/develop/ninja-demo-application-app-engine .

The special parts are:

1) Make sure you got a file called appengine-web.xml in your /src/main/webapp/WEB-INF directory.

The contents of the file look like:

    <?xml version="1.0" encoding="utf-8"?>
    <appengine-web-app xmlns="http://appengine.google.com/ns/1.0">
        <application>MY-APP-ID</application>
        <version>2012-07-27</version>
        <threadsafe>true</threadsafe>
    </appengine-web-app>

This is a standard descriptor of the App Engine and you can define a lot more using that descriptor. Check out the
App Engine docs for details


2) We need the App Engine SDK and a special way to upload stuff

As we are using Maven we can use a great Maven plugin from [net.kindleit] (http://www.kindleit.net/maven_gae_plugin/usage.html).
A minimal configuration in your pom.xml will look like:

    <plugin>
        <groupId>net.kindleit</groupId>
        <artifactId>maven-gae-plugin</artifactId>
        <version>0.9.4</version>
        <dependencies>
            <dependency>
                <groupId>net.kindleit</groupId>
                <artifactId>gae-runtime</artifactId>
                <version>${gae.version}</version>
                <type>pom</type>
            </dependency>
        </dependencies>
    <plugin>
    
After adding the plugin to your Maven build your have to execute

    mvn gae:unpack
    
This will download the latest App Engine SDK automatically.

Then you can call
    
    mvn gae:update
    
This will prompt you for your username / password and upload your Ninja application
to Google's cloud. Done.



Heroku support
--------------

Ninja is fully supported by Heroku. Check out the guide at their website at:
https://devcenter.heroku.com/articles/java .

