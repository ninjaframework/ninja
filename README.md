     _______  .___ _______        ____.  _____   
     \      \ |   |\      \      |    | /  _  \  
     /   |   \|   |/   |   \     |    |/  /_\  \ 
    /    |    \   /    |    \/\__|    /    |    \
    \____|__  /___\____|__  /\________\____|__  /
         web\/framework   \/                  \/ 
        


Ninja - web framework
=====================
CI at: https://buildhive.cloudbees.com/job/reyez/job/ninja/


Quickstart for users
--------------------
Check out the project and run "mvn jetty:run" inside ninja-demo-application.
This will start the demo application and you are ready to go.

Then check out the source code inside ninja-demo-application. There is a lot of
documentation inside the classes.



GOALS
-----
- Be the best! Make the best web framework for the java language
- Scalability! Ninja is state-less. Sessions are client-side.
- Productivity! Making changes must be ultrafast.
- Testability! Excellent test support + integration selenium tests.

More specifically:
- Tooling! Ninja is a 1A IDE citizen (Eclipse and Intellij love us).
- Dependency Injection! Yes. Ninja will is built on DI. For many many good reasons.
- Compatability! Ninja will run on any war container (tomcat, jetty) but also on netty. And also on any provider. From App Engine to Heroku to Cloudbees.
- Simple management! We are using maven to get dependencies and much more right.
- No magic! We won't use bytecode enhancement.

NON-GOALS v1.0
--------------
- Async operations. We don't want to focus on async operations for v1.0. While we have a good
 idea how we can make async work really well it is not the focus for v.1.0


Next big steps
--------------

For version 0.4 - "html forms support" 
- login logout module for security (casino)
-- check methods in route if they are okay
-- make submodules work in casino
-- map assets of submodules to main module...
-- Application class? that binds routes?
-- something like global? => also add filters here...
-- => also add plugins here...
-- what does the routes binding? => maybe done in dispatcher?
- demo app extending via forms validation and xsrf support
-- xsrf suppor should be in login logout module
-- forms and so on should also be in login logout module...
- maybe reverse routing in templates...

For version 0.5 - "test subsystem support"
- isdev isprod istest modes integrated into app...
- testsystem is there
- DONE fluentlenium maybe
- DONE selenium testcases
- DONE integration test and tests run nicely in maven
- support for verify tests in mockito so that frontend tests become ultrafast...

For version 0.6 - "internationalization support"
- make sure properties support works

For version 0.7 - Tomcat, Jetty, Heroku, Cloudbees, App Engine support
- check if stuff runs on third party hosts...
- make sure mappings are correct in all containers.
- Improve interoperability (eg /assets, /views and so on...)


For version 0.8
- Performance
- appengine support
- mongo support
- sql support via mybatis
- migrations support (carbon5 or mybatis migrations)

For version 0.9
- gwt bridge

For version 1.0
- install script to install maven AND ninja
- maven archetype for ninja


For 2.0
- long polling connections (async support)


To discuss:
- should sessions be active for a finite time, or should they refresh themselves automatically?


Already done:
-------------
version 0.1
- freemarker example with parent and included refinement... => reuse of html. => ending should be ftl.html
=> http://richardbarabe.wordpress.com/category/web-development/template-system/freemarker/
- simple freemarker templating working in /views/controller/index.ftl.html
- simple json support via renderJson(object) in context
- controller works
- simple router configuration works
- displaying of pages works
- auto reloading feature of jetty and guice works fast!
- guice like routes filter...
- routes file, just with a java router...
- filling of freemarker templates with content
- router flexible routes todo (with {})
-- serve "/me/{id}", with when content("json xyz");
- how to develop modules in an efficient way
==> http://maven.apache.org/plugins/maven-war-plugin/overlays.html ninja-demo-module
===> use ln for that...
- default error handling 404 => 505 etc...
==> using status(HTTP_STATUS) on context


For version 0.2 - "basics"
- DONE: Augment the router with support for catchall routes and routes that contain regex parameters
- DONE: Complete the AssetsController
-- DONE (right now managed by container) supply correct response codes for files (txt/css, pictures, js etc pp)
- DONE integrate bootstrap and make demo application nice
-- DONE prettify.css from googlecode for code...
- DONE: make templating system modular (ftl optional, as well as jsons) DONE
- DONE put post parser

For version 0.3 - "security"
- session cookie there with signing
-- DONE todo: extensible testing...
-- DONE cookie gets only transferred when changed
-- DONE app.conf file with secret, expiry and so on...
- flash cookie support
-- DONE test: keep
-- DONE test: now
-- DONE test: clear
-- DONE: basic support

GENERAL:
- DONE... conf file with secure token and or other stuff...
- DONE cookies: expires support
- DONE secure cooke support / https support
- DONE transmit cookie always on/off
- DONE showcase application with flash cookie



TODOS
-----
- oauth support
- ws support
- global filters => ninja router?
- pdf rendering?
- subdomains in ninja?
- results as "results object?
- call static methods in an even better way...
- bootstrap integration into nija-demo-application
- form validation and bootstrap post etc
- less and external minimizer...
- test test test
- logging subsystem working
- generic renderer for output (render xyz)...
- filter for nice error stacktraces in TEST mode...
- .gitignore when creating new maven projet
- different modes - TEST, DEV, PROD ???
- maven project that generates a stub and everything to get started...
- ajax xhr support
- memcache support (rewire to gae)
- xsrf token in forms
- client side session support
- flash cookie support
- caching of freemarker templates
- how to integrate plugins
-- especially when they provide views
-- need for a plugins.java? or is configuration.java okay?
- Routes.java => can it be bound automatically in default location? conf.Routes?
- caching of routes matching and binding to controllers for faster stuff...
- support for 3rd party compiler plugins
=> closure
=> dart
- context negotiation? needed?
- login / logout support built in...
- doctest support...
-- testing and generating a documentation must be part of the framework...
-- documentation of written restful api for instance must be a breeze.
- internationalization properties AND templates...
-- make sure i18n is derived from request language


- scheduled jobs
-- gae? with .xml config?


- built in security mechanism... salted bcrypt...
- mailer plugin
-- with different implementations...
- db plugin
-- .......

- mongodb plugin
-- mongo jackson mapper

- validation plugin
-- => how to validate forms?

- websocket demonstration (netty, etcpp)

- async http client...

- comet implementation?
- what about threads and long polling io... is that efficient?

- benchmarking of framework
- itaree pattern? thread consumption on jetty, tomcat, netty?
- etag support?



DONE
----
- freemarker example with parent and included refinement... => reuse of html. => ending should be ftl.html
=> http://richardbarabe.wordpress.com/category/web-development/template-system/freemarker/
- simple freemarker templating working in /views/controller/index.ftl.html
- simple json support via renderJson(object) in context
- controller works
- simple router configuration works
- displaying of pages works
- auto reloading feature of jetty and guice works fast!
- guice like routes filter...
- routes file, just with a java router...
- filling of freemarker templates with content
- router flexible routes todo (with {})
-- serve "/me/{id}", with when content("json xyz");
- how to develop modules in an efficient way
==> http://maven.apache.org/plugins/maven-war-plugin/overlays.html ninja-demo-module
===> use ln for that...
- default error handling 404 => 505 etc...
==> using status(HTTP_STATUS) on context



Random issues for v1.0
----------------------
- regex inside parameters like in play1: GET   /files/{<[a-z0-9/\.]*>name}               Application.download(name)
- Optional<xyz> for get parameter in context => not null
- remove dependency on servlet (httprequest, response, cookie etcpp)
- sessionexpiry => prolong session when hit server or only when valid for 2 weeks in total (google way)...
- init methds should become assisted injects...
- implement session that cannot be read by javascript (servlet 3.0 etc pp)
- archetype for simple project generation out of the box
- Make sure Ninja (and the produced war) runs on tomcat, jetty app engine...
- working security subsystem (client side session crypto key'ed)
- allow routes to save content type they are responsible for...
