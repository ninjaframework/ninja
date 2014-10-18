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

### Subclass InstrumentedNinja in your `conf.Ninja` class. (optional)

<pre class="prettyprint">
package conf;

import ninja.metrics.InstrumentedNinja;

public class Ninja extends InstrumentedNinja {

}

</pre>

### Collecting Metrics

Now you are ready to start annotating your controllers or any other methods.

You have several choices in the collection of metrics:

- *Do nothing*
- *Counted*
A counter increments (and optionally decrements) when a method is executed.
- *Metered*
A meter measures the rate of events over time (e.g., “requests per second”). In addition to the mean rate, meters also track 1-, 5-, and 15-minute moving averages.
- *Timed*
A timer measures both the rate that a particular piece of code is called and the distribution of its duration.

1. Start by sprinkling `@Counted`, `@Metered`, or `@Timed` on some of your controller methods.
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

### Collecting Additional Metrics

#### JVM Metrics

You may optionally enable JVM-level details reporting by setting *metrics.jvm.enabled=true* in your `application.conf` file.

    metrics.jvm.enabled = true

#### Logback Metrics

You may optionally enable reporting of Logback log-level counts by setting *metrics.logback.enabled=true* in your `application.conf` file.

    metrics.logback.enabled = true

### Reporting Metrics via MBeans for VisualVM, JConsole, or JMX

If you want to expose your metrics to VisualVM, JConsole, or JMX you must enable the MBeans reporter in your `application.conf` file.

    metrics.mbeans.enabled = true

You can view the collected metrics using VisualVM (with the MBeans plugin installed) or using JConsole.

### Reporting Metrics to Graphite

Ninja Metrics supports reporting to [Graphite](https://github.com/graphite-project).

Add the following dependency to your application `pom.xml`.

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-metrics-graphite</artifactId>
        <version>${ninja.version}</version>
    </dependency>

Bind the Graphite integration in your `Module.java`.

<pre class="prettyprint">
@Singleton
public class Module extends AbstractModule {

  @Override
  protected void configure() {
    bind(NinjaGraphite.class);
  }
}
</pre>

Add the following settings to your `application.conf`.

    metrics.graphite.enabled = true
    metrics.graphite.address = graphite.example.com
    metrics.graphite.port = 2003
    metrics.graphite.pickled = false
    metrics.graphite.period = 60s

### Reporting Metrics to Ganglia

Ninja Metrics supports reporting to [Ganglia](http://ganglia.info).

Add the following dependency to your application `pom.xml`.

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-metrics-ganglia</artifactId>
        <version>${ninja.version}</version>
    </dependency>

Bind the Ganglia integration in your `Module.java`.

<pre class="prettyprint">
@Singleton
public class Module extends AbstractModule {

  @Override
  protected void configure() {
    bind(NinjaGanglia.class);
  }
}
</pre>

Add the following settings to your `application.conf`.

    metrics.ganglia.enabled = true
    metrics.ganglia.address = ganglia.example.com
    metrics.ganglia.port = 8649
    metrics.ganglia.period = 60s

### Reporting Metrics to InfluxDB

Ninja Metrics supports reporting to [InfluxDB](http://influxdb.com).

Add the following dependency to your application `pom.xml`.

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-metrics-influxdb</artifactId>
        <version>${ninja.version}</version>
    </dependency>

Bind the InfluxDB integration in your `Module.java`.

<pre class="prettyprint">
@Singleton
public class Module extends AbstractModule {

  @Override
  protected void configure() {
    bind(NinjaInfluxDB.class);
  }
}
</pre>

Add the following settings to your `application.conf`.

    metrics.influxdb.enabled = true
    metrics.influxdb.address = localhost
    metrics.influxdb.port = 8086
    metrics.influxdb.database = mydb
    metrics.influxdb.username = root
    metrics.influxdb.password = root
    metrics.influxdb.period = 60s

### Reporting Metrics to Librato

[Librato](http://metrics.librato.com) is a cloud-based metrics database and dashboard service.

Add the following dependency to your application `pom.xml`.

    <dependency>
        <groupId>org.ninjaframework</groupId>
        <artifactId>ninja-metrics-librato</artifactId>
        <version>${ninja.version}</version>
    </dependency>

Bind the Librato integration in your `Module.java`.

<pre class="prettyprint">
@Singleton
public class Module extends AbstractModule {

  @Override
  protected void configure() {
    bind(NinjaLibrato.class);
  }
}
</pre>

Add the following settings to your `application.conf`.

    metrics.librato.enabled = true
    metrics.librato.username = person@example.com
    metrics.librato.apikey = 12345cafebabe
    metrics.librato.period = 60s
