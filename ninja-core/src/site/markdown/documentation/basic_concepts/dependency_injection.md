Dependency injection
====================

Introduction
------------

In regular code you usually would use a lot of <code>new MyObject(...)</code> statements. But
that is usually bad because you don't have any outside control of the instances.
And this makes the code hard to test. It also makes it hard
to give the MyObject different behaviors - let's say for testing and in production.

You can of course use the factory pattern. But there, too, you find a strong 
coupling between the factory and the produced instances.

Dependency injection is in fact a factory pattern on steroids. There are different
implementations of the dependency injection pattern available, but Ninja uses
Google's Guice for dependency injection. 

The nice thing about Guice is the fact that there is one central point where
you can declare which interface should be implemented by what class. Whether
it is a Singleton or what Factory (aka Provider) should create that class.


Guice in Ninja
--------------

By convention Ninja will look for a Java file at <code>conf/Module.java</code>.
That module is a regular Guice module that will be automatically loaded when
your application starts up. This file is 100% pure Guice and you can use
all Guice goodies. If needed, the constructor of this module can take a 
<code>NinjaProperties</code> object as argument, but it's not mandatory.


An example
----------

A typical usecase it to have an interface that specifies certain methods and
glue these interface to implementations via Guice. Let's say we have an interface
that returns a message:

<pre class="prettyprint">
public interface GreetingService {
	String hello();
}
</pre>

and the following implementation:

<pre class="prettyprint">
public class GreetingServiceImpl implements GreetingService {
    
    @Override
	public String hello() {
		return "hi!!!!";
	}

}
</pre>

We can glue together interface and implementation via our 
<code>conf/Module.java</code>:

<pre class="prettyprint">
public class Module extends AbstractModule {

    @Override
    protected void configure() {       

        bind(GreetingService.class).to(GreetingServiceImpl.class);

    }

}
</pre>

And if you want to use that GreetingService inside your application you can
simply use the <code>@Inject</code> annotation:

<pre class="prettyprint">
public class ApplicationController {

    @Inject
    GreetingService greeter;

    public Result injection(Context context) {

        return Results.html().render("greeting", greeter.hello());

    }
}
</pre>

That's all. No more <code>new</code> statements in your code! That way you only
deal with interfaces in your code and you get the ability to change the real
implementation. You can easily replace real service implementations with
mocked service implementations when developing your application. And it also
gives you the ability to run clean mocked tests using Mockito.

Advanced configuration
----------------------

Ninja includes many default bindings in guice before your application module
`conf.Module` is called.  These include bindings for base Ninja features like logging,
lifecycle support, scheduler support, etc.  There are also bindings for "classic"
Ninja features such as freemarker templates, jackson json/xml support, cache,
postoffice, and JPA.

You can exclude Ninja's "classic" bindings by extending your `conf.Module`
from `ninja.conf.FrameworkModule` rather than `com.google.inject.AbstractModule`.
This instructs Ninja to not load `ninja.conf.NinjaClassicModule` by default.
You may find customizing `ninja.conf.NinjaClassicModule` useful in building the
exact set of features you'd like, especially since it has a builder-syntax for
enabling/disabling of feature sets.

<pre class="prettyprint">
package conf;

import ninja.conf.FrameworkModule;
import ninja.conf.NinjaClassicModule;
import ninja.utils.NinjaProperties;

public class Module extends FrameworkModule {

    private final NinjaProperties ninjaProperties;

    public Module(NinjaProperties ninjaProperties) {
        this.ninjaProperties = ninjaProperties;
    }
    
    @Override
    protected void configure() {
        // classic ninja stack but with no freemarker or xml
        install(new NinjaClassicModule(ninjaProperties)
            .freemarker(false)
            .xml(false)
        );
    }

}
</pre>

Conclusion
----------

Dependency injection is the key component to develop and maintain a clean and
large codebase in Java. Dependency injection is at the core of Ninja 
and implemented via Google Guice. 

The entry point for your application is the module
<code>conf/Module.java</code>.

Guice can do a lot more for you. Read more about the abilities here: 
https://github.com/google/guice .
