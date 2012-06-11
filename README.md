ninja - a web framework
=======================
GOALS:
- provide a rails, play like java only framework
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
- working security subsystem

DONE:
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

TODOS:
- how to develop modules in an efficient way
==> http://maven.apache.org/plugins/maven-war-plugin/overlays.html ninja-demo-module
- test test test
- default error handling 404 => 505 etc...
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
- Routes.java => can it be bound automatically in default location? conf.Rotues?
- support for 3rd party compiler plugns
=> closure
=> dart
- context negotiation? needed?
- login / logout support built in...
- doctest support...
-- testing and generating a documentation must be part of the framework...
-- documentation of written restful api for instance must be a breeze.
- internationalization properties AND templates...
-- make sure i18n is derived from request language

- freemarker example with parent and included refinement... => reuse of html. => ending should be ftl.html
=> http://richardbarabe.wordpress.com/category/web-development/template-system/freemarker/
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




