The Ninja Web Framework
=======================

Goals of Ninja
--------------

 * Provide a full stack web framework.
 * Fast and responsive development
 * Web friendly. Restful architecture. Simple programming model.
 * Easily scalable horizontally - share nothing approach.
 * No bytecode magic.
 * Built on standards: Maven for building, servlet containers for deployment, Ioc by Guice...
 

Introduction
-----------

It is a really stupid idea writing your own framework. It is even more stupid writing your own web framework. Did you know
that even Java's binary search contained a bug until 2006? Even with binary search being invented 50 years earlier. And
how many bugs do you think a new web framework will contain?

Well. Most probably many.

Nevertheless the work for Ninja - a new full stack web framework for the Java community - began early 2012. Out of frustration.
Out of the love for Java and productivity the Java eco-system offers.


Why another framework?
----------------------

The only capable full stack framework we know of in the Java world is Play. Play is great. And indeed the authors
of Ninja were using Play 1 series for a range of projects successfully.

Then early 2012 Play 2 was released. It was a bit different. The core was written in Scala. Templates in Scala and
typesafe. The architecture was a lot less magic and much more async.

Great changes. We thought.

However the reality was quite different. Scala was a pain because the relaods were slow. Play 2 uses sbt as build tool - 
a completely novel tool featuring some nasty bugs. IDE integration bas non existent. And
Play 2 was a complete rewrite and rewrites will contain strange errors. And we faced them a bit too often 
(Akka subsystem anyone). Even worse: We could not easily debug and fix the problem because the core was in Scala.
Additionally the async nature of the framework can lead to elegant solutions. 
But also to code that is incredibly hard to analyze and read.

Does that mean that Play 2 is a bad choice? No. As it matures it will become a great framework. We
heavily think that the team around Play 2 is composed of the best developers we have ever seen. And
Play 2 will be a great framework for the Scala community. It is our fault after all that we are not
as fluent in Scala as we should be.


Thinking about the framework
----------------------------

There we were - early 2012. Play 2 did not seem to work for us well. Play 1 was nice, but somehow discontinued. What
should we do? Continue with Play 1 for new projects? Fork Play 1? Switch to Rails? Swith to something else?

The truth is: We love the Java eco-system. The great Ide support. Great debuggers. Superior dependency and build tools. The
power of a statically typed language. All that is great.

Therefore we decided to write a new pure Java framework. We feel that Ninja is in the tradition of Play 1 and Rails. Therefore
we own a lot to the Play and Rails teams.

Conclusion
----------

Ninja delivers everything to get productive immediately.
And it does so by using a lot of proven technology that is used and known for years.

