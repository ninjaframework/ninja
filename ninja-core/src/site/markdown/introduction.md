The problem we solve
====================

Introduction
------------

We successfully developed and maintained 
applications with different frameworks. For our own
businesses and for our customers. Frameworks we used were mainly
JEE, Spring but also Play framework (1 and 2).

To make it clear: All these frameworks are awesome. We love them and enjoy working with them :)
They are a huge help in developing large scale applications in Java.

But for our particular 
use cases those frameworks often did not feel right for many many reasons.

Somewhere in 2012 we took a deep breath and began developing ideas about
the web framework of our dreams. How should such a framework look like? What are
the key features we always needed to complete and maintain a software project successfully?


Key features we wanted to see
-----------------------------

After long hours of discussions and a lot of prototypes we came 
up with the following key features:

 * Web friendly. Restful architecture. Simple programming model.
 * Plain vanilla Java. Dependency Injection. First class IDE integration.
 * Super fast development cycles.
 * Simple and fast to test (Mocked tests / Integration tests).
 * Excellent build and CI support.
 * Simple JSON consumption and rendering for clean RESTful APIs.
 * HTML rendering / Form submission validation and parsing.
 * Built-in support for authentication of users.
 * Not much bytecode magic. Clean codebase - easy to extend.

And because we have many customers that run their applications on the 
Google App Engine (GAE) we needed to support the GAE, too.

Back in 2012 there was not a single Java framework (!) that supported our use case 
out of the box.