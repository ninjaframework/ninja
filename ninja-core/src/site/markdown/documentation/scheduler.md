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


CRON expression
---------------

Sometimes you need to schedule tasks in an advanced way and without depending on
when the application has started. For this you can use a CRON expression.

The format of the CRON expression must be : second, minute, hour, day of month,
month and day of week.


<table class="table">
  <thead>
    <tr>
      <th scope="col">Unit</th>
      <th scope="col">Value</th>
      <th scope="col">Step Value</th>
      <th scope="col">Extra Information</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th scope="row">Second</th>
      <td>0 - 59</td>
      <td>1 - 60</td>
      <td>–</td>
    </tr>
    <tr>
      <th scope="row">Minute</th>
      <td>0 - 59</td>
      <td>1 - 60</td>
      <td>–</td>
    </tr>
    <tr>
      <th scope="row">Hour</th>
      <td>0 - 23</td>
      <td>1 - 24</td>
      <td>–</td>
    </tr>
    <tr>
      <th scope="row">Day of Month</th>
      <td>0 - 31</td>
      <td>1 - 32</td>
      <td>–</td>
    </tr>
   <tr>
      <th scope="row">Month</th>
      <td>0 - 12</td>
      <td>1 - 13</td>
      <td>–</td>
    </tr>
   <tr>
      <th scope="row">Day of Week</th>
      <td>0 - 6</td>
      <td>1 - 7</td>
      <td>0: Sunday, 1: Monday, ..., 6: Saturday</td>
    </tr>
  </tbody>
</table>


<pre class="prettyprint">
@Singleton
public class ScheduledAction {


    @Schedule(cron = "0 */5 * * * *")
    public void doStuffEach5minutes() {
        // do stuff
    }

    @Schedule(cron = "0 0 23 * * *", cronZone = "Europe/Paris")
    public void doStuffEachDayAt23HourEuropeParis() {
        // do stuff
    }

    @Schedule(cron = "0 30 2,14 * * 1-5")
    public void doStuffTwiceADayFromMondayToFriday() {
        // do stuff
    }
}
</pre>


Execution thread pool
---------------------

By default, scheduled tasks run on a single thread, which can be sufficient
for most cases. However, if you need to run several tasks at the same time
or if the execution of your tasks takes too much time and delays the others,
you can change the size of the thread pool allocated to scheduled tasks.

To change the size of the thread pool used by scheduled tasks, you must use
the <code>scheduler.thread_pool_size</code> configuration key in the
configuration file.
