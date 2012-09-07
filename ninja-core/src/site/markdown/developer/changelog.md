Version 1.X
===========

Version 1.0.4
=============
 
 * Bump to Gson 2.2.2
 * Fixed encoding issue in TestBrowser that showed up on some english macs


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