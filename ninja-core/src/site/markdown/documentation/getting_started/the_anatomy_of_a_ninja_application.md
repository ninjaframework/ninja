The anatomy of a Ninja application
===================================

Intro
-----

Ninja does a lot of things via convention over configuration. If things are in 
the right place it will automatically work. 

For instance the routes for your
application should always at conf/Routes.java. And HTML views always map to a 
certain controller method via their directory structure.

The conventions are explained in detail in the later sections of the documentation.


An example project
------------------

The following tree shows an example project with some explanations what is going on.
The project is a comprehensive project with database access via JPA, database migrations
and advanced features like filters and argument extractors.

<pre class="prettyprint">
&#x251c;&#x2500;&#x2500; pom.xml                                     // Instructions about dependencies and the build (Maven)
&#x2514;&#x2500;&#x2500; src
    &#x251c;&#x2500;&#x2500; main
    &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; META-INF
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; persistence.xml             // Contains informations how to access databases via JPA
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; assets                          // Static assets of your application
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; css
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; custom.css
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; conf 
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; Module.java                 // Dependency injection definitions via Guice (Optional) 
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; Routes.java                 // Contains all routes of your application in one location
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ServletModule.java          // Integration of arbitrary servlet filters and mappings (Optional)
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; StartupActions.java         // Customization of application startup (Optional)
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; application.conf            // Configuration for test dev and production mode
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; messages.properties         // 18n messages
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; messages_de.properties
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; controllers                     // Controllers will handle the actual request and do something
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ApiController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ApplicationController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticleController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; LoginLogoutController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; dao                             // Database access via DAO objects and not in the controller
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticleDao.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; SetupDao.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; UserDao.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; db                              // Database migrations when dealing with RDBMS (Flyway)
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; migration
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; V1__.sql
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; V2__.sql
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ehcache.xml                     // Configuration for ehcache
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; etc
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; LoggedInUser.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; LoggedInUserExtractor.java  // Argument extractors for controller methods
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; filters
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; LoggerFilter.java           // Filter to filter the request in the controller
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; logback.xml                     // Logging configuration via logback / slf4j
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; models                          // Some models that map to your relational database
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; Article.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticleDto.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticlesDto.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; User.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; views                           // html views - always map to a controller and a method
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApplicationController
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; index.ftl.html          // Maps to controller "ApplicationController" and method "index"
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; setup.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ArticleController
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; articleNew.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; articleShow.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; LoginLogoutController
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; login.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; logout.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; layout
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; defaultLayout.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; footer.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; header.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; system                      // Error html views. Can be customized to output custom error pages
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;         &#x251c;&#x2500;&#x2500; 403forbidden.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;         &#x2514;&#x2500;&#x2500; 404notFound.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; resources
    &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; webapp
    &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; WEB-INF
    &#x2502;&#x00a0;&#x00a0;         &#x2514;&#x2500;&#x2500; web.xml                    // Needed for servlet containers to start up Ninja
    &#x2514;&#x2500;&#x2500; test
        &#x251c;&#x2500;&#x2500; java
        &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; controllers                    // Different tests for your application
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApiControllerDocTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApiControllerDocTesterTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApiControllerMockTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApiControllerTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApplicationControllerFluentLeniumTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApplicationControllerTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; LoginLogoutControllerTest.java
        &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; RoutesTest.java
        &#x2514;&#x2500;&#x2500; resources
            &#x2514;&#x2500;&#x2500; test_for_upload.txt
</pre>

