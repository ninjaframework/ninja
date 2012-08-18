Injection
=========

Writing Java applications with or without injection is a completely different thing.
While both ways work using Injection completely changes the way you write code.

And it will change it to the better.

Using injection means you don't have to worry about assembling your classes. You
simply define how they should be assembled and you get them by using @Inject.

Ninja uses Guice as injection technology. Please
[refer to their manual](http://code.google.com/p/google-guice/) to get a feeling how cool that is.


Example
-------

Injection and is a first class citizen of Ninja. Ninja would not be possible without Guice.

The main starting point to define your bindings is a file "conf/Module.conf".

This module is a plain Guice module that will be started when you start your application.

<pre class="prettyprint">
    public class Module extends AbstractModule {

        protected void configure() {

            bind(GreetingService.class).to(GreetingServiceImpl.class);
        }

    }
</pre>


And that is all to use injection you can then simply inject the GreetingService (an interface)
into your controller and get a ready to use implementation:

<pre class="prettyprint">
    @Singleton
    public class InjectionExampleController {

        private GreetingService greeter;

        @Inject
        public InjectionExampleController(GreetingService greeter) {
            this.greeter = greeter;

        }

        public Result injection(Context context) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("greeting", greeter.hello());

            return Results.html().render(map);

        }
    }
</pre>

You can also use Providers, annotations like @Singleton and so on. Guice does
all the configuration for you. Hoooray!



