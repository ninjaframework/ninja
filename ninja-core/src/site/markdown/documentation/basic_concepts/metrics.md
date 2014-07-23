Metrics
==============

Ninja provides optional integration with [Metrics](http://metrics.dropwizard.io/) for measuring the responsiveness of your application.

Setup
------------------

### Add the Ninja-Metrics dependency

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-metrics</artifactId>
        <version>${ninja.version}</version>
    </dependency>


### Install the `MetricsModule` in your `conf.Module` class.

<pre class="prettyprint">
@Singleton
public class Module extends AbstractModule {

    @Override
    protected void configure() {

        install(new MetricsModule());

    }
}
</pre>

### Subclass InstrumentedNinja in your `conf.Ninja` class.

<pre class="prettyprint">
package conf;

import ninja.metrics.InstrumentedNinja;

public class Ninja extends InstrumentedNinja {

}

</pre>

### Collecting Route Metrics

Now you are ready to start annotating your controllers.

You have three choices in the collection of route metrics:

- *Do nothing*
- *Metered*
A meter measures the rate of events over time (e.g., “requests per second”). In addition to the mean rate, meters also track 1-, 5-, and 15-minute moving averages.
- *Timed*
A timer measures both the rate that a particular piece of code is called and the distribution of its duration.

1. Start by sprinkling `@Metered` or `@Timed` on some of your controller methods.
2. Start up VisualVM (and install the MBeans plugin) or JConsole.
3. Browse your app and refresh the collected metrics.

<pre class="prettyprint">
package controllers;

@Singleton
public class AppController {

    @Timed
    public Result index() {

        return Results.html();
    }

}
</pre>

### Collecting Cache Metrics

If you want to instrument your NinjaCache, Ninja-Metrics supports instrumenting both the EhCache and the Memcached implementations.

In your `application.conf` file specify:

    cache.implementation = ninja.metrics.InstrumentedEhCache

or

    cache.implementation = ninja.metrics.InstrumentedMemcached

Reporting
------------------

By default, Ninja Metrics will expose your metrics over JMX. You may disable this behavior by setting *metrics.jmx=false* in your `application.conf` file.

You can view the collected metrics using VisualVM (with the MBeans plugin installed) or using JConsole.
