Getting Started - Installing Ninja
==================================

Prerequisites
-------------

You'll need just 3 things to develop with Ninja:

* **JDK** (Java Development Kit), version **1.8** and above
* **Maven** (Project Management Tool), version **3.1.1** and above
* **Internet** connection (to fetch libraries)

If you already have the JDK and Maven installed you can safely skip the following sections.

**Note:** Ninja is compatible with Java 11 and we'll support future Java versions
with long term support.
 
Installing Java
---------------

Ninja is using the Java as programming language and the Java Virtual Machine
to run your applications. You have to make sure that you are running at least
Java in version 1.8.

You can check that by executing the following command:

<pre class="prettyprint">
java -version
</pre>

... which prints out the following:

<pre class="prettyprint">
java version "1.8.0_171"
Java(TM) SE Runtime Environment (build 1.8.0_171-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.171-b11, mixed mode)java version "1.8.0_171"
Java(TM) SE Runtime Environment (build 1.8.0_171-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.171-b11, mixed mode)
</pre>

As you can see, this machine is running Java 1.8.0_171. If you are using an older
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
...
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.7.5", arch: "x86_64", family: "mac"
</pre>

Make sure that you are using Maven 3 or greater. 
If Maven is not available on your computer please follow the following guide to setup Maven:

http://maven.apache.org/guides/getting-started

