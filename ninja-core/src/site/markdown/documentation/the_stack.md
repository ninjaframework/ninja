The stack
=========

Ninja is an integrated package and the aim is that you don't have to setup everything yourself.
Ideally you just generate a new project from our maven archetype, import the project into your
IDE and start hacking.

Therefore Ninja is made up of libraries that deliver everything. From Html, Json and Xml rendering
and parsing to management of different environments, database persistence, testing and much more.

Here is a tiny overview:

Frontend
--------
- Html rendering (Freemaker)
- Json rendering / parsing (Jackson)
- Xml rendering / parsing (Jackson

Stateful restful
----------------
- Client side session / Authentication (ninja-session)
- Flash scope (ninja-flash)

Core libraries
--------------
- Injection support (Guice)
- Mutiple environment configuration (Ninja)
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
- NinjaApiDocTest (Devbliss DocTest)
- NinjaFluentLeniumTest (FluentLenium)


Ninja makes sure that all libraries mentioned above work together nicely. And as you can see we are standing
on the shoulder of giants. Ninja is really small compared to all the great libraries we can build on.


