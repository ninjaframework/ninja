Deployment
==========

Introduction
------------

You just developed a great application - and now you want to host it somewhere.
Fortunately, Ninja is built on standard technologies. 

Therefore you got a myriad of great options for deployment.

The most basic deployment is a single instance. More complex deployments will use
more than one instance and a reverse proxy in front of them. Ninja uses
the share nothing principle, and sessions are client side. Therefore scaling
Ninja is incredibly simple. Just add more Ninja server instances and add them
to your reverse proxy configuration. That's all.


A note on GZIP, SPDY, SSL and more
----------------------------------

Ninja provides basic support for SSL and no support for GZip out of the box.
We think that's the responsibility of the application container or better your
reverse proxy like nginx.
