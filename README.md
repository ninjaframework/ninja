ninja - a web framework
=======================
GOALS:
- provide a rails, play like java only framework
- stateless to the max, no server side session
- productivity is king - reloads must be ultra fast
- should run on any war container (tomcat, jetty)
- should run on gae, heroku, cloudbees
- formidable test support + integration selenium tests
- configuratino of complete app with guice
- full eclipse support
- full maven integration
- don't use magic
-- no bytecode enhancement
-- no reflection (or only a minimal amount)
- not dependend on serlvet api. should eg also run on netty
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

TODOS:
- filter for nice error stacktraces in TEST mode...
- .gitignore when creating new maven projet
- maven project that generates a stub and everything to get started...
- ajax xhr support
- memcache support (rewire to gae)
- xsrf token in forms
- client side session support
- flash cookie support
- filling of freemarker templates with content
- caching of freemarker templates
- router flexible routes todo (with {})
- default error handling 404 => 505 etc...
- how to integrate plugins
-- especially when they provide views
- context negotiation? needed?
-- serve Ò/me/{id}Ó, with when content(Òjson xyzÓ);
- login / logout support built in...
- doctest support...
-- testing and generating a documentation must be part of the framework...
-- documentation of written restful api for instance must be a breeze.
- internationalisation properties AND templates...

- freemarker example with parent und included refinement... => reuse of html É. => ending should be ftl.html
=> http://richardbarabe.wordpress.com/category/web-development/template-system/freemarker/


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
- etag support?