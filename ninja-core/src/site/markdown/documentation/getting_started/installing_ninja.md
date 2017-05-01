Getting Started - Installing Ninja
==================================

Prerequisites
-------------

You'll need just 3 things to develop with Ninja:

* **JDK**  (Java Development Kit), version **1.7** and above
* **Maven** (Project Management Tool), version **3.1.1** and above
* **Internet** connection (to fetch libraries)

If you already have the JDK and Maven installed you can safely skip the following sections.
 
Installing Java
---------------

Ninja is using the Java as programming language and the Java Virtual Machine
to run your applications. You have to make sure that you are running at least
Java in version 1.7.

You can check that by executing the following command:

<pre class="prettyprint">
java -version
</pre>

... which prints out the following:

<pre class="prettyprint">
java version "1.7.0_13"
Java(TM) SE Runtime Environment (build 1.7.0_13-b20)
Java HotSpot(TM) 64-Bit Server VM (build 23.7-b01, mixed mode)
</pre>

As you can see, this machine is running Java 1.7.0_13. If you are using an older
version please install the latest Java version from: 

http://www.oracle.com/technetwork/java/javase/downloads/index.html


Installing Maven
----------------

Ninja uses a Java tool called Maven to build and create new projects. 
Maven is used by the majority of Java based projects, and most likely you already
have it installed on your machine. Let's check if Maven is installed:

<pre class="prettyprint">
mvn -version
</pre>

This will print out the version of your Maven installation, similar to this:

<pre class="prettyprint">
Apache Maven 3.1.1 (0728685237757ffbf44136acec0402957f723d9a; 2013-09-17 17:22:22+0200)
Maven home: /Users/user/applications/apache-maven-3.1.1
Java version: 1.7.0_13, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.7.0_13.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.7.5", arch: "x86_64", family: "mac"
</pre>

Make sure that you are using Maven 3 or greater. 
If Maven is not available on your computer please follow the following guide to setup Maven:

http://maven.apache.org/guides/getting-started

If you like to use the simplified form of calling the Ninja Maven plugin you have to
define the pluginGroup within your <code>settings.xml</code>:

<pre class="prettyprint">
  .
  <pluginGroups>
    <pluginGroup>org.ninjaframework</pluginGroup>
  </pluginGroups>
  .
</pre>

otherwise you have to use the full qualified way of calling the Ninja Maven plugin via:

<pre class="prettyprint">
mvn org.ninjaframework:ninja-maven-plugin:run
</pre>
