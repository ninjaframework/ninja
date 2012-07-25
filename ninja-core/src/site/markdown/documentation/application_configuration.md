Configure your application
==========================

Introduction to application.conf
--------------------------------

By convention Ninja will look for a configuration file at "conf/application.conf".

application.conf is a simple properties file with keys and values. "#" is a comment.

    # an arbitrary content
    application.name=Ninja demo application
    application.cookie.prefix=NINJA

Like any other Ninja file it is encoded in Utf-8. Make sure your editor reads and
saves the file in the correct encoding. 

Ninja properties can do much more than traditional Java properties. For instance
it is possible to reference other properties by their name and combine them.

    serverName=http://myserver.com
    serverPort=80
    fullServerName=${serverName}:${serverPort}
    
fullServerName references the previously set values of the keys and will return "http://myserver.com:80".

Inside your application you can use class NinjaProperties to get the properties.

    NinjaProperties.get("fullServerName")            # will return "http://myserver.com:80"

The properties are read and managed by the excellent Apache Configurations library. Please
refer to [their manual](http://commons.apache.org/configuration/) for even more information on advanced usage.


Configuring the modes
---------------------
Ninja uses three predefined modes (test, dev, prod as defined in NinjaConstant.java). You can set the
modes by setting a Java system property called "ninja.mode".

    On the command line:
    > java -Dninja.mode=dev
    
    Or programmatically:
    System.setProperty(NinjaConstant.MODE_KEY, NinjaConstant.MODE_DEV)

Just make sure that you set the system property early so that Ninja knows its mode from the very beginning.


This modes are handy if you want to define different properties for different environments.
You may want to use database1 when running tests and database2 when developing.

Ninja supports that use case by using a mode prefix prior to your property key.

    database.name=database_production   # will be used when no mode is set (or prod)
    %dev.database.name=database_test    # will be used when running in dev mode
    %test.database.name=database_test   # will be used when running in test mode

The convention is to use a "%" and the name of the mode followed by ".".


External configuration for deployment
-------------------------------------

When running on a server you may want to use a completely different configuration.
This can be accomplished by using the setting a Java system property:

    java -Dninja.external.configuration=production.conf

This tells Ninja to load conf/application.conf as usual - but also load production.conf and
replace all keys that are mentioned in both files with the one from production.conf.

That way you can manage a production configuration separately from
your project. For instance - you may want to do this when your server secret should only
be available to a certain set of people and not all devs. 

Just make sure the file "production.conf" is on the classpath. Ninja currently does
not read the file from the file system. It is loaded solely from the classpath to
circumvent any Security Issues with your container.


