Debugging
==========

Introduction
------------

Ninja is based around standards. If you run a Ninja application inside a servlet container you can simply
connect to the servlet container via your IDE and debug the running application. So there are
potentially a lot of ways how you can debug your app.

Feel free to suggest other ways to debug a Ninja application!


Debugging via Eclipse and m2eclipse plugin
------------------------------------------

Our favorite way to debug an application is to use Eclipse. We are always using the m2eclipse plugin to import our
project.

And if you make a left click on your project you get the option "Debug as...". Hit that and generate a profile
that debugs the goal jetty:run. That's all. Now Eclipse starts your application and you can cleanly debug
everything that is going on.

Debugging via NetBeans
----------------------
1. Right click your Project and open "Project"
2. Click on "Action" on the left.
3. Choose "Debug project"
4. Enter "jetty:run" into the "Execute Goals" input field
5. Enter "jpda.listen=maven" into the "Set Properties" field
6. Click the "OK"-Button and start a Debug-Session with Ctrl+F5 or the Debug-Button
