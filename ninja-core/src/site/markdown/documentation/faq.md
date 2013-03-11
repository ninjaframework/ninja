FAQ
==========

When i run ninja through eclipse, after few code reloads i got PermGen
------------

open the window where you created <b>jetty:run</b> goal
go to JRE tab and add to VM arguments
<b>-XX:+CMSClassUnloadingEnabled -XX:PermSize=512M -XX:MaxPermSize=1024M</b>

Ninja doesn't shutdown gracefully.
------------
The red stop button forcibly kills the application, i.e. not gracefully, 
so the JVM doesn't know that the application is exiting, therefore the shutdown hooks are not invoked.

Create new goal <b>jetty:stop</b> and stop the app this way