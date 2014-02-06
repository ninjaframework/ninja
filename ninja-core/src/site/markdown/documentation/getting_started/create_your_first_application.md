Create your first application
=============================

Ninja features so called "Maven archetypes". These archetypes are blueprints
that allow you to generate a new project based on a predefined blueprint.

In Ninja's case we recommend to create a new Ninja project based on our
"ninja-servlet-jpa-blog-archetype". This archetype is a blog-like application
and shows a real world application written in Ninja.

You should use that archetype as starting point to create your own application.

The command to do so is:

<pre class="prettyprint">
mvn archetype:generate -DarchetypeGroupId=org.ninjaframework -DarchetypeArtifactId=ninja-servlet-archetype-simple
</pre>

Please enter sensible values for "groupId" and "artifactId" and let Maven 
generate your first Ninja project.


After finishing the generation change into your project directory and execute:

<pre class="prettyprint">
cd MY_INSTALLED_PROJECT
mvn clean install     // to generate the compiled classes the first time
mvn ninja:run         // to start Ninja's SuperDevMode
</pre>

This starts Ninja's SuperDevMode. Simply open http://localhost:8080 in your browser.
You'll see Ninja demo project ready to work on. That's it basically. You just created
your first Ninja application!

<div class="alert alert-info">
We think that fast and responsive development cycles are a key success factor
for software projects. SuperDevMode is our answer to that challenge. Say goodbye
to long and time consuming deployment cycles while developing.
</div>
