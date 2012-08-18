Debugging
==========

Introduction
------------

Ninja is based around standards. If you run a Ninja application inside a servlet container you can simply
connect to the servlet container via your IDE and debug the running application. So there are
potentially a lot of ways how you can debug your app.

Fee free to suggest other ways to debug a Ninja application!


Debbuging via Eclipse and m2eclipse plugin
------------------------------------------

Our favorite way to debug an application is to use Eclipse. We are always using the m2eclipse plugin to import our
project.

And if you make a left click on your project you get the option "Debug as...". Hit that and generate a profile
that debugs the goal jetty:run. That's all. Now eclipse starts your application and you can cleanly debug
all stuff that is going on