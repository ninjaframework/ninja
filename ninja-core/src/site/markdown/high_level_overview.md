High level overview - what does Ninja do for you?
=================================================

Ninja is an integrated software stack. The aim is that you don't have to 
setup everything yourself. Ideally you just generate a new project from our 
maven archetype, import the project into your IDE and start hacking.

Therefore Ninja is made up of libraries that deliver everything. From Html, Json and Xml rendering
and parsing to management of different environments, database persistence, testing and much more.

Here is an overview:

Build and development support
-----------------------------
- Apache Maven based. Easy to import in any IDE, simple to build on any continuous integration system.
- SuperDevMode hot reloading server for quick development turnaround.


Frontend
--------
- HTML rendering (Freemarker)
- JSON rendering / parsing (Jackson)
- XML rendering / parsing (Jackson)

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
- Logging (slf4j and logback)

Relational data persistence
---------------------------
- JPA (Hibernate)
- Database migrations (Flyway)

Cache layer
-----------
- Memcached
- EhCache

Testing support
---------------
- Mocked Tests (Mockito)
- NinjaTest (Ninja)
- NinjaDocTester (DocTester)
- NinjaFluentLeniumTest (FluentLenium)


Deployment to live
------------------

- Either as war in your favorite application container
- Or in standalone mode as self executing jar package with a bundled Jetty.
- In general synchronous programming style.
 
