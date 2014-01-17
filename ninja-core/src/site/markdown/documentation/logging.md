Logging
=======

Ninja uses the Logback via slf4j as logging library. The prototypes both contain a file called logback.xml. Logback.xml
configures the logging behavior. Below is an exemplary logging configuration. But in general
the best way to configure Logback is to follow the excellent guide at: http://logback.qos.ch/manual/configuration.html

<pre class="prettyprint">
&lt;configuration&gt;

  &lt;appender name=&quot;FILE&quot; class=&quot;ch.qos.logback.core.FileAppender&quot;&gt;
    &lt;file&gt;myApp.log&lt;/file&gt;

    &lt;encoder&gt;
      &lt;pattern&gt;%date %level [%thread] %logger{10} [%file:%line] %msg%n&lt;/pattern&gt;
    &lt;/encoder&gt;
  &lt;/appender&gt;

  &lt;appender name=&quot;STDOUT&quot; class=&quot;ch.qos.logback.core.ConsoleAppender&quot;&gt;
    &lt;encoder&gt;
      &lt;pattern&gt;%msg%n&lt;/pattern&gt;
    &lt;/encoder&gt;
  &lt;/appender&gt;

  &lt;root level=&quot;debug&quot;&gt;
    &lt;appender-ref ref=&quot;FILE&quot; /&gt;
    &lt;appender-ref ref=&quot;STDOUT&quot; /&gt;
  &lt;/root&gt;
&lt;/configuration&gt;
</pre>

