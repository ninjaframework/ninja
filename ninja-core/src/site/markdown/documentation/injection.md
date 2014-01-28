Injection
=========

Writing Java applications with or without injection is a completely different thing.
While both ways work using Injection completely changes the way you write code.

In our opinion it for sure changes it to the better.

Using injection means you don't have to worry about assembling your classes. You
simply define how they should be assembled and you get them by using @Inject.

Ninja uses Guice as injection tool. Please
[refer to their manual](http://code.google.com/p/google-guice/) to get a feeling how cool that is.


Example
-------

Injection is a first class citizen of Ninja. Ninja would not be possible without Guice. Guice
is preconfigured for your application and you can use it out of the box.

The main starting point to define your bindings is a file "conf/Module.java".

This module is a plain Guice module that will be started when you start your application.

<pre class="prettyprint">

    public class Module extends AbstractModule {

        protected void configure() {

            bind(GreetingService.class).to(GreetingServiceImpl.class);
        }

    }

</pre>


And that's it already. You can now simply inject the GreetingService (an interface)
into your controller and get a ready to use implementation:

<pre class="prettyprint">

    @Singleton
    public class InjectionExampleController {
	
	@Inject
        private GreetingService greeter;

        public Result injection(Context context) {

            return Results.html().render("greeting", greeter.hello());

        }
    }

</pre>

You can also use Providers, annotations like @Singleton and so on. Guice does
all the configuration for you.



