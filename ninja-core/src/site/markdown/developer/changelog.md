Version XXXX
============

* 2013-11-05 New NinjaDaoTestBase class to facilitate test your DAOs with a real database (emiguelt)
* 2013-11-06 Set general log level of archetypes and integration tests to "info" (ra)

Version 2.1.0
=============

* 2013-10-21 Adding JsonP support (an)
* 2013-10-30 Fixed PostOfficeConstants spelling mistake https://github.com/ninjaframework/ninja/issues/117 (ra+socket70)
* 2013-10-30 Improvements of Validation interface - added hasBeanViolations() (an)

Version 2.0.1
=============

 * 2013-09-27 Removed bogus System.setProperty(test...) in NinjaJetty.java (ra)
 * 2013-09-27 Some documentation fixes (ra)
 * 2013-09-27 ninja-core changed from junit-dep (deprecated) to junit archetypeid (ra)
 * 2013-09-28 Added log4j-over-slf4j - Needed for EhCache which logs via log4j by default. (ra)
 * 2013-09-30 Possibility to change context path for standalone mode (an)


Version 2.0.0
=============

 * 2013-08-16 Improved Json and Xml rendering / Switch from Gson to Jackson. (ra)
 * 2013-08-25 Added facility to render simple strings to the outputstream without
              invoking templating engine. (sojin, ra)
 * 2013-08-28 Guice injector now started in mode production by default. In that course
              also fixed some hidden problems with JPA shutdown and eh cache registration (samliard, ra)
 * 2013-08-28 Bump to latest javax.el interfaces and implementation (needed for validation) (ra)
 * 2013-08-29 Rollback of hibernate-validation to 4.3.1.Final - because version 5.0.1.Final not compatible with GAE (ra)
 * 2013-09-01 Bugfix - static assets should not handle any flash or session scopes
               https://github.com/ninjaframework/ninja/issues/109 (Sojin, ra).
 * 2013-09-02 Bump to jetty 9 for testing and running of applications (ra).
 * 2013-09-02 AssetsController now supports to include webjars aka META-INF/resources folders (ra).
 * 2013-09-05 Bump to 1.3.0 of fluido in ninjaframework.org site (ra).
 * 2013-09-12 Added https://code.google.com/p/error-prone/ to prevent bugs at compile time. (ra)
 * 2013-09-12 Now using module.setDefaultUseWrapper(false); in xml rednering 
              (see also https://github.com/FasterXML/jackson-dataformat-xml) (ra)
 * 2013-09-13 Improved documentation (testing, controller) (ra)
 * 2013-09-14 Added blog integration test to JPA (ra)
 * 2013-09-14 New default JPA blog archetype (ra)
 * 2013-09-16 Multiple fixes for maven archetype (ra) 
 * 2013-09-17 Fixed misnamed logging configuration files (logging.xml => logback.xml) (ra)

Version 1.6.0
=============

 * 2013-07-17 JPA support went into ninja-core (ra)
 * 2013-07-17 Added a JPA demo acting as integration test and an archetype for JPA (ra)
 * 2013-07-26 Added Flyway integration (database migration tool) (ra)
 * 2013-07-28 Bugfix for template loading in dev mode (modules' templates not loaded) (ra)
 * 2013-07-30 Changed accesses of the flash cookie from underscore syntax into "." syntax.
              This is now much more consistent with the general way we access stuff 
              inside any ftl.html file. (ra)
 * 2013-07-30 Direct streaming from assets folder without jetty reload in dev mode. Cool
              for developing js apps inside the assets folder. (ra)
 * 2013-08-05 Fixed bug in OPTION method of routes. (ra)
 * 2013-08-06 Switch to logback - exlusion of commons-logging in pom (ra)
 * 2013-08-06 Important security fix imported from Play:
              https://github.com/playframework/play1/commit/dce07610f1400a9c031753fc413a324e0a74c4c1 (ra)
 * 2013-08-08 Added default logging.xml config and enforcer plugin to make sure we do have
              commons logging on the classpath (ra)
 * 2013-08-08 Added .gitignore to archetypes (demo and jpa) (ra)
 
Version 1.5.1
=============

 * 2013-07-15 Support for skipping rendering of HttpBody via NoHttpBody class (useful in redirects eg) (makotan, pthum, ra)
 * 2013-07-16 Support to show the user of Ninja the version of the framework.
              Embedded in the splash screen (Ninja logo) at the beginning. (ra)
 * 2013-07-17 Ninja now logs the mode in which the framework runs currently. (ra)

Version 1.5
===========
 
 * bump to devbliss.doctest 0.6.5 (ra)
 * free marker templates changes no longer cause ninja restart (makotan)
 * freemarker templates performance optimizations (ra)
 * some improvements to harden thread-safe behavior of ninja (ra)
 * Pinned ninja-core-archetype to java 1.7 (ra)
 * Fixed a bug in TestServer that caused context not to shut down properly (ra)
 * Added Cache implementation (EhCache and Memcache) (ra)
 * Added support for injecting HttpServletRequest and HttpServletResponse into controller method (ra, Tristan)
 * Bump to latest libraries (hibernate-validator 5.0.1) (ra)
 * Ninja now uses Java 1.7 (ra)


Version 1.4.4
=============

 * Added possibility to get the contextpath in templates (pthum)
 * Fixed bug when translating flash scope i18n (pthum, ra)
 * Added better i18n facilities to html tempting (ra)
 * Added possibility to get the contextpath in templates (pthum)
 * Added reverse routing facilities (ra)
 * Extended reverse routing facilities with support for query parameters (ra)
 * Added support for a more much more convenient use of Result.render(...) (pthum, ra)

Version 1.4.3
=============

 * Added an easy way to access the Session-Cookie-Values in every template (pthum)
 * Added support for creation of application.secret (ra)
 * Added security guide covering sessions (ra)
 * Fixed bug in ninja-core-archetype (ra)
 

Version 1.4.2
=============

 * Removed deprecated and confusing methods in Results.java. Now we have only .json() .html() and so on.
   https://github.com/reyez/ninja/issues/92#issuecomment-18015522
 * Fixed content negotiation bug https://github.com/reyez/ninja/issues/83
 * Made bodyparserengine extensible and faster by using map for lookup.
   
   
Version 1.4.1
=============

 * Archetype wrongly referenced a snapshot version (seratch)

Version 1.4
===========

 * Libraries of ninja-core updated to latest versions
 * Added Optional (guava) support to Messages and Lang
 * Added modules documentation page (ra).
 * Replaced potentially harmful SimpleDateFormatter with JodaTime. SDF is not thread safe... (ra)
 * Better printing of numbers in Freemarker Templating Engine (See also: http://freemarker.sourceforge.net/docs/app_faq.html#faq_number_grouping)
 * Better error handling for templating exceptions (ra)
 * Brand new maven archetype ninja-core-demo-archetype for really simple project generation... (ra)


Version 1.3
===========

 * Added _attributes_ to `Context`, updated Request scope documentation (tbroyer)
 * Headers can be multivalued, added `getParameterValues` for multivalued parameters (tbroyer)
 * Docs: Added documentation for WrappedContext / ArgumentMatchers aka Request scope (ra)
 * Docs: Added twitter account (ra) 
 * Separation of servlet support (module ninja-servlet) from main ninja framework (tbroyer)
 * replaced default freemarker templating engine with freemarker-gae for appengine compatibility (ra)


Version 1.2.1
=============

 * Fix for byte[] get a forced octet stream when content type is already explicitly set (qubic)
 * tiny fix typos in servlet bridge documentation (ra)
 * bugfix => TestServer in NinjaTest was in wrong mode by default (ra)


Version 1.2
===========

 * Fixes in Validation (phil)
 * Validation for controller parameter fields as well as validation for injected beans (phil)
 * Servlet-Ninja bridge - support for loading Servlet Filters and Servlets using ServletModule (zoza)
 * Cleaner cookie handling (removed dependencies servlet cookies) (ra)
 * Support for force-setting default language via NINJA_LANG cookie (ra)
 * Messages stuff moved from Lang to Message (ra)
 * ETag and http-cache header support for static assets (AssetsController) (ra).
 * Context now supports getMethod function to know the http request method from the context (ra).


Version 1.1
===========
 
 * Bugfix: explicitly set content type of result keeps untouched by ResultHandler now (roughy/henning)
 * Added HttpOnly option for the session cookie (roughy)
 * Added support for simple parsing of post forms (aka application/x-www-form-urlencoded) (zoza)
 * Added support for simple rendering of objects in html templates (reyez)

 
Version 1.0.8
=============
 
 * Fixed bug + test with flash scope: https://github.com/reyez/ninja/issues/70 (zoza / ra)

Version 1.0.7
=============

 * Support XML payload parsing on Content-Type: application/xml
 * Fix for multiple values in request "Accept-Language" header

Version 1.0.6
=============

 * Better way to retrieve constraint violations in controller methods (https://github.com/reyez/ninja/pull/58)


Version 1.0.5
=============

 * Fix possible NPE in field validation of controller methods.


Version 1.0.4
=============
 
 * Bump to Gson 2.2.2
 * Fixed encoding issue in TestBrowser that showed up on some English macs


Version 1.0.3
=============
 
 * Added better sending of Json payload to NinjaTestBrowser
 * Utf-8 now default character set when client does not specify correct Content-type
 * Added tests to context
 * Fixed bug where incoming payload was not parsed by correct bodyParser
 
 
Version 1.0.2
=============
 
 * cleanup with develop and master => now only develop active
 * added missing license headers
 * fixed some spelling errors


Version 1.0.1
=============

 * 2012-09-05 Better default http cache-control header handling (no-caching by default).
 * 2012-08-27 Support for better testing whether routes are handled by framework (NinjaRoutesTest).
