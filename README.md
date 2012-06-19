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
- provide a rails, play-framework like java only framework
- stateless to the max, no server side session
- productivity is king - reloads must be ultra fast
- should run on any war container (tomcat, jetty), but also on netty (async)
- should run on gae, heroku, cloudbees
- formidable test support + integration selenium tests
- configuration of complete app with guice
- full eclipse support
- full maven integration
- don't use magic
-- no bytecode enhancement
-- no reflection (or only a minimal amount)
- not dependent on serlvet api. should eg also run on netty
- archetype for simple project generation out of the box
- working security subsystem (client side session crypto key'ed)
- framework completely interfaced for supersimple and fast mocked testing



Next big steps
--------------

For version 0.2 - "basics"
- DONE: Augment the router with support for catchall routes and routes that contain regex parameters
- Complete the AssetsController
-- supply correct response codes for files (txt/css, pictures, js etc pp)
- integrate bootstrap and make demo application nice
-- prettify.css from googlecode for code...
- DONE: make templating system modular (ftl optional, as well as jsons) DONE

For version 0.3 - "security"
- session cookie there with signing...
- flash cookie support there
- login logout module there

For version 0.4 - "html forms support" 
- demo app extending via forms validation and xsrf support
- maybe reverse routing in templates...

For version 0.5 - "test subsystem support"
- isdev isprod istest modes integrated into app...
- testsystem is there
- fluentlenium maybe
- selenium testcases
- integration test and tests run nicely in maven
- support for verify tests in mockito so that frontend tests become ultrafast...

For version 0.6 - "internationalization support"
- make sure properties support works

For version 0.7 - Tomcat, Jetty, Heroku, Cloudbees, App Engine support
- check if stuff runs on third party hosts...
- make sure mappings are correct in all containers.
- Improve interoperability (eg /assets, /views and so on...)


For version 0.8
- Performance


For version 0.9
- gwt bridge


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


TODOS
-----
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


