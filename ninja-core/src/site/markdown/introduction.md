The Ninja Web Framework
=======================

The problem
-----------

We are in the business of software development since 1998. And
we successfully wrote applications with different frameworks for our customers.
JEE, Spring but also Play framework (1 and 2).

But often those widely used frameworks were more a burden than a help.

Somewhere in 2012 we took a deep breath and began developing ideas about
the framework of our dreams. How should such a framework look like? What are
the key features we always needed to complete and maintain a project successfully?


Key features we wanted to see
-----------------------------

We discussed a lot of things. What are the key features that helped to make
a project a success? What helped us as company to deliver a successful
project?

We came up with the following bullet points:

 * First class citizen of the Java ecosystem / First class IDE integration
 * Super fast development cycles.
 * Build-in authentication.
 * Html rendering / form submission parsing.
 * Simple Json reading / writing.
 * Web friendly. Restful architecture. Simple programming model.
 * Dependency injection.
 * First class testability.
 * Different environments for production, testing, development.
 * Excellent build and CI support.
 * No bytecode magic.

And because we have many customers that run their applications on the 
Google App Engine (GAE) we needed to support the GAE, too.

Back in 2010 there was no (!) single Java framework that supported our use case 
out of the box.