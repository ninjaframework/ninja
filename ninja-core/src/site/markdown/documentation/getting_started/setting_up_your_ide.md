Setting up your IDE
===================

Just import it
--------------

The Ninja application you created via the archetype is just a simple 
plain old Maven project. This means you can import the project into any modern IDE. 
Eclipse, NetBeans, IntelliJ and many more. 

One catch...
------------

Ninja's SuperDevMode relies on an external compiler to compile your sources.
In essence the flow will be:

 * You run Ninja's SuperDevMode via "mvn ninja:run"
 * You change a Java file in your IDE
 * Your IDE picks up the changes and recompiles the results to a .class file
 * Ninja's SuperDevMode detects the recompiled file and immediately shows the latest
   version of your application at http://localhost:8080.

Nice. But the catch is that your IDE has to recompile the files. If your IDE
does not recompile the file Ninja's SuperDevMode will not work.

Fortunately IDE based compilation works out of the box for Eclipse 
("Automatically build project"), NetBeans ("Compile on Save") and IntelliJ 12+.
(http://stackoverflow.com/questions/12744303/intellij-idea-java-classes-not-auto-compiling-on-save).

<div class="alert alert-info">
Sometimes Ninja's SuperDevMode does no longer pick up changes you made.
In many cases automatic compilation by the IDE is broken. If you run into that problem
it helps to "clean and build" the project from within your IDE.
</div>