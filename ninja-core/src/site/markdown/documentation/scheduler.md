Scheduler
==========

Introduction
-------------
Sometimes you have to do some tasks in a scheduled way. Let's say every 24 hours.

To make that happen in Ninja you need two things

 * An method annotated of class X with <code>@Schedule</code>
 * X being bound explicitly by Guice
 

The class and binding
---------------------

The class and method then looks like:

<pre class="prettyprint">
@Singleton
public class ScheduledAction {


    @Schedule(delay = 60, initialDelay = 5, timeUnit = TimeUnit.SECONDS)
    public void doStuffEach60Seconds() {
        // do stuff
    }
}
</pre>   

Don't forget to bind the class explicitly inside conf/Module.java

<pre class="prettyprint">
public class Module extends AbstractModule {

    protected void configure() {

        bind(ScheduledAction.class);

    }
}
</pre> 


By that Ninja will execute method doStuffEach60Seconds each - well - 60 seconds.


