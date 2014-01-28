The Ninja Web Framework
=======================

Goals of Ninja
--------------

 * Provide a full stack Java web framework.
 * Superfast and responsive development cycles.
 * First class testing support.
 * Web friendly. Restful architecture. Simple programming model.
 * Easily scalable horizontally - share nothing approach.
 * No bytecode magic.
 * Built on standards: Maven for building, servlet containers for deployment, Ioc by Guice...
 
 
The stack
=========

Ninja is an integrated package and the aim is that you don't have to setup everything yourself.
Ideally you just generate a new project from our maven archetype, import the project into your
IDE and start hacking.

Therefore Ninja is made up of libraries that deliver everything. From Html, Json and Xml rendering
and parsing to management of different environments, database persistence, testing and much more.

Here is an overview:

Frontend
--------
- Html rendering (Freemaker)
- Json rendering / parsing (Jackson)
- Xml rendering / parsing (Jackson)

Stateful restful
----------------
- Client side session / Authentication (ninja-session)
- Flash scope (ninja-flash)

Core libraries
--------------
- Injection support (Guice)
- Multiple environment configuration (Ninja)
- i18n for templates / in controllers (Ninja)
- Lifecycle (Ninja)
- Mail sending (Ninja)
- Scheduler (Ninja)
- Object validation - JSR 303 (Hibernate-validation)
- Support library: (Google Guava)
- Logging (logback / slf4j)

Relational data persistence
---------------------------
- JPA (Hibernate)
- Database migrations (FlyWayDB)

Cache layer
-----------
- Memcached
- EhCache

Testing support
---------------
- NinjaRouterTest (Ninja)
- Mocked Tests (Mockito)
- NinjaTest (Ninja)
- NinjaDocTester (DocTester)
- NinjaFluentLeniumTest (FluentLenium)


Ninja makes sure that all libraries mentioned above work together nicely. And as you can see we are standing
on the shoulder of giants. Ninja is really small compared to all the great libraries we can build on.


 
