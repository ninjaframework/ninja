# SuperDevMode

## Why 

If you develop your applications you want super fast round trips. Deploying
you application to an application server and waiting a long time until stuff
is ready is just not productive.

On the other hand there is already seemingly good solution: the jetty-maven-plugin and
the jetty:run goal.

Unfortunately jetty-maven-plugin and its jetty:run goal often leads to PermGen 
exceptions that block your JVM and cause nasty interruptions to productivity.

Therefore we developed Ninja's SuperDevMode. SuperDevMode does not suffer
any PermGen exceptions and picks up any changes in your code really fast.


## How does it work

In any case you need an IDE that compiles your classes when you change them.
NetBeans' "compile on save" works, Eclipse's "build automatically" feature works, but
potentially any IDE out there will compile your Java files when you save them.

The flow is as follows: You start Ninja's SuperDevMode in a console. Then you
edit a Java file in your IDE and save it. Your IDE will then compile
your Java file to a class file. Ninja's SuperDevMode recognizes that and
restarts Ninja within a second. You can then switch to your browser and
verify that your changes work at http://localhost:8080 .

Rinse and repeat.

##Configuration

If you created your application from one of our recent archetypes you
are already ready to go.

If not add the following plugin to your pom.xml file:

<pre class="prettyprint">
&lt;plugin&gt;
   &lt;groupId&gt;org.ninjaframework&lt;/groupId&gt;
   &lt;artifactId&gt;ninja-maven-plugin&lt;/artifactId&gt;
   &lt;version&gt;${ninja.version}&lt;/version&gt;
&lt;/plugin&gt;  
</pre>

You can then call:

<pre class="prettyprint">
mvn ninja:run
</pre>

And have a damn fast hot-reload code server running.


## Advanced configuration

An example configuration looks like:

<pre class="prettyprint">
&lt;plugin&gt;
    &lt;groupId&gt;org.ninjaframework&lt;/groupId&gt;
    &lt;artifactId&gt;ninja-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;${project.version}&lt;/version&gt;
    &lt;configuration&gt;
        &lt;useDefaultExcludes&gt;true&lt;/useDefaultExcludes&gt;
        &lt;excludes&gt;
            &lt;exclude&gt;(.*)png$&lt;/exclude&gt;
        &lt;/excludes&gt;
        &lt;contextPath&gt;/your_context_path&lt;/contextPath&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;
</pre>

You can configure the plugin via the following parameters:
 
### useDefaultExcludes

Can be true or false. Ninja's SuperDevMode will not restart when
you make changes inside templates (ftl.html) or your assets directory. These changes
will be picked up anyway by Ninja in devmode. But there might be cases were
you want Ninja to restart. Then simply set useDefaultExcludes to false.

### excludes

If you want to tell Ninja's SuperDevMode that it should no pick up changes
of certain files or patterns you can define that by the parameter excludes.

In the example above no file ending in "png" will cause a reload. You can
use Java regular expressions to specify the files.

### contextPath

Allows you to add a context prefix to your application ("/" by default).
If the option is omitted you can provide it by system property:

<pre class="prettyprint">
mvn ninja:run -Dninja.context=/your_context_path
</pre>

### port

Allows you to set a custom port when running SuperDevMode (8080 default).
If the option is omitted you can provide it by system property:

<pre class="prettyprint">
mvn ninja:run -Dninja.port=YourPortNumber
</pre>
