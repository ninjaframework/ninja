Creating your first application
===============================

The best way to start a Ninja project is by executing the following command:

<pre class="prettyprint">
mvn archetype:generate -DarchetypeGroupId=org.ninjaframework -DarchetypeArtifactId=ninja-servlet-jpa-blog-archetype -DarchetypeVersion=2.5.1
</pre>

This command tells Maven (mvn) to create a new Ninja application based on a blueprint.
Please enter sensible values for "groupId" and "artifactId" and let Maven generate your first Ninja project.


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
