Version CURRENT
===============

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
