The anatomy of a Ninja application
===================================

<pre class="prettyprint">
&#x251c;&#x2500;&#x2500; pom.xml
&#x2514;&#x2500;&#x2500; src
    &#x251c;&#x2500;&#x2500; main
    &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; META-INF
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; persistence.xml
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; assets
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; css
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; custom.css
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; conf
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; Module.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; Routes.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ServletModule.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; StartupActions.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; application.conf
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; messages.properties
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; messages_de.properties
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; controllers
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ApiController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ApplicationController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticleController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; LoginLogoutController.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; dao
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticleDao.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; SetupDao.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; UserDao.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; db
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; migration
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; V1__.sql
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; V2__.sql
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ehcache.xml
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; etc
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; LoggedInUser.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; LoggedInUserExtractor.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; filters
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; LoggerFilter.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; logback.xml
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; models
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; Article.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticleDto.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; ArticlesDto.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; User.java
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; views
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x251c;&#x2500;&#x2500; ApplicationController
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; index.ftl.html
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
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; system
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;         &#x251c;&#x2500;&#x2500; 403forbidden.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x2502;&#x00a0;&#x00a0;         &#x2514;&#x2500;&#x2500; 404notFound.ftl.html
    &#x2502;&#x00a0;&#x00a0; &#x251c;&#x2500;&#x2500; resources
    &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; webapp
    &#x2502;&#x00a0;&#x00a0;     &#x2514;&#x2500;&#x2500; WEB-INF
    &#x2502;&#x00a0;&#x00a0;         &#x2514;&#x2500;&#x2500; web.xml
    &#x2514;&#x2500;&#x2500; test
        &#x251c;&#x2500;&#x2500; java
        &#x2502;&#x00a0;&#x00a0; &#x2514;&#x2500;&#x2500; controllers
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