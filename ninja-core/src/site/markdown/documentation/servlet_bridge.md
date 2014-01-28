Servlet bridge
==============

Intro
-----

Ninja is designed to not depend on a specific web container technology per se.
You can run Ninja inside any servlet container - but also as Netty application.

In reality, however, Ninja tends to run 99% in servlet based containers. Tomcats, Jettys and so on.
It is also true that many users want to have a clear migration path. 

Your organization possibly already has a lot of Servlets you want to reuse. Or filters you wrote, 
or servlet based plugins you want to combine with Ninja.

The servlet bridge supports this use case and makes it really simple to combine servlet technology
and Ninja to get the best of both worlds.


The Servlet-bridge
------------------

The best way to add servlets and filters to Ninja is to use Ninja's SevletModule at /conf/ServletModule.java .

<pre class="prettyprint">
public class ServletModule extends com.google.inject.servlet.ServletModule {


    @Override
    protected void configureServlets() {

        bind(LegacyServletFilter.class).asEagerSingleton();
        bind(NinjaServletDispatcher.class).asEagerSingleton();

        filter("/*").through(LegacyServletFilter.class);
        serve("/*").with(NinjaServletDispatcher.class);
    }

}
</pre>


We using an arbitrary Servlet filter (LegacyServletFilter) to filter stuff completely outside of Ninja. It is of course
a better way to use Ninja's filters directly. But if you got a large legacy app it can make sense to combine
both technologies for a smooth transition.

Please note: ServletFilter.java is optional. If the file is not present all requests will be handled by
Ninja.


Note of caution
---------------

1) Using serlvets and servlet filters is a really convenient way. But there is a big danger: If you don't know
what you are doing you might end up with a framework that does not scale. Please remember: Ninja is stateless
and does not use and servlet sessions. If you combine Ninja with servlets that use servlet sessions you can no
longer count on the fact that your Ninja app scales with ease.

It is usually a better idea writing a module for Ninja and use Ninja's session mechanism to circumvent that problem.

2) ServletModule only works when Ninja is actually running inside a servlet container. If you are running Ninja
inside a Netty this will not work as Netty does not understand the servlet spec.
